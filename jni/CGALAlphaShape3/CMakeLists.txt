cmake_minimum_required(VERSION 3.17)
project(CGALAlphaShape3)

set(CMAKE_MACOSX_RPATH 1)
set(CMAKE_BUILD_TYPE Release)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

find_package(CGAL REQUIRED)
include(${CGAL_USE_FILE})

set(SOURCE_FILES CGALAlphaShape3.cpp)
#add_executable(CGALAlphaShape3 ${SOURCE_FILES})
add_library(CGALAlphaShape3 SHARED ${SOURCE_FILES})
target_link_libraries(CGALAlphaShape3 ${CGAL_LIBS})