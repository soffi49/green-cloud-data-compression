#!/bin/bash
echo "Running engine..."

trap navigate SIGINT SIGTERM

# NAVIGATE BACK TO COMPILE DIRECTORY IN CASE OF SIGTERM/SIGINT
function navigate() {
  cd ../../compile || exit
}

# NAVIGATE TO ENGINE DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}/engine/runnable" || exit

PACKAGE_NAME="green-cloud-engine.jar"

if [ ! -z "$2" ]
then
  PACKAGE_NAME=$2
fi

# COPY CONFIGURATION TO JAR
jar -uvf "${PACKAGE_NAME}" -C properties .
jar -uvf "${PACKAGE_NAME}" -C scenarios .
jar -uvf "${PACKAGE_NAME}" -C knowledge .
jar -uvf "${PACKAGE_NAME}" -C samples .

if [ "$1" == "MULTI" ]
then
  java -cp "${PACKAGE_NAME}" runner.MultiEngineRunner
else
  java -cp "${PACKAGE_NAME}" runner.EngineRunner
fi

# NAVIGATE BACK TO COMPILE DIRECTORY
navigate
