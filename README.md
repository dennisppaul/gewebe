# gewebe

a collection of tools and methods for working with meshes in [Processing](https://processing.org/).

## dependencies + libraries

- [Processing](https://processing.org/)
- [Cycles](https://www.cycles-renderer.org/) a physically based renderer included in the [Blender](https://www.blender.org/) project
- [Sunflow](http://sunflow.sourceforge.net/) an open source rendering system for photo-realistic image synthesis written in Java ( v0.07.3 )
- [Java 3D™](https://java3d.java.net) a scene graph-based 3D API for Java ( v1.5.2 )
- [Computational Geometry Algorithms Library (CGAL)](http://www.cgal.org) ( v5.0.2 ) 

## known limitations and bugs

- CGAL binding only works on *macOS*
- Cycles standalone renderer is currently only compiled + distributed for macOS + windows ( but not tested )

## resources

- [Bandai Namco Research Motiondataset](https://github.com/BandaiNamcoResearchInc/Bandai-Namco-Research-Motiondataset) -- "This repository provides motion datasets collected by Bandai Namco Research Inc." motion data can be used with the BVH importer.

## exporting model from 3D application to use in processing

* create shapes in 3D application
* convert model to mesh and to triangles
* export model as `*.obj` ( sometimes also called WaveFront )
* check file in text editor
   * file should only contain one line with group ( `g` ). additional groups will be ignored.
   * lines describing faces ( `f` ) should only contain 3 blocks of numbers. e.g `f 1 2 3` or with textures `f 1/4 2/5 3/6`
* put file in data folder of processing sketch
* load file in processing sketch as string array. e.g `String[] md = loadStrings("donut.obj");`
* create mesh from model loader. e.g `Mesh m = ModelLoaderOBJ.parseModelData(md).mesh();`
