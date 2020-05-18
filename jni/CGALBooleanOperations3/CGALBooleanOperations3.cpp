#include <fstream>
#include <iostream>
//#include <boost/foreach.hpp>

#include <CGAL/Exact_predicates_exact_constructions_kernel.h>
#include <CGAL/Polyhedron_3.h>
#include <CGAL/Nef_polyhedron_3.h>
//#include <CGAL/iterator.h>
//#include <CGAL/squared_distance_3.h>

typedef CGAL::Exact_predicates_exact_constructions_kernel       Kernel;
typedef CGAL::Polyhedron_3<Kernel>                              Polyhedron;
typedef CGAL::Nef_polyhedron_3<Kernel>                          Nef_polyhedron;
typedef Polyhedron::HalfedgeDS                                  HalfedgeDS;
//typedef Polyhedron::Vertex_iterator                             Vertex_iterator;
//typedef Polyhedron::Halfedge_iterator                           Halfedge_iterator;
typedef Polyhedron::Facet_iterator                              Facet_iterator;
typedef Kernel::Point_3                                         Point_3;

//typedef Polyhedron::Halfedge_handle                             Halfedge_handle;
//typedef Polyhedron::Facet_handle                                Facet_handle;
//typedef Polyhedron::Vertex_handle                               Vertex_handle;
typedef Polyhedron::Halfedge_around_facet_circulator            Halfedge_facet_circulator;
//typedef CGAL::Inverse_index<Vertex_iterator>                    Index;

#include "de_hfkbremen_gewebe_CGALBooleanOperations3.h"

using namespace std;

const bool M_DEBUG              = false;
const int NOT_FOUND             = -1;
const int INTERSECTION          = 0;
const int JOIN                  = 1;
const int DIFFERENCE            = 2;
const int SYMMETRIC_DIFFERENCE   = 3;


/**
 * @TODO
 *
 * - read CGAL doc : http://doc.cgal.org/latest/Polyhedron/classCGAL_1_1Polyhedron__3.html + http://doc.cgal.org/latest/Polyhedron/index.html
 * - check polyhedron + builder : http://doc.cgal.org/latest/Polyhedron/classCGAL_1_1Polyhedron__incremental__builder__3.html
 * - read on traits : http://doc.cgal.org/latest/Manual/devman_traits_classes.html
 * - look at example code in CGAL
 *
 */

// from http://cgal-discuss.949826.n4.nabble.com/attachment/4661367/0/union_and_intersection.cpp
// from http://jamesgregson.blogspot.de/2012/05/example-code-for-building.html
// from https://github.com/yusuketomoto/ofxCGAL/blob/master/src/ofxCGALBooleanOp.cpp

template <class HDS>
class PolyhedronBuilder : public CGAL::Modifier_base<HDS> {
public:
    
    vector<Point_3> & vertices;
    vector<int> & indices;
    const float kPointScale = 1.0f;
    
    PolyhedronBuilder(vector<Point_3> & _vertices, vector<int> & _indices) : vertices(_vertices), indices(_indices) {}
    
    void operator()(HDS & hds) {
        
        CGAL::Polyhedron_incremental_builder_3<HDS> mBuilder(hds, true);
        
        int numOfVertices = vertices.size();
        int numOfIndices = indices.size();
        
        mBuilder.begin_surface(numOfVertices, numOfIndices / 3);
        
        typedef typename HDS::Vertex Vertex;
        typedef typename Vertex::Point Point;
        
        for(int i=0; i<vertices.size(); i++) {
            mBuilder.add_vertex(Point(vertices[i].x(),
                                      vertices[i].y(),
                                      vertices[i].z()));
        }
        
        
        for(int i=0; i<numOfIndices; i+=3) {
            mBuilder.begin_facet();
            mBuilder.add_vertex_to_facet(indices[i + 0]);
            mBuilder.add_vertex_to_facet(indices[i + 1]);
            mBuilder.add_vertex_to_facet(indices[i + 2]);
            mBuilder.end_facet();
        }
        
        mBuilder.end_surface();
    }
};

bool near(Point_3 v0, Point_3 v1, double pMinDistance) {
    if (pMinDistance == 0) {
        return v0.x() == v1.x() && v0.y() == v1.y() && v0.z() == v1.z();
    } else {
        double x = CGAL::to_double(v0.x() - v1.x());
        double y = CGAL::to_double(v0.y() - v1.y());
        double z = CGAL::to_double(v0.z() - v1.z());
        double mDistance = sqrt(x*x+y*y+z*z);
        return mDistance < pMinDistance;
    }
}

int findVertexInIndexList(vector<Point_3> vertices_opt, Point_3 v, float pEpsilon) {
    for (int j = 0; j < vertices_opt.size(); j++) {
        Point_3 vo = vertices_opt[j];
        bool isNear = near(v, vo, pEpsilon);
        if (isNear) {
            return j;
        }
    }
    return NOT_FOUND;
}

