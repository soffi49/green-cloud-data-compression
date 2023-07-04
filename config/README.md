# System configuration

Some system modules require/accept additional configuration parameters that are passed by the user in the dedicated
property files. All of such files are located in this directory (i.e. under `./config`), and are compiled automatically
while running the main building scripts (see [Main README](../README.md)).

## application

Module _application_ accepts single configuration file: _.config.properties_ (**it is important to not change the file
name**).

It contains the following properties:

- `weather.api.key` - key used to access the default _OpenWeather API_ account via which _Monitoring Agents_ retrieve
  weather conditions. The user should keep in mind, that the account provided by default has **limited number** of API
  request.
- `offline.mode` - boolean flag indicating, whether _Monitoring Agents_ should use external weather API service, or
  should operate in "offline" manner (meaning, using mock data in place of the real weather conditions).

## engine

In contrary to _application_ module, _engine_ is using multiple configuration files.
The structure of these files is as following:

```
|--/engine
|   |--/properties
|   |   |--scenario.properties
|   |   |--system.properties
|   |
|   |--/scenarios
|   |-- <scenario/event name>.xml
|   |-- <scenario/event name>.xml
|   |
|   |--/examples
|   |
```

### ./properties

The directory `./properties` contains two files (**which names should not be changed**):

- _scenario.properties_ - file specifying general scenario arguments, including:
    - `scenario.usesubdirectory` - boolean flag indicating if the scenario file should be taken from a nested
      directory (taken relatively to _/config/engine/scenarios_).
    - `scenario.subdirectory` - name of the nested directory in which scenario files are placed . While specifying the
      directory path, the user should use `.` instead of separators (e.g. if the scenario file is placed
      in _/scenarios/test/test1_ directory, then path should be given as _test.test1_).
    - `scenario.structure` - name of the file defining cloud network topology. **IMPORTANT! If
      the `scenario.usesubdirectory` flag is set to true, then the system will look for the network topology file in the
      specified subdirectory!**
    - `scenario.runEvents` - flag indicating if the system should run explicitly specified events (such as new clients
      appearing in the network), or (if false), if the system events, should be generated randomly based on predefined
      general parameters.
    - `scenario.events` - name of the file in which events are defined. **IMPORTANT! If the `scenario.usesubdirectory`
      flag is set to true, then the system will look for the event file in the specified subdirectory!**
    - `scenario.clients.*` - specify parameters used to generate workload in the system in case
      when `scenario.runEvents` flag is false:
        - `maxpower` - maximal power of job sent to the cloud
        - `minpower` - minimal power of job sent to the cloud
        - `minstarttime` - minimal number of seconds specifying expected start time of new client's job
        - `maxstarttime` - maximal number of seconds specifying expected start time of new client's job
        - `maxendtime` - maximal number of seconds specifying expected finish time of new client's job
        - `maxdeadline` - maximal number of seconds specifying expected deadline of new client's job
