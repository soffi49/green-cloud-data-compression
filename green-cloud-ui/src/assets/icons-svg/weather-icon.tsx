import React from 'react'
import { IconProps } from '@types'

/**
 * Svg weather icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @returns JSX object representing svg icon
 */
const IconWeather = ({ size }: IconProps) => {
   return (
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 30 30" {...{ width: size, height: size }}>
         <g id="surface285715578">
            <path
               style={{
                  stroke: 'none',
                  fillRule: 'nonzero',
                  fill: '#429647',
                  fillOpacity: 1
               }}
               d="M 46 14 L 46 36 C 46 41.515625 41.515625 46 36 46 L 14 46 C 8.484375 46 4 41.515625 4 36 L 4 14 C 4 8.484375 8.484375 4 14 4 L 36 4 C 41.515625 4 46 8.484375 46 14 Z M 32.5 13 C 29.371094 13 26.679688 14.921875 25.558594 17.648438 C 27.199219 18.320312 28.621094 19.480469 29.601562 21 C 33.078125 21.039062 36.058594 23.179688 37.339844 26.21875 C 38.96875 24.851562 40 22.789062 40 20.5 C 40 16.359375 36.640625 13 32.5 13 Z M 29.5 23 C 29.144531 23 28.796875 23.035156 28.453125 23.09375 C 27.421875 20.6875 25.035156 19 22.25 19 C 18.773438 19 15.914062 21.628906 15.542969 25.003906 C 15.527344 25.003906 15.515625 25 15.5 25 C 12.460938 25 10 27.460938 10 30.5 C 10 30.613281 10.011719 30.722656 10.015625 30.832031 C 10.015625 30.890625 10 30.941406 10 31 C 10 33.761719 12.238281 36 15 36 L 29 36 L 29 35.976562 C 29.164062 35.988281 29.332031 36 29.5 36 C 33.089844 36 36 33.089844 36 29.5 C 36 25.910156 33.089844 23 29.5 23 Z M 29.5 23 "
            />
         </g>
      </svg>
   )
}

export default IconWeather
