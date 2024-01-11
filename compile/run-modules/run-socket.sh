#!/bin/bash
echo "Running Socket Server..."

trap navigate SIGINT SIGTERM

# NAVIGATE BACK TO COMPILE DIRECTORY IN CASE OF SIGTERM/SIGINT
function navigate() {
  cd ../compile || exit
}

# NAVIGATE TO ENGINE DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}/socket-server" || exit

# RUN GUI
npm start

# NAVIGATE BACK TO COMPILE DIRECTORY
navigate
