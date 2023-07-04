import React from 'react'

interface Styles {
   modalContainer: React.CSSProperties
   singleEventParentContainer: React.CSSProperties
   headerContainer: React.CSSProperties
}

export const styles: Styles = {
   modalContainer: {
      backgroundColor: 'var(--white)',
      height: '55%',
      width: '30%'
   },
   singleEventParentContainer: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'flex-start',
      minWidth: 'fit-content',
      marginTop: '20px'
   },
   headerContainer: {
      width: 'none'
   }
}
