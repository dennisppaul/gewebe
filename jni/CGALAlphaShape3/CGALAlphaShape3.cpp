//#include <iostream>
//#include <fstream>

//#include <sys/types.h>
//#include <sys/ipc.h>
//#include <sys/shm.h>
//#include <sys/mman.h>
//#include <sys/stat.h>
//#include <fcntl.h>
//#include <unistd.h>
//#include <list>
//#include <strings.h>

#include <CGAL/basic.h>
#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include <CGAL/Delaunay_triangulation_3.h>
#include <CGAL/Alpha_shape_3.h>
#include <CGAL/Inverse_index.h>
#include <CGAL/Linear_cell_complex.h>

#include "de_hfkbremen_gewebe_CGALAlphaShape3.h"

/* --- */

using namespace std;

struct K : CGAL::Exact_predicates_inexact_constructions_kernel {};
typedef K::Point_3                                  Point;

typedef CGAL::Alpha_shape_vertex_base_3<K>          Vb;
typedef CGAL::Alpha_shape_cell_base_3<K>            Fb;
typedef CGAL::Triangulation_data_structure_3<Vb,Fb> Tds;
typedef CGAL::Delaunay_triangulation_3<K,Tds>       Triangulation_3;
typedef CGAL::Alpha_shape_3<Triangulation_3>        Alpha_shape_3;

typedef Alpha_shape_3::Alpha_iterator               Alpha_iterator;
typedef Alpha_shape_3::Facet                        Facet;
typedef Alpha_shape_3::Cell_handle                  Cell_handle;
typedef Alpha_shape_3::Vertex_iterator              Vertex_iterator;
typedef Alpha_shape_3::Vertex_handle                Vertex_handle;

typedef Alpha_shape_3::Classification_type          Classification_type;
typedef CGAL::Inverse_index<Vertex_iterator>        Index;

JNIEXPORT jint JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_version
(JNIEnv * env, jobject)
{
    int mVersion = 20160625;
    int mTime = 115125;
    cout << "--- version " << mVersion << " time " << mTime << "\n";
    return mVersion;
}

/* Initialize the alpha_shape from the array of points coordinates
 *  (coord), return a pointer to the c++ alpha_shape object for
 *  subsequent native method calls.
 */
JNIEXPORT jlong JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_init_1alpha_1shape
(JNIEnv * env, jobject jobj, jfloatArray coord)
{
    jboolean iscopy;
    jfloat* jcoord = env->GetFloatArrayElements(coord, &iscopy);
    const jsize nb_pts = (env->GetArrayLength(coord)) / 3;
    
    //cgal stuff///////////////////////////////
    
    bool M_DEBUG = false;
    
    if (M_DEBUG) cout << "--- init_alpha_shape" << endl;
    
    //get the points
    std::list<Point> lp;
    double x, y, z;
    Point p;
    int countp = nb_pts;
    
    if (M_DEBUG) cout << "--- nb_pts: " << nb_pts << endl;
    
    for( ; countp>0 ; countp--)    {
        x = *jcoord;//what about *jcoord++  ??
        jcoord++;
        y = *jcoord;
        jcoord++;
        z = *jcoord;
        jcoord++;
        p = Point(x,y,z);
        //        if (M_DEBUG) cout << "--- pt: " << x << ", " << y << ", " << z << endl;
        lp.push_back(p);
    }
    
    //build alpha_shape in GENERAL mode and set alpha=0
    Alpha_shape_3* as;
    as = new Alpha_shape_3(lp.begin(),lp.end(), 0, Alpha_shape_3::GENERAL);
    
    //    cout << "alpha " << as->get_alpha() << endl;
    //    cout << "prt   " << as << endl;
    
    jlong mPtr = (jlong)as;
    //    cout << "jlong " << mPtr << endl;
    return mPtr;
}

/** For a given value of alpha and a given class_type for the facets,
 * sets the alpha value of the alpha_shape to alpha. Returns the array
 * of facet indices from the alpha_shape.
 */
