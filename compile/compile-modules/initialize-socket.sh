#!/bin/bash
echo "Initializing Socket Server"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/"green-cloud"*}
cd "${PARENT_DIR}/green-cloud" || exit

# COPY CONFIGURATION FILE
echo "Copying configuration .env file..."
cp -fr ./config/socket-server/.env ./socket-server/.env

# NAVIGATE TO MODULES
cd ./socket-server || exit &&

# CLEAR PREVIOUS NODE MODULES (IF THEY EXIST)
echo "Cleaning previous node modules..."
npm run clean

# INSTALL NODE MODULES
echo "Installing node modules..."
npm install

# BUILDING LIBRARIES USING BABEL
echo "Installing node modules..."
npm run build

echo "Initialization of Socket Server completed!"

# NAVIGATE BACK TO COMPILE DIRECTORY
cd ../compile || exit
