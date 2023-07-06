# Extended Green Cloud Simulator (EGCS)

Extended Green Cloud Simulator (EGCS) is a self-adaptive multi-agent system used to simulate carbon-intelligent cloud
infrastructures. Specifically, it allows the users to:

- define topologies of cloud networks that are, among others, powered with green-energy sources
- employ and assess different system strategies such as:
    - orchestration of the workload across different cloud network regions
    - job prioritization and scheduling policies
    - handling unexpected environmental events such as dynamic weather changes
- run large-scale distributed simulations including simulations in which agents reside in multiple agent platforms and
  are distributed among different hosts
- experiment with system's autonomic behaviours

## System architecture and technological requirements

### Technological stack

The system was implemented with the following technologies:

- Java 17 + JADE (Backend)
- Typescript (Frontend)
- Typescript + Express + Express WS (Socket Server)
- PostgreSQL Timescale (Database)

In order to run the system, it is also required to install:

1. Docker (https://docs.docker.com/get-docker/)
2. Maven (https://maven.apache.org/download.cgi)
3. Shell with the ability to execute bash scripts (e.g. Git BASH for Windows users: https://gitforwindows.org/)
4. Node.js 18+ (https://nodejs.org/en/download)

### System components

The system consists of the following modules:

- **_agent-factory_** - module containing methods used for generating agent controllers and GUI agent nodes
- **_application_** - module containing agent system logic
- **_commons_** - module containing common methods and classes shared between other modules
- **_engine_** - module used to run the simulations. It allows the users to specify the topology of the cloud network,
  pass the desired system strategies and specify test scenario events
- **_green_cloud_ui_** - module containing implementation of GUI
- **_gui_** - module implementing GUI controller with WebSockets. It is used to connect JADE agents with the frontend
- **_knowledge-database_** - PostgreSQL Timescale database used to store monitoring data for the analysis performed by
  system's managing component
- **_managing-system_** - module realizing agents' adaptations. It implements the architectural MAPE-K model
- **_rules-controller_** (to be implemented) - module that defines rules and behaviours that (in the future) will handle
  the system's strategies
- **_socket-server_** - WebSocket module that serves as a proxy between backend and the GUI

### External services

The system is using _OpenWeatherMap API_ to retrieve weather predictions: https://openweathermap.org/api.

## System configuration and running

All files used to configure the system are placed in `./config`. The instruction on how to configure the system can be
found in: [Configuration Instruction](config/CONFIG_INSTRUCTION.md).

Similarly, the scripts used to compile the project are in `./compile`. In order to compile and run the project, refer
to: [Compilation Instruction](compile/COMPILE_INSTRUCTION.md).





