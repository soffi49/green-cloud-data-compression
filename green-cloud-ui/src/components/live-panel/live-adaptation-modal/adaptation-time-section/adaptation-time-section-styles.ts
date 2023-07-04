import React from 'react'

interface Styles {
   wrapper: React.CSSProperties
   headerWrapper: React.CSSProperties
   headerText: React.CSSProperties
   fieldsWrapper: React.CSSProperties
}

export const styles: Styles = {
   wrapper: {
      borderRadius: '20px',
      boxShadow: 'var(--event-shadow)',
      minWidth: 'fit-content',
      margin: '10px 0px 5px 0px',
      padding: '0px 10px 10px 10px',
      width: '88%',
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
      marginBottom: '10px'
   },
   fieldsWrapper: {
      padding: '10px 0px',
      display: 'flex',
      width: '100%',
      flexDirection: 'row',
      alignItems: 'center'
   }
}
