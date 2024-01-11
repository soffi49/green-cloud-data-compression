#!/bin/bash
echo "Compiling strategy"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}" || exit

mvn package
echo "Finished preparing strategy jar. Jar can be found in /engine/target directory"

# NAVIGATE BACK TO COMPILE DIRECTORY
cd ./compile || exit
