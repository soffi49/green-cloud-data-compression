import { AdaptationGoalAvgQuality } from './adaptation-goal-avg-quality'

export interface AdaptationActionGrouped {
   name: string
   goal: string[]
   runsNo: number[]
   avgGoalQualities: AdaptationGoalAvgQuality[][]
   avgDuration: number[]
}
