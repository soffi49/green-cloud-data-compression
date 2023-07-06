#!/bin/bash
echo "Running Socket Server..."

trap navigate SIGINT SIGTERM

# NAVIGATE BACK TO COMPILE DIRECTORY IN CASE OF SIGTERM/SIGINT
function navigate() {
  cd ../compile || exit
}

# NAVIGATE TO ENGINE DIR
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/"green-cloud"*}
cd "${PARENT_DIR}/green-cloud/socket-server" || exit

# RUN GUI
npm start

# NAVIGATE BACK TO COMPILE DIRECTORY
navigate
