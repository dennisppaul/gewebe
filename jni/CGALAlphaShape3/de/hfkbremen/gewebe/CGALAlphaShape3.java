package de.hfkbremen.gewebe;

public class CGALAlphaShape3 {
    /**
     * print version of library.
     *
     * @return
     */
    public native int version();

    /**
     * Initialize the alpha_shape from the array of points coordinates
     * (coord), return a pointer to the c++ alpha_shape object for
     * subsequent native method calls.
     *
     * @param pts_coord
     * @return
     */
    private native long init_alpha_shape(float[] pts_coord);

    /**
     * For a given value of alpha and a given class_type for the facets,
     * sets the alpha value of the alpha_shape to alpha. Returns the array
     * of facet indices from the alpha_shape.
     *
     * @param classification_type
     * @param alpha
     * @param ptr
     * @return
     */
    private native int[] get_alpha_shape_facets(String classification_type, float alpha, long ptr);

    /**
     * For a given number of solid components and a given class_type for
     * the facets, sets the alpha value of the alpha_shape A such that A
     * satisfies the following two properties: (1) all data points are
     * either on the boundary or in the interior of the regularized
     * version of A; (2) the number of solid component of A is equal to or
     * smaller than nb_components. Returns the array of facet indices from
     * the alpha_shape.
     *
     * @param classification_type
     * @param nb_sc
     * @param ptr
     * @return
     */
    private native int[] get_alpha_shape_facets_optimal(String classification_type, int nb_sc, long ptr);

    /**
     * gives the alpha value of the current alpha_shape
     *
     * @param ptr
     * @return
     */
    private native float get_alpha(long ptr);

    /**
     * Returns the number of solid components of the current alpha_shape,
     * that is, the number of components of its regularized version.
     *
     * @param ptr
     * @return
     */
    private native int number_of_solid_components(long ptr);

    /**
     * 
     * @param classification_type
     * @param alpha
     * @param ptr
     * @return
     */
    public native float[] get_alpha_shape_mesh(String classification_type, float alpha, long ptr);
    
    /**
     * @param nb_sc
     * @param ptr
     * @return
     */
    private native float get_optimal_alpha(int nb_sc, long ptr);
    
    static {
        System.loadLibrary("CGALAlphaShape3");
    }
    
    public static void main(String[] args) {
        CGALAlphaShape3 cgal = new CGALAlphaShape3();
        int mResult = cgal.version();
        long mPtr = cgal.init_alpha_shape(new float[]{1,2,3,4,5,6,7,8,9});
        System.out.println(mPtr);
        System.out.println(cgal.get_alpha(mPtr));
    }
}