- _system.properties_ - file specifying configuration of the system including parameters of agent platform. If the
  system is to be run on multiple hosts, this file allows to specify how the machines will communicate with each other.
    - `mtp.*` - specifies default ports used by the JADE message transporter
        - `intra` - port used for the communication between agents residing in the same platform
        - `inter` - port used for the communication between agents residing in different platforms
    - `container.*` - specifies configuration of the agent container and which agents are going to be run
        - `mainhost` - flag indicating if the container is the main host for **the entire system** (i.e. container
          running _central_ agents such as _Scheduler Agent_ or _Managing Agent_)
        - `createnew` - flag indicating if the container will run in a new agent platform
        - `locationId` - corresponds to the value of property field of _Cloud Network Agents_ defined
          in cloud network topology file, which specifies the name of the region, managed by the given _CNA_. By
          passing it in the configuration file, the user outlines that a given container will run only the agents
          that belong to the aforementioned region (e.g. if `locationId` is _CNA1_ it means that only agents that
          are under _CNA_ which manages _CNA1_ region, will be taken under consideration). If the `locationId` is of
          the form _Clients<number>_ then it means that a given container will run only _Client Agents_. If
          the `locationId` is empty, then the system takes under consideration all _CNA_'s agents.
        - `containerId` - argument similar to `locationId`, but corresponds to the name of the group of _Server Agents_
          that should reside in the same container (it is also specified in the cloud network topology file). If
          `containerId` is left empty, then it means that the container will run only these _Server Agents_, that do not
          have this property specified. **IMPORTANT! If the `createnew` flag is set to true, then `containerId` field is
          being ignored!**
        - `platformid` - identifier of the platform inside which the container is to be created
        - `localhostip` - IP address of the host that is running a given container
    - `main.*` - specifies the configuration of main agent platform of the entire system or the platform in which a
      given container reside.
        - `hostip` - there are two cases:
            1. If `containerId` is specified - it is the IP address of the host that runs main container in which a give
               sub-container will reside
            2. If `containerId` is not specified - it is the IP address of the host running main container of the entire
               system
        - `platformid` - it is always the identifier of the platform that runs the main container of entire system
        - `inter` - there are two cases:
            1. If `containerId` is specified - it is the inter agent communication port of the host that runs main
               container in which a give sub-container will reside
            2. If `containerId` is not specified - it is the inter agent communication port of the host that runs main
               container of the entire system
    - `service.*` - specifies the configuration of various services running along with the agent system
        - `database.hostip` - IP address of the host running the database
        - `websocket.agentsip` - IP address of the host that runs WebSocket responsible for passing agent-related data
        - `websocket.clientsip` - IP address of the host that runs WebSocket responsible for passing client-related data
        - `websocket.managingip` - IP address of the host that runs WebSocket responsible for passing adaptation-related
          data
        - `websocket.networkip` - IP address of the host that runs WebSocket responsible for passing general network
          statistics data
        - `websocket.eventip` - IP address of the host that runs WebSocket responsible for passing external events to
          agents
    - `jade.*` - specifies additional configuration responsible for running JADE-related interfaces
        - `rungui` - flag indicating if the JADE GUI should be run by default along with the system start
        - `runsniffer` - flag indicating if the JADE Sniffer should be run by default along with the system start

#### Remarks:

1. `container.*` properties do not have to be specified when the system runs on a single machine. They are mostly
   ignored, as the system runs all agents in a single platform.

#### Example configuration:

The following example, will illustrate how to configure a system that is going to run on 4 different hosts:

1. _Host1_ (IP: 10.0.0.0) - running only main system container
2. _Host2_ (IP: 10.0.0.1) - running Cloud Network Agent and its Servers that do not have container specified
3. _Host3_ (IP: 10.0.0.2) - running remaining Servers of Cloud Network Agent that should reside in container of a
   specific name
4. _Host4_ (IP: 10.0.0.3) - running Client Agents

The configuration will focus mainly of the fields of `container.*` and `main.*` as these are the most complicated to
configure.
Let's assume the following network topology:

```
CNA1 (loactionId: Location1):
|-- Server1 (containerId: - )
|   |
|   |-- GreenSource1
|   |   |-- MonitoringAgent1
|   |
|   |-- GreenSource2
|   |   |-- MonitoringAgent2
|   
|-- Server2 (containerId: - )
|   |
|   |-- GreenSource3
|   |   |-- MonitoringAgent3
|   
|-- Server3 (containerId: Container1)
|   |
|   |-- GreenSource4
|   |   |-- MonitoringAgent4
|   
|-- Server4 (containerId: Container1)
|   |
|   |-- GreenSource5
|   |   |-- MonitoringAgent5
|   
```

The configuration of the _Host1_ will be as follows:

```
container.mainhost=true  

container.createnew=false   
container.locationId=Clients0  
container.containerId=r

container.platformid=MainPlatform
container.localhostip=10.0.0.0      

main.hostip=10.0.0.0                
main.platformid=MainPlatform
main.inter=7778        
```

This container will run _Scheduler Agent_ and _Managing Agent_.

Now, let us specify the configuration of _Host2_:

```
container.mainhost=false 

container.createnew=true
container.locationId=Location1
container.containerId=

container.platformid=CNA1
container.localhostip=10.0.0.1

main.hostip=10.0.0.0             
main.platformid=MainPlatform        
main.inter=7778                     
```

The container in _Host2_ will be created in a new agent platform with identifier _CNA1_ and will run _CNA1_, _Server1_
, _Server2_, _GreenSource1_, _GreenSource2_, _GreenSource3_, _Monitoring1_, _Monitoring2_, _Monitoring3_. By using
information of _Host1_ in `main.*` section, the container will be able to communicate with the DF of the main host (
hence, streamlining communication between agents).

Then, let us configure _Host3_:

```
container.mainhost=false 

container.createnew=false
container.locationId=Location1 
container.containerId=Container1

container.platformid=CNA1
container.localhostip=10.0.0.2

main.hostip=10.0.0.1             
main.platformid=MainPlatform        
main.inter=7778                     
```