void optimizeIndexList(vector<Point_3> & coords_raw, vector<Point_3> & coords_opt, vector<int> & indices_opt) {
    int mIndexCounter = 0;
    for (int i=0; i<coords_raw.size(); i++) {
        Point_3 v = coords_raw[i];
        int mExistingIndex = findVertexInIndexList(coords_opt, v, 0.0f);
        if (mExistingIndex == NOT_FOUND) {
            coords_opt.push_back(v);
            indices_opt.push_back(mIndexCounter);
            mIndexCounter++;
        } else {
            indices_opt.push_back(mExistingIndex);
        }
    }
}


void convertToPolyhedron(vector<Point_3> vertices, vector<int> indices, Polyhedron & polyhedron) {
    PolyhedronBuilder<HalfedgeDS> builder(vertices, indices);
    polyhedron.delegate(builder);
}

void extract_vertices(vector<Point_3> & vertices, JNIEnv * env, jfloatArray & j_coords) {
    jboolean iscopy;
    jfloat* jcoords_A_array = env->GetFloatArrayElements(j_coords, &iscopy);
    const int mNumberOfPoints_A = (env->GetArrayLength(j_coords)) / 3;
    
    if (M_DEBUG) cout << "--- A has points: " << mNumberOfPoints_A << endl;
    
    for(int i=0; i < mNumberOfPoints_A; i++) {
        double x = (double)*jcoords_A_array;
        jcoords_A_array++;
        double y = (double)*jcoords_A_array;
        jcoords_A_array++;
        double z = (double)*jcoords_A_array;
        jcoords_A_array++;
        vertices.push_back(Point_3(x, y, z));
    }
}

JNIEXPORT jfloatArray JNICALL Java_de_hfkbremen_gewebe_CGALBooleanOperations3_boolean_1operation
(JNIEnv * env, jobject jobj, jint class_type, jfloatArray coords_A_array, jfloatArray coords_B_array )
{
    if (M_DEBUG) cout << "--- boolean_operation" << endl;
    
    /* evaluate command */

    int mBooleanOperation;
    if ( class_type == JOIN ) {
        if (M_DEBUG) cout << "--- type: JOIN" << endl;
        mBooleanOperation = JOIN;
    } else if ( class_type == INTERSECTION ) {
        if (M_DEBUG) cout << "--- type: INTERSECTION" << endl;
        mBooleanOperation = INTERSECTION;
    } else if ( class_type == DIFFERENCE ) {
        if (M_DEBUG) cout << "--- type: DIFFERENCE" << endl;
        mBooleanOperation = DIFFERENCE;
    } else if ( class_type == SYMMETRIC_DIFFERENCE ) {
        if (M_DEBUG) cout << "--- type: SYMMETRIC_DIFFERENCE" << endl;
        mBooleanOperation = SYMMETRIC_DIFFERENCE;
    } else {
        if (M_DEBUG) cout << "--- type: (DEFAULT) ?: " << class_type << endl;
        mBooleanOperation = INTERSECTION;
    }

    
    /* ------ */
    
    vector<Point_3> vertices_raw_A;
    extract_vertices(vertices_raw_A, env, coords_A_array);
    if (M_DEBUG) cout << "coords_raw_A: " << vertices_raw_A.size() << endl;
    
    vector<Point_3> vertices_raw_B;
    extract_vertices(vertices_raw_B, env, coords_B_array);
    
    /* build polyhedrons */
    
    Polyhedron polyhedronA;
    vector<Point_3> vertices_opt_A;
    vector<int> indices_opt_A;
    optimizeIndexList(vertices_raw_A, vertices_opt_A, indices_opt_A);
    convertToPolyhedron(vertices_opt_A, indices_opt_A, polyhedronA);
    
    Polyhedron polyhedronB;
    vector<Point_3> vertices_opt_B;
    vector<int> indices_opt_B;
    optimizeIndexList(vertices_raw_B, vertices_opt_B, indices_opt_B);
    convertToPolyhedron(vertices_opt_B, indices_opt_B, polyhedronB);
    
    /* check validity of polyhedra */
    
    if (!polyhedronA.is_pure_triangle() || !polyhedronB.is_pure_triangle()){
        cerr << "Inputs polyhedra must be triangulated." << std::endl;
        cerr << "A.is_pure_triangle() " << polyhedronA.is_pure_triangle() << endl;
        cerr << "B.is_pure_triangle() " << polyhedronB.is_pure_triangle() << endl;
    }
    
    if (!polyhedronA.size_of_vertices() || !polyhedronB.size_of_vertices()){
        cerr << "Inputs polyhedra must not be empty." << std::endl;
    }
    
    if (!polyhedronA.is_valid() || !polyhedronB.is_valid()){
        cerr << "Inputs polyhedra must be valid." << std::endl;
    }
    
    if(!polyhedronA.is_closed()) {
        cerr << "input mesh A is not closed." << endl;
    }
    
    if(!polyhedronB.is_closed()) {
        cerr << "input mesh B is not closed." << endl;
    }
    
    Nef_polyhedron Nef_A(polyhedronA);
    Nef_polyhedron Nef_B(polyhedronB);
    
    if (M_DEBUG) cout << "Nef_A (op) Nef_B" << endl;
    
    /*
     operator *= :: intersection
     operator += :: join
     operator -= :: difference
     operator ^= :: symmetric_difference
     */

    if (mBooleanOperation == INTERSECTION) {
        Nef_A *= Nef_B;
    } else if (mBooleanOperation == JOIN) {
        Nef_A += Nef_B;
    } else if (mBooleanOperation == DIFFERENCE) {
        Nef_A -= Nef_B;
    } else if (mBooleanOperation == SYMMETRIC_DIFFERENCE) {
        Nef_A ^= Nef_B;
    }

    if (M_DEBUG) cout << "Nef_A.is_simple(): " << Nef_A.is_simple() << endl;
    
    Polyhedron polyhedron_result;
    Nef_A.convert_to_polyhedron(polyhedron_result);
    if(Nef_A.is_simple()) {
        Nef_A.convert_to_Polyhedron(polyhedron_result);
    } else {
        cerr << "analyze/process n1 and do something..." << endl;
    }
    
    /* build jfloatarray */
    
    const int mNumberOfMeshData = distance(polyhedron_result.facets_begin(), polyhedron_result.facets_end()) * 3 * 3;
    jfloat mMeshData[mNumberOfMeshData];
    if (M_DEBUG) cout << "mNumberOfMeshData: " << mNumberOfMeshData << endl;
    
    int i = 0;
    for(Facet_iterator f = polyhedron_result.facets_begin();
        f != polyhedron_result.facets_end();
        ++f )
    {
        Halfedge_facet_circulator j = f->facet_begin();
        //        // Facets in polyhedral surfaces are at least triangles.
        //        CGAL_assertion( CGAL::circulator_size(j) >= 3);
        //        cout << CGAL::circulator_size(j) << ' ';
        do {
            if (M_DEBUG) cout << j->vertex()->point() << endl;
            mMeshData[i] = (float)CGAL::to_double(j->vertex()->point().x());
            i++;
            mMeshData[i] = (float)CGAL::to_double(j->vertex()->point().y());
            i++;
            mMeshData[i] = (float)CGAL::to_double(j->vertex()->point().z());
            i++;
        } while ( ++j != f->facet_begin());
    }
    
    jfloatArray mResultMeshData;
    mResultMeshData = env->NewFloatArray(mNumberOfMeshData);
    env->SetFloatArrayRegion(mResultMeshData, 0, mNumberOfMeshData, mMeshData);
    
    return mResultMeshData;
}




