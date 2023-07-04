import React from 'react'

interface Styles {
   modalStyle: ReactModal.Styles
   modalTitle: React.CSSProperties
   modalHeaderStyle: React.CSSProperties
   mainContainer: React.CSSProperties
   contentWrapper: React.CSSProperties
   nested: React.CSSProperties
}

export const styles: Styles = {
   modalStyle: {
      overlay: {
         backgroundColor: 'var(--black-transparent)'
      },
      content: {
         margin: 'auto',
         borderRadius: 15,
         display: 'flex',
         flexDirection: 'column',
         minWidth: 'fit-content',
         overflowY: 'hidden'
      }
   },
   modalTitle: {
      fontSize: 'var(--font-size-8)',
      fontFamily: 'var(--font-1)',
      fontWeight: 400,
      color: 'var(--gray-2)',
      width: '100%',
      borderBottom: 'var(--border-thin-light-gray)',
      paddingBottom: '5px',
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      textAlign: 'center'
   },
   modalHeaderStyle: {
      marginRight: '20px',
      textAlign: 'left',
      display: 'flex',
      alignItems: 'center'
   },
   mainContainer: {
      overflowY: 'auto',
      height: '100%',
      marginTop: '15px'
   },
   contentWrapper: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column'
   },
   nested: {
      backgroundColor: 'var(--black-transparent-2)'
   }
}
