#! /usr/local/bin/zsh

if [ -z ${3+x} ]; then 
    NUMBER_OF_SAMPLES=25
else 
    NUMBER_OF_SAMPLES=$3
fi

CYCLES_BIN="$1"
SCENE_FILES_FOLDER="$2"

echo "+++ using cycles binary ........... : " $CYCLES_BIN
echo "+++ looking for scene XML files ... : " $SCENE_FILES_FOLDER
echo "+++ samples ....................... : " $NUMBER_OF_SAMPLES

find $SCENE_FILES_FOLDER -name "*.xml" -type f -exec $CYCLES_BIN --samples $NUMBER_OF_SAMPLES --background --output {}.png {} \;
