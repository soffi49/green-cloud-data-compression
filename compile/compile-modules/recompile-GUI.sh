#!/bin/bash
echo "Recompiling GUI"

# NAVIGATE TO MAIN PROJECT DIR
PROJECT_DIR=$(pwd)
PARENT_DIR=${PROJECT_DIR%/"green-cloud"*}
cd "${PARENT_DIR}/green-cloud" || exit

# COPY CONFIGURATION FILE
echo "Copying configuration .env file..."
cp -fr ./config/green-cloud-ui/.env ./green-cloud-ui/.env

# REINSTALLING NODE MODULES
npm install

echo "Recompilation of GUI completed"