//    int size = std::distance(C.vertices_begin(), C.vertices_end());
//    cout << "number of points : " << size << endl;
//
//
//    //    if (/* DISABLES CODE */ (false)) {
//    cout << "coref ... ";
//
//    coref(new_A, new_B,
//          polyline_output,
//          std::back_inserter(result),
//          operation_type);
//
//    cout << "done." << endl;
//
//    //    }
//
//    //    int union_index = result[0].second == Corefinement::Join_tag ? 0:1;
//    //    int intersection_index = (union_index+1)%2;
//    //
//    //    cout << union_index << endl;
//    //    cout << intersection_index << endl;
//
//    //    cout << *( result[union_index].first );
//    //    cout << endl;
//    //    cout << *( result[intersection_index].first );
//
//    //    cout << *( result[0].first );
//
//    //    /* return results */
//
//    //    CGAL::Vertex_iterator begin = result[union_index].first->vertices_begin();
//    //    CGAL::Vertex_iterator end = result[union_index].first->vertices_end();
//
//    //    std::list<Vertex>::iterator itb = result[union_index]->vertices_begin();
//    //    for(; result[union_index].first->vertices_begin() != result[union_index].first->vertices_end(); itb++) {
//    //
//    //    }

//    /* populate vectors */
//    {
//        jfloat* jcoords_A_array = env->GetFloatArrayElements(coords_A_array, &iscopy);
//        const int mNumberOfPoints_A = (env->GetArrayLength(coords_A_array)) / 3;
//
//        if (M_DEBUG) cout << "--- A has points: " << mNumberOfPoints_A << endl;
//
//        for(int i=0; i < mNumberOfPoints_A; i++) {
//            double x = (double)*jcoords_A_array;
//            jcoords_A_array++;
//            double y = (double)*jcoords_A_array;
//            jcoords_A_array++;
//            double z = (double)*jcoords_A_array;
//            jcoords_A_array++;
//            coords_A.push_back(x);
//            coords_A.push_back(y);
//            coords_A.push_back(z);
//            //            cout << x << ", " << y << ", " << z << endl;
//        }
//    }
//    {
//        jfloat* jcoords_B_array = env->GetFloatArrayElements(coords_B_array, &iscopy);
//        const int mNumberOfPoints_B = (env->GetArrayLength(coords_B_array)) / 3;
//
//        if (M_DEBUG) cout << "--- B has points: " << mNumberOfPoints_B << endl;
//
//        for(int i=0; i < mNumberOfPoints_B; i++) {
//            double x = (double)*jcoords_B_array;
//            jcoords_B_array++;
//            double y = (double)*jcoords_B_array;
//            jcoords_B_array++;
//            double z = (double)*jcoords_B_array;
//            jcoords_B_array++;
//            coords_B.push_back(x);
//            coords_B.push_back(y);
//            coords_B.push_back(z);
//            //            cout << x << ", " << y << ", " << z << endl;
//        }
//    }


