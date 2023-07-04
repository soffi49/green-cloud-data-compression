import { AdaptationAction, AdaptationActionGrouped, AdaptationGoal } from '@types'
import { convertMillisToString } from '@utils'

const groupAdaptations = (adaptations: AdaptationAction[]) =>
   adaptations.reduce(function (prev, curr) {
      if (prev.some((action) => action.name === curr.name)) {
         return prev.map((action) => {
            if (action.name === curr.name) {
               action.avgGoalQualities.push(curr.avgGoalQualities)
               return {
                  ...action,
                  runsNo: action.runsNo.concat(curr.runsNo),
                  avgDuration: action.avgDuration.concat(curr.avgDuration),
                  goal: action.goal.concat(curr.goal)
               }
            }
            return action
         })
      }
      const mappedCurr = {
         name: curr.name,
         runsNo: [curr.runsNo],
         avgDuration: [curr.avgDuration],
         avgGoalQualities: [curr.avgGoalQualities],
         goal: [curr.goal]
      }

      return [...prev, mappedCurr]
   }, [] as AdaptationActionGrouped[])

const getAdaptationExportHeaders = (systemGoals: AdaptationGoal[]) => {
   const goalsKeyLabels = systemGoals.map((goal) => ({ label: goal.name.toUpperCase(), key: goal.name.toUpperCase() }))

   return [
      { label: 'Adaptation name', key: 'name' },
      { label: 'Adaptation goal', key: 'goal' },
      { label: 'Runs no.', key: 'runsNo' },
      { label: 'Avg. Execution Duration', key: 'avgDuration' },
      ...goalsKeyLabels
   ]
}

const getAdaptationExportValues = (adaptations: AdaptationAction[]) => {
   return adaptations.map((adaptation) => ({
      ...adaptation,
      avgDuration: convertMillisToString(adaptation.avgDuration),
      ...getMappedAdaptationQualities(adaptation)
   }))
}

const getMappedAdaptationQualities = (adaptation: AdaptationAction) => {
   return adaptation.avgGoalQualities.reduce(
      (prev, curr) => ({ ...prev, [curr.name]: parseGoalQuality(curr.avgQuality) }),
      {}
   )
}

const parseGoalQuality = (quality: number) => `${Math.round(quality * 100)}%`

export { groupAdaptations, getAdaptationExportHeaders, getAdaptationExportValues, parseGoalQuality }
