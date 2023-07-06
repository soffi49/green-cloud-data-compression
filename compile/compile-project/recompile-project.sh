#!/bin/bash
# RECOMPILE BACKEND APPLICATION
source ./compile-modules/recompile-backend.sh &&

# RECOMPILE GUI
source ./compile-modules/recompile-GUI.sh

# RECOMPILE SOCKET-SERVER
source ./compile-modules/recompile-socket.sh

echo "Full recompilation completed!"