//    Vertex_iterator v;
//    for( v = polyhedron_result.vertices_begin();
//        v != polyhedron_result.vertices_end();
//        ++v )     {
//        cout << v->point() << endl;
//    }

//    vector<Alpha_shape_3::Facet> a_facets;
//    as->get_alpha_shape_facets(std::back_inserter(a_facets), facets_type);
//    const jsize nb_facets_indices = 3 * 3 * a_facets.size();
//
//    if (M_DEBUG) cout << "--- number of points: " << nb_facets_indices << endl;
//
//    Index index(as->vertices_begin(), as->vertices_end());
//    jfloat m_mesh_points [nb_facets_indices];
//
//    size_t nbf=a_facets.size();
//    for (size_t i=0;i<nbf;++i) {
//        if ( as->classify( a_facets[i].first )!=Alpha_shape_3::EXTERIOR ) {
//            a_facets[i]=as->mirror_facet( a_facets[i] );
//        }
//
//        int indices[3]={
//            (a_facets[i].second+1)%4,
//            (a_facets[i].second+2)%4,
//            (a_facets[i].second+3)%4,
//        };
//
//        /// according to the encoding of vertex indices, this is needed to get
//        /// a consistent orienation
//        if ( a_facets[i].second%2==0 ) {
//            swap(indices[0], indices[1]);
//        }
//
//        m_mesh_points[i * 9 + 0] = a_facets[i].first->vertex(indices[0])->point()[0];
//        m_mesh_points[i * 9 + 1] = a_facets[i].first->vertex(indices[0])->point()[1];
//        m_mesh_points[i * 9 + 2] = a_facets[i].first->vertex(indices[0])->point()[2];
//        m_mesh_points[i * 9 + 3] = a_facets[i].first->vertex(indices[1])->point()[0];
//        m_mesh_points[i * 9 + 4] = a_facets[i].first->vertex(indices[1])->point()[1];
//        m_mesh_points[i * 9 + 5] = a_facets[i].first->vertex(indices[1])->point()[2];
//        m_mesh_points[i * 9 + 6] = a_facets[i].first->vertex(indices[2])->point()[0];
//        m_mesh_points[i * 9 + 7] = a_facets[i].first->vertex(indices[2])->point()[1];
//        m_mesh_points[i * 9 + 8] = a_facets[i].first->vertex(indices[2])->point()[2];
//    }
//
//    jfloatArray result;
//    result = env->NewFloatArray( nb_facets_indices);
//    env->SetFloatArrayRegion(result, 0, nb_facets_indices, m_mesh_points);
//
//    return result;


//    /* build jfloatarray */
//
//    const int mNumberOfMeshData = distance(polyhedron_result.vertices_begin(), polyhedron_result.vertices_end()) * 3;
//    jfloat mMeshData[mNumberOfMeshData];
//
//    Vertex_iterator v;
//    int i = 0;
//    for( v = polyhedron_result.vertices_begin();
//        v != polyhedron_result.vertices_end();
//        ++v )
//    {
//        mMeshData[i] = (float)CGAL::to_double(v->point().x());
//        i++;
//        mMeshData[i] = (float)CGAL::to_double(v->point().y());
//        i++;
//        mMeshData[i] = (float)CGAL::to_double(v->point().z());
//        i++;
//    }
//
//    if (M_DEBUG) cout << "--- build array " << endl;
//
//    jfloatArray mResultMeshData;
//    mResultMeshData = env->NewFloatArray(mNumberOfMeshData);
//    env->SetFloatArrayRegion(mResultMeshData, 0, mNumberOfMeshData, mMeshData);
//
//    if (M_DEBUG) cout << "--- return array " << endl;
//
//    return mResultMeshData;

