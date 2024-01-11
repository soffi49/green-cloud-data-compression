#!/bin/bash
echo "Running GUI..."

trap navigate SIGINT SIGTERM

# NAVIGATE BACK TO COMPILE DIRECTORY IN CASE OF SIGTERM/SIGINT
function navigate() {
  cd ../compile || exit
}

# NAVIGATE TO ENGINE DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}/green-cloud-ui" || exit

# RUN GUI
npm start

# NAVIGATE BACK TO COMPILE DIRECTORY
navigate
