import React from 'react'

interface Styles {
   headerStyle: React.CSSProperties
}

export const styles: Styles = {
   headerStyle: {
      marginBottom: '10px',
      fontSize: 'var(--font-size-4)',
      padding: '0px 15px 5px 15px',
      borderBottom: 'var(--border-gray)',
      fontWeight: 400,
      width: '50%',
   },
}
