#!/bin/bash
echo "Recompiling Socket Server"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}" || exit

# COPY CONFIGURATION FILE
echo "Copying configuration .env file..."
cp -fr ./config/socket-server/.env ./socket-server/.env

# REINSTALLING NODE MODULES
npm install
npm run build

echo "Recompilation of Socket Server completed"