JNIEXPORT jintArray JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_get_1alpha_1shape_1facets
(JNIEnv * env, jobject jobj, jstring class_type, jfloat alpha, jlong ptr)
{
    bool M_DEBUG = false;
    
    if (M_DEBUG) cout << "--- get_alpha_shape_facets" << endl;
    
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    if (M_DEBUG) cout << "--- set alpha " << alpha << endl;
    as->set_alpha(alpha);
    
    Classification_type facets_type;
    jboolean iscopy;
    const char *class_type_char = env->GetStringUTFChars(class_type, &iscopy);
    if ( strcmp(class_type_char, "REGULAR") == 0 ) {
        facets_type = Alpha_shape_3::REGULAR;
        if (M_DEBUG) cout << "--- type: REGULAR" << endl;
    } else {
        facets_type = Alpha_shape_3::SINGULAR;
        if (M_DEBUG) cout << "--- type: SINGULAR" << endl;
    }
    
    //get facets and compute their vertices index wrt the order in which
    //points are given. Note that the order of vertices in the triangulation
    //is the insersion order.
    //WARNING the first vertex is the infinite vertex, so that finite
    //vertices are indexed from 1 to n, and one has to substract 1 to
    //get the corresponding index in a usual array beginning from 0
    
    // from http://stackoverflow.com/questions/15905833/saving-cgal-alpha-shape-surface-mesh
    vector<Alpha_shape_3::Facet> a_facets;
    as->get_alpha_shape_facets(std::back_inserter(a_facets), facets_type);
    
    size_t nbf=a_facets.size();
    for (size_t i=0;i<nbf;++i) {
        if ( as->classify( a_facets[i].first )!=Alpha_shape_3::EXTERIOR ) {
            a_facets[i]=as->mirror_facet( a_facets[i] );
        }
        
        int indices[3]={
            (a_facets[i].second+1)%4,
            (a_facets[i].second+2)%4,
            (a_facets[i].second+3)%4,
        };
        
        /// according to the encoding of vertex indices, this is needed to get
        /// a consistent orienation
        if ( a_facets[i].second%2==0 ) {
            swap(indices[0], indices[1]);
        }
        
        if (M_DEBUG) cout <<
            a_facets[i].first->vertex(indices[0])->point() << "\n" <<
            a_facets[i].first->vertex(indices[1])->point() << "\n" <<
            a_facets[i].first->vertex(indices[2])->point() << "\n";
        
        if (M_DEBUG) cout << a_facets[i].first->vertex(indices[0])->point()[0] << ", " << a_facets[i].first->vertex(indices[0])->point()[1] << ", " << a_facets[i].first->vertex(indices[0])->point()[2] << endl;
    }
    
    // ------------------- //
    
    std::list<Facet> facets;
    as->get_alpha_shape_facets(back_inserter(facets), facets_type);
    const jsize nb_facets_indices = 3*facets.size();
    if (M_DEBUG) cout << "--- number of facets: " << facets.size() << endl;
    
    Index index(as->vertices_begin(), as->vertices_end());
    jint facets_indices [nb_facets_indices];
    int ii = 0;
    
    std::list<Facet>::iterator itb=facets.begin(), ite=facets.end();
    for(; itb!=ite; itb++) {
        //Facet is a std::pair<Cell_handle, int>
        Cell_handle ch = itb->first;
        int i = itb->second;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+1)%4 ) )] -1);
        if (M_DEBUG) cout << "p0(" << facets_indices[ii] << ") ";
        ii++;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+2)%4 ) )] -1);
        if (M_DEBUG) cout << "p1(" << facets_indices[ii] << ") ";
        ii++;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+3)%4 ) )] -1);
        if (M_DEBUG) cout << "p2(" << facets_indices[ii] << ")\n";
        ii++;
    }
    
    //set the result
    jintArray result;
    result = env->NewIntArray( nb_facets_indices);
    env->SetIntArrayRegion(result, 0, nb_facets_indices, facets_indices);
    
    return result;
}

/* For a given number of solid components and a given class_type for
 * the facets, sets the alpha value of the alpha_shape A such that A
 * satisfies the following two properties: (1) all data points are
 * either on the boundary or in the interior of the regularized
 * version of A; (2) the number of solid component of A is equal to or
 * smaller than nb_components. Returns the array of facet indices from
 * the alpha_shape.
 */
JNIEXPORT jintArray JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_get_1alpha_1shape_1facets_1optimal
(JNIEnv * env, jobject jobj, jstring class_type, jint nb_sc, jlong ptr)
{
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    
    Classification_type facets_type;
    jboolean iscopy;
    const char *class_type_char = env->GetStringUTFChars(class_type, &iscopy);
    if ( strcmp(class_type_char, "REGULAR") == 0 ) {
        facets_type = Alpha_shape_3::REGULAR;
    }
    else {
        facets_type = Alpha_shape_3::SINGULAR;
    }
    
    // find optimal alpha value
    Alpha_iterator opt = as->find_optimal_alpha(nb_sc);
    as->set_alpha(*opt);
    
    //get facets and compute their vertices index wrt the order in which
    //points are given. Note that the order of vertices in the triangulation
    //is the insersion order.
    //WARNING the first vertex is the infinite vertex, so that finite
    //vertices are indexed from 1 to n, and one has to substract 1 to
    //get the corresponding index in a usual array beginning from 0
    std::list<Facet> facets;
    as->get_alpha_shape_facets(std::back_inserter(facets), facets_type);
    const jsize nb_facets_indices = 3*facets.size();
    
    Index index(as->vertices_begin(), as->vertices_end());
    jint facets_indices [nb_facets_indices];
    int ii = 0;
    
    std::list<Facet>::iterator itb=facets.begin(), ite=facets.end();
    for(; itb!=ite; itb++) {
        //Facet is a std::pair<Cell_handle, int>
        Cell_handle ch = itb->first;
        int i = itb->second;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+1)%4 ) )] -1);
        ii++;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+2)%4 ) )] -1);
        ii++;
        facets_indices[ii] = int( index[ Vertex_iterator( ch->vertex( (i+3)%4 ) )] -1);
        ii++;
    }
    
    //set the result
    jintArray result;
    result = env->NewIntArray( nb_facets_indices);
    env->SetIntArrayRegion(result, 0, nb_facets_indices, facets_indices);
    
    return result;
}

