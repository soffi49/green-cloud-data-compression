#!/bin/bash
# INITIALIZE BACKEND APPLICATION
source ./compile-modules/initialize-backend.sh &&

# INITIALIZE GUI
source ./compile-modules/initialize-GUI.sh

# INITIALIZE SOCKET-SERVER
source ./compile-modules/initialize-socket.sh

echo "Full initialization completed!"
