import React from 'react'

interface Styles {
   valueFieldWrapper: React.CSSProperties
   valueFieldLabel: React.CSSProperties
   valueFieldValue: React.CSSProperties
}

export const styles: Styles = {
   valueFieldWrapper: {
      flexGrow: 1,
      flexBasis: 0,
      margin: '5px',
      width: '80%',
      padding: '15px 10px 10px 10px',
      boxShadow: 'var(--event-shadow)',
      borderRadius: '10px',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center'
   },
   valueFieldLabel: {
      fontWeight: 500,
      color: 'var(--green-1)',
      fontSize: '1.5vw',
      paddingBottom: '2px',
      marginBottom: '10px',
      borderBottom: 'var(--border-thin-green-2)'
   },
   valueFieldValue: {
      padding: '10px 0px 5px 0px',
      fontWeight: 700,
      fontSize: '1.2vw',
      color: 'var(--gray-3)'
   }
}
