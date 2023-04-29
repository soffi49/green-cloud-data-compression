import React from 'react'
import { IconProps } from '@types'

/**
 * Svg menu icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @param {string}[color] - optional icon fill
 * @returns JSX object representing svg icon
 */
const IconMenu = ({ size, color }: IconProps) => {
   const fill = color ?? '#ffffff'
   return (
      <svg
         xmlns="http://www.w3.org/2000/svg"
         viewBox="0 0 24 24"
         {...{
            width: size,
            height: size,
            fill,
            opacity: 0.8,
         }}
      >
         <g id="surface249774927">
            <path
               fillRule="evenodd"
               clipRule="evenodd"
               d="M3 6C3 5.44772 3.44772 5 4 5H20C20.5523 5 21 5.44772 21 6C21 6.55228 20.5523 7 20 7H4C3.44772 7 3 6.55228 3 6ZM3 12C3 11.4477 3.44772 11 4 11H20C20.5523 11 21 11.4477 21 12C21 12.5523 20.5523 13 20 13H4C3.44772 13 3 12.5523 3 12ZM3 18C3 17.4477 3.44772 17 4 17H20C20.5523 17 21 17.4477 21 18C21 18.5523 20.5523 19 20 19H4C3.44772 19 3 18.5523 3 18Z"
               fill={fill}
            />
         </g>
      </svg>
   )
}

export default IconMenu
