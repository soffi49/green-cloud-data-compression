import React from 'react'

interface Styles {
   checkContainer: React.CSSProperties
   headerStyle: React.CSSProperties
}

export const styles: Styles = {
   headerStyle: {
      fontSize: 'var(--font-size-4)',
      fontFamily: 'var(--font-1)',
      color: 'var(--gray-3)',
      fontWeight: 600,
      paddingLeft: '5px',
      paddingBottom: '10px'
   },
   checkContainer: {
      marginBottom: '10px',
      display: 'table',
      marginLeft: '5px',
      textOverflow: 'ellipsis'
   }
}
