import React from 'react'
import { TooltipProps, XAxisProps, YAxisProps } from 'recharts'
import { CategoricalChartProps } from 'recharts/types/chart/generateCategoricalChart'
import { NameType, ValueType } from 'recharts/types/component/DefaultTooltipContent'

interface Styles {
   chartContainer: any
   chart: CategoricalChartProps
   xAxis: XAxisProps
   yAxis: YAxisProps
   legend: any
   tooltip: TooltipProps<ValueType, NameType>
   tooltipContainer: React.CSSProperties
}

export const styles: Styles = {
   chartContainer: {
      width: '100%',
      height: '90%'
   },
   chart: {
      margin: {
         top: 30,
         right: 50,
         left: 0,
         bottom: 10
      }
   },
   xAxis: {
      tickSize: 0,
      tickCount: 10,
      minTickGap: -50,
      strokeWidth: 0,
      padding: 'gap',
      stroke: 'var(--gray-2)',
      width: 150
   },
   yAxis: {
      dx: -18,
      tickSize: 0,
      tickCount: 10,
      minTickGap: 10,
      fontSize: 12,
      strokeWidth: 0,
      fontWeight: 500,
      width: 70,
      stroke: 'var(--gray-2)'
   },
   tooltip: {
      offset: 10,
      wrapperStyle: { zIndex: 10000 },
      allowEscapeViewBox: { y: true }
   },
   legend: {
      layout: 'horizontal',
      iconType: 'circle',
      iconSize: 6,
      align: 'left',
      verticalAlign: 'top',
      wrapperStyle: {
         top: -10,
         left: 25
      },
      formatter: (_: any, entry: any) => (
         <span
            style={{
               fontSize: 'var(--font-size-10)',
               fontWeight: 400
            }}
         >
            {entry.value?.toLocaleUpperCase()}
         </span>
      )
   },
   tooltipContainer: {
      backgroundColor: 'var(--white)',
      border: '1px solid var(--gray-5)',
      padding: '15px'
   }
}
