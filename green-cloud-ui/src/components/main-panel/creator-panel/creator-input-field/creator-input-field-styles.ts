import React from 'react'

interface Styles {
   wrapper: React.CSSProperties
   wrapperHeader: React.CSSProperties
   wrapperInput: React.CSSProperties
   descriptionStyle: React.CSSProperties
}

export const styles: Styles = {
   wrapper: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      width: '100%',
      marginBottom: '10px'
   },
   wrapperHeader: {
      marginRight: '10px',
      font: 'var(--font-1)',
      fontWeight: 500
   },
   wrapperInput: {
      width: '50%'
   },
   descriptionStyle: {
      fontSize: 'var(--font-size-6)',
      fontWeight: 400,
      color: 'var(--gray-1)',
      margin: '5px 0px 5px 5px'
   }
}
