#!/bin/bash

PROCESSING_APP_PATH='/Applications/Processing.app/'
PROJECT_NAME="CGALAlphaShape3"


PWD=`pwd`
BUILD_PATH=build
PACKAGE_NAME='gewebe'
PACKAGE_JAR=$PACKAGE_NAME'.jar'
JAVA_SRC_PATH='../../src/'
NATIVE_ACCESSER_PATH=$PACKAGE_NAME/$PROJECT_NAME'.java'
NATIVE_ACCESSER_CLASS_NAME=$PACKAGE_NAME'.'$PROJECT_NAME
NATIVE_HEADER_PATH='../cpp/'
JAVA_BUILD_PATH=java
JAVA_JNI_FILE='lib'$PROJECT_NAME
JAVA_CLASSPATH='.:../../'$PACKAGE_JAR':../'$PACKAGE_JAR':'$PROCESSING_APP_PATH'/Contents/Java/core/library/core.jar'

echo
echo '----- Gewebe $PROJECT_NAME JNI Builder -----'
if [ "$1" != "" ]; then
    echo 'Options:'
    echo '--refresh-header     refreshes header'
    echo '--build              CMake build'
    echo '--test-java          test library'
    echo
fi

refresh_header () {
    cd $JAVA_BUILD_PATH
    javac -h $NATIVE_HEADER_PATH $NATIVE_ACCESSER_PATH -cp $JAVA_CLASSPATH
    cd ../
    echo "+++ refreshed headers"
}

buildj () {
    rm -rf $BUILD_PATH
    mkdir $BUILD_PATH
    cd $BUILD_PATH
    cmake ../
    make
    cd ../
    echo '+++ finished build'
}

testjava () {
    echo '+++ starting test'
    cd $PWD
    java -cp $JAVA_CLASSPATH:$PWD/../gewebe.jar -Djava.library.path=$PWD/../ $NATIVE_ACCESSER_CLASS_NAME
    echo '+++ finished test'
}

mkdir -p $JAVA_BUILD_PATH/gewebe
cp $JAVA_SRC_PATH/$NATIVE_ACCESSER_PATH $JAVA_BUILD_PATH/$NATIVE_ACCESSER_PATH

if [ "$1" != "" ]; then
    while [ "$1" != "" ]; do
        case $1 in
            --refresh-header        )   refresh_header
                                        ;;
            --build                 )   buildj
                                        ;;
            --test-java | --testjava )   testjava
        esac
        shift
    done
else
    refresh_header
    buildj
    testjava
fi

# move library to lib folder
MACOS_DYLIB=$JAVA_JNI_FILE.dylib
if [ -f "$BUILD_PATH/$MACOS_DYLIB" ]; then
    echo '+++ moving library into place (macos)'
    mv $BUILD_PATH/$MACOS_DYLIB ../
fi
LINUX_DYLIB=$JAVA_JNI_FILE.so
if [ -f "$BUILD_PATH/$LINUX_DYLIB" ]; then
    echo '+++ moving library into place (linux)'
    mv $BUILD_PATH/$LINUX_DYLIB ../
fi

rm -rf $JAVA_BUILD_PATH
rm -rf $BUILD_PATH

