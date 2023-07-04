import AdaptationGoals from './adaptation-goals/adaptation-goals-connected'
import AdaptationLog from './adaptation-log/adaptation-log-connected'
import AdaptationStatistics from './adaptation-statistics/adaptation-statistics-connected'

export interface TabProps {
   title: string
   isOpen: boolean
   panel: React.ReactNode
}

export const INITIAL_TABS: TabProps[] = [
   { title: 'Adaptation Statistics', isOpen: false, panel: <AdaptationStatistics /> },
   { title: 'Adaptation Log', isOpen: false, panel: <AdaptationLog /> },
   { title: 'Adaptation Goals', isOpen: false, panel: <AdaptationGoals /> }
]

const GOAL_TO_QUALITY_MAP = [
   { label: 'Job success ratio quality', key: 'goalId1' },
   { label: 'Back-up power usage quality', key: 'goalId2' },
   { label: 'Traffic distribution quality', key: 'goalId3' }
]

export const ADAPTATION_STATISTICS_FIELDS = [
   { label: 'System Quality Indicatior', key: 'systemIndicator' },
   ...GOAL_TO_QUALITY_MAP,
   { label: 'Number of performed adaptations', key: 'performedAdaptations' },
   { label: 'Number of weak adaptations', key: 'weakAdaptations' },
   { label: 'Number of strong adaptations', key: 'strongAdaptations' }
]

export const COUNTERS = ['performedAdaptations', 'weakAdaptations', 'strongAdaptations']
