import React from 'react'

interface Styles {
   checkBoxContainer: React.CSSProperties
   collapse: React.CSSProperties
   collapseSubContainerContent: React.CSSProperties
}

export const styles: Styles = {
   checkBoxContainer: {
      marginTop: '30px',
      marginBottom: '10px',
   },
   collapse: {
      fontSize: 'var(--font-size-4)',
      backgroundColor: 'var(--green-3)',
      padding: '3px 7px',
      color: 'var(--white)',
      marginBottom: '10px',
   },
   collapseSubContainerContent: {
      width: '100%',
      marginBottom: '30px',
   },
}
