# gewebe

a collection of tools and methods for working with meshes in [Processing](https://processing.org/).

## dependencies + libraries

- [Processing](https://processing.org/)
- [Cycles](https://www.cycles-renderer.org/) a physically based renderer included in the [Blender](https://www.blender.org/) project
- [Sunflow](http://sunflow.sourceforge.net/) an open source rendering system for photo-realistic image synthesis written in Java
- [Java 3D™](https://java3d.java.net) a scene graph-based 3D API for Java ( v1.5.2 )
- [Computational Geometry Algorithms Library (CGAL)](http://www.cgal.org) ( v5.0.2 ) 
    - on macOS `cgal` is available via [Homebrew](http://brew.sh) install with `$ brew install cgal`

## know limitations and bugs

- CGAL binding is partly broken ( and currently only compiled for macOS ( 10.15 ) )
- Cycles standalone renderer is currently only compiled + distributed for macOS ( 10.15 )