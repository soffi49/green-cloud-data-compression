import React from 'react'

interface Styles {
   containerStyle: React.CSSProperties
}

export const styles: Styles = {
   containerStyle: {
      flexShrink: 0,
      backgroundColor: 'var(--white)',
      height: '100%',
      marginRight: '10px',
      minWidth: 'fit-content',
      boxShadow: 'none',
   },
}
