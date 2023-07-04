import React from 'react'
import DetailsField from '../details-field/details-field'
import { styles } from './card-styles'

interface Props {
   children?: React.ReactNode
   header?: string | React.ReactNode
   subHeader?: string
   removeScroll?: boolean
   containerStyle?: React.CSSProperties
   contentStyle?: React.CSSProperties
}

/**
 * Component representing common container with shadow effect
 *
 * @param {object}[children] - content to be displayed inside the container
 * @param {string}[header] - header displayed at the top of the container
 * @param {string}[subHeader] - optional part of header displayed in the right side of the container
 * @param {boolean}[removeScroll] - flag indicating whether the scroll bar should be removed from the container
 * @param {object}[containerStyle] - optional styling applied to the container
 * @param {object}[contentStyle] - optional styling applied to the content
 *
 * @returns JSX Element
 */
const Card = ({ header, subHeader, children, containerStyle, contentStyle, removeScroll }: Props) => {
   const {
      cardContent,
      cardContainer,
      cardContentScroll,
      cardHeader,
      cardSubHeader,
      cardSubHeaderText,
      cardHeaderWithSubheader,
      cardHeaderContainer
   } = styles
   const contentFinalStyle = removeScroll
      ? { ...cardContent, ...contentStyle }
      : { ...cardContent, ...cardContentScroll }
   const parentContainerStyle = { ...cardContainer, ...containerStyle }
   const headerSubContainerStyle = { ...cardHeader, ...cardHeaderWithSubheader }

   const getHeaderWithSubHeader = () => {
      return (
         <div style={cardHeaderContainer}>
            <div style={headerSubContainerStyle}>{(header as string).toUpperCase()}</div>
            <DetailsField
               {...{
                  label: subHeader,
                  fieldContainerStyle: cardSubHeader,
                  fieldLabelStyle: cardSubHeaderText,
                  isHeader: true
               }}
            />
         </div>
      )
   }

   const mapHeader = () => {
      if (typeof header === 'string') {
         return subHeader ? getHeaderWithSubHeader() : <div style={cardHeader}>{header?.toUpperCase()}</div>
      }
      return header
   }

   return (
      <div style={parentContainerStyle}>
         {header && mapHeader()}
         <div style={contentFinalStyle}>{children}</div>
      </div>
   )
}

export default Card
