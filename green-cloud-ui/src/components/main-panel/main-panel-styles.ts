import React from 'react'

interface Styles {
   mainContainer: React.CSSProperties
}

export const styles: Styles = {
   mainContainer: {
      flexGrow: 1,
      backgroundColor: 'var(--white)',
      height: '100%',
      marginLeft: '20px',
      minWidth: 'fit-content',
      paddingBottom: '20px',
   },
}
