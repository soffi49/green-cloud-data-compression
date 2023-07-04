import React from 'react'

interface Styles {
   mainContainer: React.CSSProperties
   contentContainer: React.CSSProperties
   headerContainer: React.CSSProperties
   headerStyle: React.CSSProperties
}

export const styles: Styles = {
   mainContainer: {
      flexGrow: 1,
      backgroundColor: 'var(--white)',
      height: '100%',
      marginLeft: '20px',
      paddingBottom: '20px'
   },
   contentContainer: {
      marginTop: '10px'
   },
   headerContainer: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingBottom: '10px'
   },
   headerStyle: {
      color: 'var(--gray-2)',
      fontWeight: 500,
      marginRight: '10px'
   }
}
