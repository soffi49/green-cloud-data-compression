import React from 'react'

interface Styles {
   wrapper: React.CSSProperties
   headerWrapper: React.CSSProperties
   headerText: React.CSSProperties
   fieldsWrapper: React.CSSProperties
   valueFieldContainer: React.CSSProperties
}

export const styles: Styles = {
   wrapper: {
      borderRadius: '20px',
      boxShadow: 'var(--event-shadow)',
      marginTop: '10px',
      padding: '0px 5px 10px 5px',
      width: '90%',
      display: 'flex',
      flexDirection: 'column'
   },
   headerWrapper: {
      borderTopLeftRadius: '20px',
      borderTopRightRadius: '20px',
      width: '100%',
      color: 'var(--gray-3)',
      fontSize: '1vw',
      fontWeight: 500
   },
   headerText: {
      marginRight: '5px',
      padding: '25px 20px 5px 20px',
      marginBottom: '10px',
      overflowWrap: 'break-word'
   },
   fieldsWrapper: {
      padding: '5px 10px',
      width: '95%'
   },
   valueFieldContainer: {
      width: 'undefined',
      minWidth: 'undefined'
   }
}