/* gives the alpha value of the current alpha_shape
 */
JNIEXPORT jfloat JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_get_1alpha
(JNIEnv * env, jobject jobj, jlong ptr)
{
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    return as->get_alpha();
}

/*  Returns the number of solid components of the current alpha_shape,
 *   that is, the number of components of its regularized version.
 */
JNIEXPORT jint JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_number_1of_1solid_1components
(JNIEnv * env, jobject jobj, jlong ptr)
{
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    return as->number_of_solid_components();
}

JNIEXPORT jfloatArray JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_get_1alpha_1shape_1gewebe
(JNIEnv * env, jobject jobj, jstring class_type, jfloat alpha, jlong ptr)
{
    bool M_DEBUG = false;
    
    if (M_DEBUG) cout << "--- get_alpha_shape_mesh" << endl;
    
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    as->set_alpha(alpha);
    
    /*
     if (alpha < 0) {
     // find optimal alpha value
     int mNumberOfSolids = (int)abs(alpha);
     Alpha_iterator opt = as->find_optimal_alpha(mNumberOfSolids);
     as->set_alpha(*opt);
     } else {
     as->set_alpha(alpha);
     }
     */
    
    if (M_DEBUG) cout << "--- set alpha " << alpha << endl;
    
    Classification_type facets_type;
    jboolean iscopy;
    const char *class_type_char = env->GetStringUTFChars(class_type, &iscopy);
    if ( strcmp(class_type_char, "REGULAR") == 0 ) {
        facets_type = Alpha_shape_3::REGULAR;
    } else {
        facets_type = Alpha_shape_3::SINGULAR;
    }
    
    // --- from http://stackoverflow.com/questions/15905833/saving-cgal-alpha-shape-surface-mesh
    vector<Alpha_shape_3::Facet> a_facets;
    as->get_alpha_shape_facets(std::back_inserter(a_facets), facets_type);
    const jsize nb_facets_indices = 3 * 3 * a_facets.size();
    
    if (M_DEBUG) cout << "--- number of points: " << nb_facets_indices << endl;
    
    Index index(as->vertices_begin(), as->vertices_end());
    jfloat m_mesh_points [nb_facets_indices];
    
    size_t nbf=a_facets.size();
    for (size_t i=0;i<nbf;++i) {
        if ( as->classify( a_facets[i].first )!=Alpha_shape_3::EXTERIOR ) {
            a_facets[i]=as->mirror_facet( a_facets[i] );
        }
        
        int indices[3]={
            (a_facets[i].second+1)%4,
            (a_facets[i].second+2)%4,
            (a_facets[i].second+3)%4,
        };
        
        /// according to the encoding of vertex indices, this is needed to get
        /// a consistent orienation
        if ( a_facets[i].second%2==0 ) {
            swap(indices[0], indices[1]);
        }
        
        m_mesh_points[i * 9 + 0] = a_facets[i].first->vertex(indices[0])->point()[0];
        m_mesh_points[i * 9 + 1] = a_facets[i].first->vertex(indices[0])->point()[1];
        m_mesh_points[i * 9 + 2] = a_facets[i].first->vertex(indices[0])->point()[2];
        m_mesh_points[i * 9 + 3] = a_facets[i].first->vertex(indices[1])->point()[0];
        m_mesh_points[i * 9 + 4] = a_facets[i].first->vertex(indices[1])->point()[1];
        m_mesh_points[i * 9 + 5] = a_facets[i].first->vertex(indices[1])->point()[2];
        m_mesh_points[i * 9 + 6] = a_facets[i].first->vertex(indices[2])->point()[0];
        m_mesh_points[i * 9 + 7] = a_facets[i].first->vertex(indices[2])->point()[1];
        m_mesh_points[i * 9 + 8] = a_facets[i].first->vertex(indices[2])->point()[2];
    }
    
    jfloatArray result;
    result = env->NewFloatArray( nb_facets_indices);
    env->SetFloatArrayRegion(result, 0, nb_facets_indices, m_mesh_points);
    
    return result;
}

JNIEXPORT jfloat JNICALL Java_de_hfkbremen_gewebe_CGALAlphaShape3_get_1optimal_1alpha
(JNIEnv * env, jobject jobj, jint nb_sc, jlong ptr)
{
    Alpha_shape_3* as = (Alpha_shape_3*) ptr;
    Alpha_iterator opt = as->find_optimal_alpha(nb_sc);
    jfloat mAlpha = *opt;
    return mAlpha;
}
