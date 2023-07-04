import React from 'react'

interface Styles {
   modalStyle: React.CSSProperties
   modalHeader: React.CSSProperties
   wrapper: React.CSSProperties
   indicatorsContent: React.CSSProperties
   contentWrapper: React.CSSProperties
   contentContainer: React.CSSProperties
   headerContainer: React.CSSProperties
   headerText: React.CSSProperties
}

export const styles: Styles = {
   modalStyle: {
      width: '80%',
      height: '90%',
      paddingTop: '10px',
      overflowX: 'hidden'
   },
   modalHeader: {
      border: 'none',
      fontWeight: 600,
      paddingLeft: '10px',
      paddingTop: '10px',
      color: 'var(--gray-3)',
      fontSize: 'var(--font-size-11)'
   },
   wrapper: {
      height: '95%',
      width: '99%',
      display: 'grid',
      gridTemplateColumns: '34% 64%',
      gridTemplateRows: '1fr, 1fr',
      overflow: 'hidden',
      gap: '10px',
      padding: '10px 5px'
   },
   contentContainer: {
      overflowY: 'auto',
      overflowX: 'hidden',
      height: '100%',
      margin: '0px 5px 10px 5px'
   },
   contentWrapper: {
      width: '100%',
      borderRadius: '20px',
      boxShadow: 'var(--card-shadow)',
      display: 'flex',
      flexDirection: 'column',
      overflow: 'hidden'
   },
   indicatorsContent: {
      overflowY: 'auto',
      overflowX: 'hidden',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      width: '100%',
      paddingBottom: '30px'
   },
   headerText: {
      padding: '10px 20px'
   },
   headerContainer: {
      borderTopLeftRadius: '20px',
      borderTopRightRadius: '20px',
      width: '100%',
      color: 'var(--white)',
      fontSize: 'var(--font-size-4)',
      fontWeight: 500,
      backgroundColor: 'var(--gray-12)'
   }
}
