import { ReportEntry } from "../../types"

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

interface ManagingSystemReportsState {
    jobSuccessRatioReport: ReportEntry[]
    trafficDistributionReport: ReportEntry[]
    backUpPowerUsageReport: ReportEntry[]
}

let MANAGING_SYSTEM_REPORTS: ManagingSystemReportsState = {
    jobSuccessRatioReport: [],
    trafficDistributionReport: [],
    backUpPowerUsageReport: []
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

const resetManagingSystemReportsState = () =>
    Object.assign(MANAGING_SYSTEM_REPORTS,
        ({
            jobSuccessRatioReport: [],
            trafficDistributionReport: [],
            backUpPowerUsageReport: []
        }))

export {
    AdaptationGoal,
    AdaptationGoalQuality,
    AdaptationLog,
    ManagingSystemState,
    MANAGING_SYSTEM_STATE,
    MANAGING_SYSTEM_REPORTS,
    resetManagingSystemState,
    resetManagingSystemReportsState
}