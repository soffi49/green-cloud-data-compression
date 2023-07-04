import React from 'react'
import { IconProps } from '@types'

/**
 * Svg inform icon
 *
 * @param {string}[size] - icon size passed as string (eg. '2px', '10%')
 * @param {string}[color] - optional icon fill
 * @param {string | undefined}[className] - optional name of styles class
 * @returns JSX object representing svg icon
 */
const IconInfo = ({ size, color, className = '' }: IconProps) => {
   const fill = color ?? '#5e5e5e'
   return (
      <svg
         xmlns="http://www.w3.org/2000/svg"
         viewBox="0 0 58 58"
         {...{
            width: size,
            height: size,
            fill,
            className
         }}
      >
         <g id="surface249774927">
            <path
               style={{
                  stroke: 'none',
                  fillRule: 'nonzero',
                  fill,
                  fillOpacity: 1
               }}
               d="M31.171 34h-11.179c-.536 0-.992.448-.992 1 0 .556.444 1 .992 1h11.179c.412 1.165 1.523 2 2.829 2 1.306 0 2.417-.835 2.829-2h1.179c.536 0 .992-.448.992-1 0-.556-.444-1-.992-1h-1.179c-.412-1.165-1.523-2-2.829-2-1.306 0-2.417.835-2.829 2zm-3.341-5h10.179c.536 0 .992-.448.992-1 0-.556-.444-1-.992-1h-10.179c-.412-1.165-1.523-2-2.829-2-1.306 0-2.417.835-2.829 2h-2.179c-.536 0-.992.448-.992 1 0 .556.444 1 .992 1h2.179c.412 1.165 1.523 2 2.829 2 1.306 0 2.417-.835 2.829-2zm3.341-9h-11.179c-.536 0-.992.448-.992 1 0 .556.444 1 .992 1h11.179c.412 1.165 1.523 2 2.829 2 1.306 0 2.417-.835 2.829-2h1.179c.536 0 .992-.448.992-1 0-.556-.444-1-.992-1h-1.179c-.412-1.165-1.523-2-2.829-2-1.306 0-2.417.835-2.829 2zm2.829 2c.552 0 1-.448 1-1s-.448-1-1-1-1 .448-1 1 .448 1 1 1zm-9 5c-.552 0-1 .448-1 1s.448 1 1 1 1-.448 1-1-.448-1-1-1zm9 9c.552 0 1-.448 1-1s-.448-1-1-1-1 .448-1 1 .448 1 1 1z"
            />
         </g>
      </svg>
   )
}

export default IconInfo
