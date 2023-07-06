#!/bin/bash
echo "Running engine..."

trap navigate SIGINT SIGTERM

# NAVIGATE BACK TO COMPILE DIRECTORY IN CASE OF SIGTERM/SIGINT
function navigate() {
  cd ../../compile || exit
}

# NAVIGATE TO ENGINE DIR
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/"green-cloud"*}
cd "${PARENT_DIR}/green-cloud/engine/target" || exit

if [ "$1" == "SINGLE" ]
then
  java -cp green-cloud-engine.jar runner.EngineRunner
elif [ "$1" == "MULTI" ]
then
  java -cp green-cloud-engine.jar runner.MultiEngineRunner
fi

# NAVIGATE BACK TO COMPILE DIRECTORY
navigate
