import React from 'react'
import './agent-statistics-config'
import DetailsField from '../../../common/details-field/details-field'
import {
   getAgentFields,
   getStatisticsMapForAgent,
   MAP_TYPE,
   NETWORK_AGENTS,
   PERCENTAGE_VALUES,
} from './agent-statistics-config'
import SubtitleContainer from '../../../common/subtitle-container/subtitle-container'

import { Agent } from '@types'
import Badge from 'components/common/badge/badge'
import { styles } from './agent-statistics-styles'
import { Header } from 'components/common'

interface Props {
   selectedAgent?: Agent | null
}

const description = 'Click on an agent to display its statistics'

/**
 * Component represents panel gathering all information about given agent
 *
 * @param {Agent}[selectedAgent] - agent for which the statistics are displayed
 * @returns JSX Element
 */
const AgentStatisticsPanel = ({ selectedAgent }: Props) => {
   const includeQualityMap = selectedAgent ? NETWORK_AGENTS.includes(selectedAgent.type) : false
   const { fieldWrapper } = styles

   const mapToStatistics = (agent: Agent, statisticsMap: any[]) => {
      return statisticsMap.map((field) => {
         const { label, key } = field
         const agentFields = getAgentFields(agent)
         const agentValue = { ...(agentFields as any) }[key] ?? ''
         const value = formatAgentValue(agentValue, key)
         const property = key === 'isActive' ? 'valueObject' : 'value'

         return <DetailsField key={key} {...{ label, [property]: value }} />
      })
   }

   const formatAgentValue = (value: string | number, key: string) => {
      if (key === 'isActive') return <Badge text={value as string} isActive={value === 'ACTIVE'} />

      return PERCENTAGE_VALUES.includes(key) && value
         ? [(value as number).toFixed(2), '%'].join('')
         : value !== ''
         ? value
         : 0
   }

   const generateDetailsFields = (type?: string) => {
      if (selectedAgent) {
         const map = getStatisticsMapForAgent(selectedAgent, type)
         const header = type ? MAP_TYPE.QUALITY : MAP_TYPE.STATE
         return (
            <div>
               <div style={fieldWrapper}>
                  <Header {...{ text: header }} />
                  {mapToStatistics(selectedAgent, map)}
               </div>
            </div>
         )
      } else {
         return <SubtitleContainer text={description} />
      }
   }

   return (
      <div>
         {generateDetailsFields()}
         {includeQualityMap && generateDetailsFields(MAP_TYPE.QUALITY)}
      </div>
   )
}

export default AgentStatisticsPanel
