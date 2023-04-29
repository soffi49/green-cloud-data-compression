import { CartesianGrid, Legend, XAxis, YAxis, Tooltip } from 'recharts'
import { styles } from './live-chart-generic-styles'
import { convertTimeToString } from 'utils/time-utils'
import { NameType, Payload, ValueType } from 'recharts/types/component/DefaultTooltipContent'
import { ContentType } from 'recharts/types/component/Tooltip'
import { LiveChartTooltip } from '@types'

interface Props {
   isTimeChart?: boolean
   valueDomain?: number[]
   yAxisFormatter?: (data: any) => string
   customContent?: ContentType<ValueType, NameType>
}

export const renderCustomTooltipContent = (
   label: Date,
   payload: Payload<ValueType, NameType>[],
   data: LiveChartTooltip[]
) => {
   return (
      <div style={styles.tooltipContainer}>
         <span>{convertTimeToString(label)}</span>
         <p style={{ color: payload[0].color }}>
            <span>{[payload[0].name, payload[0].unit].join(' ')} : </span>
            <span>{payload[0].value}</span>
         </p>
         {data.map((el) => (
            <div>
               <span>{el.name} : </span>
               <span>{Math.round(el.value as number)}</span>
            </div>
         ))}
      </div>
   )
}

export const renderChartDescription = ({ isTimeChart = true, valueDomain, yAxisFormatter, customContent }: Props) => {
   const formatData = isTimeChart ? (date: Date | number) => convertTimeToString(date, false) : undefined
   return (
      <>
         <CartesianGrid strokeWidth={0.5} stroke="var(--gray-5)" vertical={false} />
         <YAxis {...styles.yAxis} domain={valueDomain} tickFormatter={yAxisFormatter}></YAxis>
         <XAxis
            {...{
               ...styles.xAxis,
               dataKey: isTimeChart ? 'time' : 'name',
               tickFormatter: formatData,
            }}
         ></XAxis>
         <Legend {...styles.legend} />
         <Tooltip
            labelFormatter={(label: any) => (isTimeChart ? convertTimeToString(label as Date) : label)}
            offset={10}
            wrapperStyle={{ zIndex: 10000 }}
            allowEscapeViewBox={{ y: true }}
            content={customContent}
         />
      </>
   )
}
