#!/bin/bash
echo "Recompiling backend application"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/"green-cloud"*}
cd "${PARENT_DIR}/green-cloud" || exit

echo "Recompiling backend application"
mvn clean compile
echo "Recompilation of the backend application completed"
