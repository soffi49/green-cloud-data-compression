import { IconElement } from 'types/assets'
import { LiveIndicatorAvgGenerator, LiveIndicatorAvgGeneratorType } from '../live-indicator-generator'
import React from 'react'

export interface LiveIndicatorConfiguration {
   title: string
   type: LiveIndicatorAvgGeneratorType
   value: LiveIndicatorAvgGenerator
   icon?: IconElement
   indicator: React.ElementType<{
      title: string
      value: number | string
      icon?: IconElement
   }>
}
