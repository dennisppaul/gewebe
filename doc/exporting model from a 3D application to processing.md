# exporting model from a 3D application to processing

* create shapes in 3D application
* convert model to mesh and to triangles
* export model as `*.obj` ( sometimes also called WaveFront )
* check file in text editor
   * file should only contain one line with group ( `g` ). additional groups will be ignored.
   * lines describing faces ( `f` ) should only contain 3 blocks of numbers. e.g `f 1 2 3` or with textures `f 1/4 2/5 3/6`
* put file in data folder of processing sketch
* load file in processing sketch as string array. e.g `String[] md = loadStrings("donut.obj");`
* create mesh from model loader. e.g `Mesh m = ModelLoaderOBJ.parseModelData(md).mesh();`
