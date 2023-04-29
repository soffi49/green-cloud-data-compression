import React from 'react'
import { IconProps } from '@types'

/**
 * Svg cloud icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @param {string}[color] - optional icon fill
 * @returns JSX object representing svg icon
 */
const IconCloudMenu = ({ size, color }: IconProps) => {
   const fill = color ?? '#ffffff'
   return (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 -1.5 35 35" {...{ width: size, height: size }}>
         <g id="surface249774927">
            <path
               style={{
                  stroke: 'none',
                  fillRule: 'nonzero',
                  fill,
                  fillOpacity: 0.9,
               }}
               d="M27.873 28c0 0 5.52 0.006 6.295-5.395 0.369-5.906-5.336-7.070-5.336-7.070s0.649-8.743-7.361-9.74c-6.865-0.701-8.954 5.679-8.954 5.679s-2.068-1.988-4.873-0.364c-2.511 1.55-2.067 4.388-2.067 4.388s-5.577 1.084-5.577 6.768c0.125 5.677 6.057 5.734 6.057 5.734"
            />
         </g>
      </svg>
   )
}

export default IconCloudMenu
