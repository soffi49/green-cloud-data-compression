import { AdaptationAction, AdaptationActionGrouped, AdaptationGoal, AdaptationGoalAvgQuality } from '@types'
import React from 'react'
import { styles } from './adaptation-action-table-styles'
import { convertMillisToString, getSum } from '@utils'
import { Badge } from 'components/common'
import { getColorByName } from 'components/live-panel/config/live-panel-config'
import {
   getAdaptationExportHeaders,
   getAdaptationExportValues,
   groupAdaptations,
   parseGoalQuality
} from './adaptation-action-table-config'
import { CSVLink } from 'react-csv'

interface Props {
   adaptations: AdaptationAction[]
   systemGoals: AdaptationGoal[]
}

const headerTable = 'Detailed statistics of adaptation actions'

/**
 * Component represents a table summarizing adaptation actions' statistics
 *
 * @param {AdaptationAction[]}[adaptations] - adaptation actions data
 * @param {AdaptationGoal[]}[systemGoals] - goals defined in the system
 *
 * @returns JSX Element
 */
const AdaptationActionTable = ({ adaptations, systemGoals }: Props) => {
   const { tableWrapper, header, table, tableHeader, tableColumn, buttonWrapper } = styles
   const nameStyle = { ...tableColumn, fontWeight: 600 }

   const goals = systemGoals.map((goal) => goal.name)
   const groupeAdaptations = groupAdaptations(adaptations).sort((a, b) => getSum(b.runsNo) - getSum(a.runsNo))

   const getTableHead = () => (
      <>
         <th style={tableHeader}>{'Adaptation name'.toUpperCase()}</th>
         <th style={tableHeader}>{'Goal'.toUpperCase()}</th>
         <th style={tableHeader}>{'Runs No.'.toUpperCase()}</th>
         <th style={tableHeader}>{'Avg. duriation'.toUpperCase()}</th>
         {goals.map((goal) => (
            <th style={tableHeader}>{`Diff. in ${goal}`.toUpperCase()}</th>
         ))}
      </>
   )

   const getTableBody = () => <>{groupeAdaptations.map((adaptation) => getTableRow(adaptation))}</>

   const getTableRow = (adaptation: AdaptationActionGrouped) => {
      const goalsNo = adaptation.goal.length
      return Object.entries(adaptation.goal).map((goal, idx) => {
         return (
            <tr>
               {idx === 0 && (
                  <td rowSpan={goalsNo} style={nameStyle}>
                     {adaptation.name.toUpperCase()}
                  </td>
               )}
               <td style={tableColumn}>
                  <Badge text={goal[1]} color={getColorByName(goal[1])} isSmall={true} />
               </td>
               <td style={tableColumn}>{adaptation.runsNo[idx]}</td>
               <td style={tableColumn}>{convertMillisToString(adaptation.avgDuration[idx])}</td>
               {getGoalRows(adaptation.avgGoalQualities[idx])}
            </tr>
         )
      })
   }

   const getGoalRows = (goalQualities: AdaptationGoalAvgQuality[]) => {
      return goals.map((goal) => (
         <td style={tableColumn}>
            {parseGoalQuality(
               goalQualities.filter((quality) => quality.name === goal.toUpperCase())[0]?.avgQuality ?? 0
            )}
         </td>
      ))
   }

   const getDownloadCSVButton = () => (
      <CSVLink
         {...{
            data: getAdaptationExportValues(adaptations),
            headers: getAdaptationExportHeaders(systemGoals),
            filename: 'adaptation-actions-statistics.csv',
            className: 'common-button medium-green-button medium-green-button-active'
         }}
      >
         {'Download statistics'.toUpperCase()}
      </CSVLink>
   )

   return (
      <div style={tableWrapper}>
         <div
            style={{
               display: 'flex',
               flexDirection: 'row',
               alignItems: 'center',
               justifyContent: 'space-between',
               padding: '5px 10px'
            }}
         >
            <div style={header}>{headerTable.toUpperCase()}</div>
            <div style={buttonWrapper}>{getDownloadCSVButton()}</div>
         </div>
         <table style={table}>
            <thead>
               <tr>{getTableHead()}</tr>
            </thead>
            <tbody>{getTableBody()}</tbody>
         </table>
      </div>
   )
}

export default AdaptationActionTable
