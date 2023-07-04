import React from 'react'
import { IconProps } from '@types'
/**
 * Svg decrease arrow icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @param {string}[color] - optional icon fill
 * @returns JSX object representing svg icon
 */
const DecreaseArrowIcon = ({ size, color }: IconProps) => {
   const fill = color ?? '#ffffff'
   return (
      <svg
         xmlns="http://www.w3.org/2000/svg"
         viewBox="0 0 122.88 68.04"
         {...{
            width: size,
            height: size
         }}
      >
         <g>
            <path
               {...{ fill }}
               d="M2.03,11.52C-0.63,8.94-0.68,4.69,1.9,2.03c2.58-2.66,6.83-2.72,9.49-0.13l27.65,26.98L62.16,6.57 c2.67-2.57,6.92-2.49,9.49,0.18l37.77,38.22V25.7c0-3.72,3.01-6.73,6.73-6.73s6.73,3.01,6.73,6.73v35.63h-0.02 c0,1.74-0.67,3.47-2,4.78c-1.41,1.39-3.29,2.03-5.13,1.91H82.4c-3.72,0-6.73-3.01-6.73-6.73c0-3.72,3.01-6.73,6.73-6.73h17.63 L66.7,20.84L43.67,43.07c-2.6,2.5-6.73,2.51-9.33-0.03L2.03,11.52L2.03,11.52z"
            />
         </g>
      </svg>
   )
}

export default DecreaseArrowIcon
