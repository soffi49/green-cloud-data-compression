import { CartesianGrid, Legend, XAxis, YAxis, Tooltip, XAxisProps, Text } from 'recharts'
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
   hasAxises?: boolean
   displayAllXAxis?: boolean
}

/**
 * Function renders and styles common chart properties which includes:
 * - YAxis,
 * - XAxis,
 * - CartesianGrid,
 * - Legend,
 * - Tooltip
 *
 * @param {boolean}[isTimeChart] - optional flag indicating if the chart uses time stamps on the x-axis
 * @param {number[]}[valueDomain] - optional property that defines the range of values displayed on y-axis
 * @param {func}[yAxisFormatter] - optional function that defines the formatting of values displayed on y-axis
 * @param {ContentType}[customContent] - optional modified content displayed inside the tooltip
 * @param {boolean}[displayAllXAxis] - optional flag indicating if all ticks on xAxis should be displayed
 *
 * @returns JSX.Element
 */
export const renderChartDescription = ({
   isTimeChart = true,
   valueDomain,
   yAxisFormatter,
   customContent,
   hasAxises = true,
   displayAllXAxis = false
}: Props) => {
   const tickFormatter = isTimeChart ? (date: Date | number) => convertTimeToString(date, true) : undefined
   const labelFormatter = (label: any) => (isTimeChart ? convertTimeToString(label as Date) : label)
   const xAxisStyle: XAxisProps = displayAllXAxis ? { ...styles.xAxis, interval: 0 } : styles.xAxis

   const CustomizedAxisTick = ({ x, y, payload }: any) => {
      return (
         <Text x={x} y={y} width={30} textAnchor="middle" fontSize={10} fontWeight={500} dy={10} verticalAnchor="start">
            {isTimeChart ? convertTimeToString(payload.value, true) : payload.value}
         </Text>
      )
   }

   return (
      <>
         {hasAxises && (
            <>
               <CartesianGrid strokeWidth={0.5} stroke="var(--gray-5)" vertical={false} />

               <YAxis {...styles.yAxis} domain={valueDomain} tickFormatter={yAxisFormatter}></YAxis>
               <XAxis
                  {...{
                     ...xAxisStyle,
                     dataKey: isTimeChart ? 'time' : 'name',
                     tick: CustomizedAxisTick,
                     tickFormatter
                  }}
               ></XAxis>
            </>
         )}
         <Legend {...styles.legend} />
         <Tooltip {...styles.tooltip} labelFormatter={labelFormatter} content={customContent} />
      </>
   )
}

/**
 * Function renders customized content of the tooltip displayed when hovering over a chart
 *
 * @param {Date}[label] - timestamp displayed as the tooltip label
 * @param {Payload<ValueType, NameType>[]}[payload] - chart content displayed in the tooltip
 * @param {LiveChartTooltip[]}[data] - additional content displayed in the tooltip
 * @returns JSX.Element
 */
export const renderCustomTooltipContent = (
   label: Date | string,
   payload: Payload<ValueType, NameType>[],
   data: LiveChartTooltip[]
) => {
   const unit = payload[0].unit
   return (
      <div style={styles.tooltipContainer}>
         <span>{typeof label === 'string' ? label : convertTimeToString(label)}</span>
         <p style={{ color: payload[0].color }}>
            <span>{unit ? [payload[0].name, `[${(unit as string).trim()}]`].join(' ') : payload[0].name} : </span>
            <span>{payload[0].value}</span>
         </p>
         {data.map((el) => (
            <div>
               <span>{el.name} : </span>
               <span>{typeof el.value === 'string' ? el.value : Math.round(el.value as number)}</span>
            </div>
         ))}
      </div>
   )
}
