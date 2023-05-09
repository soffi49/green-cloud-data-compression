enum JOB_STATUSES {
    CREATED = 'CREATED',
    PROCESSED = 'PROCESSED',
    IN_PROGRESS = 'IN PROGRESS',
    DELAYED = 'DELAYED',
    FINISHED = 'FINISHED',
    ON_BACK_UP = 'ON BACK UP',
    ON_HOLD = 'ON HOLD',
    REJECTED = 'REJECTED',
    FAILED = 'FAILED',
}

enum AGENT_TYPES {
    CLOUD_NETWORK = 'CLOUD_NETWORK',
    CLIENT = 'CLIENT',
    SERVER = 'SERVER',
    GREEN_ENERGY = 'GREEN_ENERGY',
    MONITORING = 'MONITORING',
    SCHEDULER = 'SCHEDULER'
}

enum EVENT_STATE {
    ACTIVE = 'ACTIVE',
    INACTIVE = 'INACTIVE'
}

enum EVENT_TYPE{
    POWER_SHORTAGE_EVENT = 'POWER_SHORTAGE_EVENT'
}

const WELCOMING_MESSAGE = {
    type: 'SOCKET_CONNECTED',
    data: 'Connection to the socket established successfully'
}

const ROUTE_TYPES = {
    FRONT: '/frontend',
}

const INITIAL_POWER_SHORTAGE_STATE = {
    state: EVENT_STATE.ACTIVE,
    disabled: false,
    type: EVENT_TYPE.POWER_SHORTAGE_EVENT,
    occurenceTime: null,
    data: null
}

const INITIAL_NETWORK_AGENT_STATE = (data) => {
    return ({
        initialMaximumCapacity: data.maximumCapacity,
        currentMaximumCapacity: data.maximumCapacity,
        traffic: 0,
        numberOfExecutedJobs: 0,
        numberOfJobsOnHold: 0,
    })
}

const REPORTING_TIME = 1

export {
    JOB_STATUSES,
    AGENT_TYPES,
    EVENT_STATE,
    EVENT_TYPE,
    WELCOMING_MESSAGE,
    ROUTE_TYPES,
    INITIAL_POWER_SHORTAGE_STATE,
    REPORTING_TIME,
    INITIAL_NETWORK_AGENT_STATE
}