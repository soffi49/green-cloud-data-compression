type AdaptationGoalQuality = {
    id: number
    quality: number
}

enum LogType {
    RECONFIGURE = 'RECONFIGURE' as any,
    ADD_COMPONENT = 'ADD_COMPONENT' as any,
    REMOVE_COMPONENT = 'REMOVE_COMPONENT' as any,
 }

type AdaptationLog = {
    type: LogType
    description: string
    agentName?: string
    time: number
}

type AdaptationGoal = {
    id: number
    name: string
    threshold: number
    isAboveThreshold: boolean
    weight: number
}


interface ManagingSystemState {
    systemIndicator: number
    goalQualityIndicators: AdaptationGoalQuality[]
    performedAdaptations: number
    weakAdaptations: number
    strongAdaptations: number
    adaptationLogs: AdaptationLog[]
    adaptationGoals: AdaptationGoal[]
}

let MANAGING_SYSTEM_STATE: ManagingSystemState = {
    systemIndicator: 0,
    goalQualityIndicators: [],
    performedAdaptations: 0,
    weakAdaptations: 0,
    strongAdaptations: 0,
    adaptationLogs: [],
    adaptationGoals: []
}

const resetManagingSystemState = () =>
  Object.assign(MANAGING_SYSTEM_STATE,
    ({
        systemIndicator: 0,
        goalQualityIndicators: [],
        performedAdaptations: 0,
        weakAdaptations: 0,
        strongAdaptations: 0,
        adaptationLogs: [],
        adaptationGoals: []
    }))

export {
    AdaptationGoal,
    AdaptationGoalQuality,
    AdaptationLog,
    ManagingSystemState,
    MANAGING_SYSTEM_STATE,
    resetManagingSystemState
}