import React from 'react'

interface Styles {
   selectorContainer: React.CSSProperties
   iconContainer: React.CSSProperties
}

export const styles: Styles = {
   selectorContainer: {
      display: 'flex',
      width: '100%',
      alignItems: 'center'
   },
   iconContainer: {
      cursor: 'pointer',
      marginRight: '2px'
   }
}
