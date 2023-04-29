import React from 'react'
import { XAxisProps, YAxisProps } from 'recharts'
import { CategoricalChartProps } from 'recharts/types/chart/generateCategoricalChart'

interface Styles {
   chartContainer: any
   chart: CategoricalChartProps
   xAxis: XAxisProps
   yAxis: YAxisProps
   legend: any
   tooltipContainer: React.CSSProperties
}

export const styles: Styles = {
   chartContainer: {
      width: '100%',
      height: '90%',
   },
   chart: {
      margin: {
         top: 30,
         right: 50,
         left: 0,
         bottom: 10,
      },
   },
   xAxis: {
      dy: 18,
      tickSize: 0,
      strokeWidth: 0,
      padding: 'gap',
      stroke: 'var(--gray-2)',
      fontSize: 10,
      fontWeight: 500,
      interval: 'preserveStartEnd',
   },
   yAxis: {
      dx: -18,
      tickSize: 0,
      tickCount: 10,
      minTickGap: 10,
      fontSize: 12,
      strokeWidth: 0,
      fontWeight: 500,
      stroke: 'var(--gray-2)',
   },
   legend: {
      layout: 'horizontal',
      iconType: 'circle',
      iconSize: 6,
      align: 'left',
      verticalAlign: 'top',
      wrapperStyle: {
         top: -10,
         left: 25,
      },
      formatter: (val: any, entry: any) => (
         <span
            style={{
               fontSize: 'var(--font-size-6)',
               fontWeight: 300,
            }}
         >
            {entry.value?.toLocaleUpperCase()}
         </span>
      ),
   },
   tooltipContainer: {
      backgroundColor: 'var(--white)',
      border: '1px solid var(--gray-5)',
      padding: '15px',
   },
}
