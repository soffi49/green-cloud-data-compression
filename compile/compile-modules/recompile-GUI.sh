#!/bin/bash
echo "Recompiling GUI"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_NAME="green-cloud-data-compression"
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/${PROJECT_NAME}*}
cd "${PARENT_DIR}/${PROJECT_NAME}" || exit

# COPY CONFIGURATION FILE
echo "Copying configuration .env file..."
cp -fr ./config/green-cloud-ui/.env ./green-cloud-ui/.env

# REINSTALLING NODE MODULES
npm install

echo "Recompilation of GUI completed"
