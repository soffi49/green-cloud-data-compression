#!/bin/bash
echo "Recompiling backend application"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}" || exit

mvn clean compile package

# COPY CONFIGURATION FILES TO STRATEGY FOLDER
cp -R ./engine/src/main/resources/scenarios/. ./engine/runnable/scenarios/
cp -R ./engine/src/main/resources/properties/. ./engine/runnable/properties/
cp -R ./engine/src/main/resources/knowledge/. ./engine/runnable/knowledge/
cp -R ./engine/src/main/resources/samples/. ./engine/runnable/samples/

echo "Recompilation of the backend application completed"

# NAVIGATE BACK TO COMPILE DIRECTORY
cd ./compile || exit