The container will run _Server3_, _Server4_, _GreenSource3_, _GreenSource4_, _MonitoringAgent3_ and _MonitoringAgent4_.

Finally, the _Host4_ configuration will be as following:

```
container.mainhost=false 

container.createnew=true
container.locationId=Clients0 
container.containerId=

container.platformid=Clients0
container.localhostip=10.0.0.3

main.hostip=10.0.0.0             
main.platformid=MainPlatform        
main.inter=7778                     
```

The last container will run _Client Agents_ in a new agent platform with identifier _Client0_.

### ./scenarios

The directory `./scenarios` contains files that specify network topologies or scenario events. Both of these types of
files are in the _.xml_ format. They are passed to the system by indicating their names in configuration files as
described above.

#### Network topology configuration

All agent parameter are defined with _.xml_ name tags, whereas each type of agent has different type of tags.
The specification of the agents should be places inside _ScenarioStructureArgs_ tag:

```
<ScenarioStructureArgs> ... </ScenarioStructureArgs>
```

##### Managing Agent

- Parent tag: `<managingAgent> ... </managingAgent>`

```xml

<managingAgent>

    [required]
    <name>name of the agent</name>

    [required]
    <systemQualityThreshold>desired system quality (double)</systemQualityThreshold>

    [optional]
    <disabledActions>
        <item>name of the adaptation action enum</item>
        ...
    </disabledActions>

</managingAgent>
```

##### Scheduler Agent (SCHA)

- Parent tag: `<schedulerAgent> ... </schedulerAgent>`

```xml

<schedulerAgent>

    [required]
    <name>name of the agent</name>

    [required]
    <deadlineWeight>priority of job deadline in workload scheduling (int)</deadlineWeight>

    [required]
    <powerWeight>priority of job power in workload scheduling (int)</powerWeight>

    [required]
    <maximumQueueSize>maximal size of the queue (int)</maximumQueueSize>

    [required]
    <jobSplitThreshold>
        threshold of the job power beyond which the job is split into instances (int)
    </jobSplitThreshold>

    [required]
    <splittingFactor>number of job instances after split (int)</splittingFactor>

</schedulerAgent>
```

##### Monitoring Agent (MA)

- _Monitoring Agents_ are defined inside `<monitoringAgentsArgs> ... </monitoringAgentsArgs>` tag
- Parent tag of single agent: `<monitoringAgent> ... </monitoringAgent>`

```xml

<monitoringAgentsArgs>
    <monitoringAgent>

        [required]
        <name>name of the agent</name>

        [optional]
        <badStubProbability>
            probability with which Monitoring Agent can randomly stub bad weather
            conditions (only works if offline mode is on)
        </badStubProbability>

    </monitoringAgent>
    ...
</monitoringAgentsArgs>
```

##### Green Energy Agent (GSA)

- _Green Energy Agents_ are defined inside `<greenEnergyAgentsArgs> ... </greenEnergyAgentsArgs>` tag
- Parent tag of single agent: `<greenenergyAgent> ... </greenenergyAgent>`

```xml

<greenEnergyAgentsArgs>
    <greenenergyAgent>

        [required]
        <name>name of the agent</name>

        [required]
        <monitoringAgent>local name of connected monitoring agent</monitoringAgent>

        [required]
        <ownerSever>local name of connected server agent</ownerSever>

        [required]
        <latitude>latitude component of agent location (double)</latitude>

        [required]
        <longitude>longitude component of agent location (double)</longitude>

        [required]
        <pricePerPowerUnit>price per single unit of provided power (double)</pricePerPowerUnit>

        [required]
        <maximumCapacity>maximum capacity of produced energy (int)</maximumCapacity>

        [required]
        <energyType>type of energy source (available: WIND, SOLAR)</energyType>

        [required]
        <weatherPredictionError>
            error with which weather is predicted (double - between 0 and 1)
        </weatherPredictionError>

    </greenenergyAgent>
    ...
</greenEnergyAgentsArgs>
```

##### Server Agent (SA)

- _Server Agents_ are defined inside `<serverAgentsArgs> ... </serverAgentsArgs>` tag
- Parent tag of single agent: `<serverAgentsArgs> ... </serverAgentsArgs>`

