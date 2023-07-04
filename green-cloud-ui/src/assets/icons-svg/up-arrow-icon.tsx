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
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024" {...{ width: size, height: size }}>
         <g id="surface249774927">
            <path
               style={{
                  stroke: 'none',
                  fillRule: 'nonzero',
                  fill
               }}
               d="M903.232 768l56.768-50.432L512 256l-448 461.568 56.768 50.432L512 364.928z"
            />
         </g>
      </svg>
   )
}

export default IconCloudMenu