```xml

<serverAgentsArgs>
    <serverAgentsArgs>

        [required]
        <name>name of the agent</name>

        [required]
        <ownerCloudNetwork>local name of parent cloud network agent</ownerCloudNetwork>

        [required]
        <maximumCapacity>maximum power capacity that can be used (int)</maximumCapacity>

        [required]
        <jobProcessingLimit>
            number of jobs that can be processed by the server at a single time (int)
        </jobProcessingLimit>

        [required]
        <price>price of power unit used in job execution (double)</price>

        [optional]
        <containerId>name of the container in which server should reside</containerId>

    </serverAgentsArgs>
    ...
</serverAgentsArgs>
```

##### Cloud Network Agent (CNA)

- _Cloud Network Agents_ are defined inside `<cloudNetworkAgentsArgs> ... </cloudNetworkAgentsArgs>` tag
- Parent tag of single agent: `<cloudNetworkAgent> ... </cloudNetworkAgent>`

```xml

<cloudNetworkAgentsArgs>
    <cloudNetworkAgent>

        [required]
        <name>name of the agent</name>

        [optional]
        <locationId>name of the region controlled by CNA</locationId>

    </cloudNetworkAgent>
    ...
</cloudNetworkAgentsArgs>
```

#### Scenario events configuration

Similarly to agents, events executed in the system are also defined with _.xml_ tags.
The main tag inside which the events are specified is:

```
<ScenarioEventsArgs> ... </ScenarioEventsArgs>
```

Currently, the system allows for 2 types of scenarios: _NEW_CLIENT_EVENT_ and _POWER_SHORTAGE_EVENT_.

1. _NEW_CLIENT_EVENT_ - introduces new client to the network
2. _POWER_SHORTAGE_EVENT_ - generates power fluctuation in a given network component

##### NEW_CLIENT_EVENT

- generated when the value of attribute **type** is NEW_CLIENT_EVENT

```xml

<event type="NEW_CLIENT_EVENT">

    [required]
    <occurrenceTime>
        second from the system start at which the event will be triggered
    </occurrenceTime>

    [required]
    <name>name of the created client (must be unique)</name>

    [required]
    <jobId>identifier of client's job (must be unique)</jobId>

    [required]
    <start>number of seconds after which the job execution should start (int)</start>

    [required]
    <end>number of seconds after which the job execution should end (int)</end>

    [required]
    <deadline>number of seconds after which the job execution reaches deadline (int)</deadline>

    [required]
    <power>power required for the job execution (int)</power>

</event>
```

##### POWER_SHORTAGE_EVENT

- generated when the value of attribute **type** is POWER_SHORTAGE_EVENT

```xml

<event type="POWER_SHORTAGE_EVENT">

    [required]
    <occurrenceTime>
        second from the system start at which the event will be triggered
    </occurrenceTime>

    [required]
    <agentName>
        name of the agent on which power fluctuation is triggered
        (can be triggered only on servers and green energy sources)
    </agentName>

    [required]
    <newMaximumCapacity>
        maximum capacity of the system component after triggering the event
    </newMaximumCapacity>

    [required]
    <isFinished>
        flag indicating if the event is a finish of power fluctuation or its start (boolean)
    </isFinished>

    [required]
    <cause>
        cause of the power fluctuation (available: PHYSICAL_CAUSE, WEATHER_CAUSE)
    </cause>

</event>
```

### ./examples

The directory `./examples` contains some old configuration files that can be used by the user as a reference point.

## green-cloud-ui

Module _green-cloud-ui_ contains a single configuration file that specifies the addresses of corresponding WebSockets
that are used by the GUI to communicate with _socket-server_:

- REACT_APP_WEB_SOCKET_AGENTS_FRONTEND_URL=http://`socket address`/frontend - IP address of the host that runs WebSocket
  responsible for passing agent-related data
- REACT_APP_WEB_SOCKET_CLIENTS_FRONTEND_URL=http://`socket address`/frontend - IP address of the host that runs
  WebSocket responsible for passing client-related data
- REACT_APP_WEB_SOCKET_MANAGING_FRONTEND_URL=http://`socket address`/frontend - IP address of the host that runs
  WebSocket responsible for passing adaptation-related
- REACT_APP_WEB_SOCKET_NETWORK_FRONTEND_URL=http://`socket address`/frontend - IP address of the host that runs
  WebSocket responsible for passing general network statistics data
- REACT_APP_WEB_SOCKET_EVENT_FRONTEND_URL=http://`socket address`/frontend - IP address of the host that runs WebSocket
  responsible for passing external events to agents

They should correspond to the addresses specified in the _engine_ configuration.

## socket-server

Module _socket-server_ also contains a single configuration file with only 1 property: `PORT` via which the user can
specify the port on which the WebSocket will be running (useful if someone would like to run two WebSockets on a single
machine).
