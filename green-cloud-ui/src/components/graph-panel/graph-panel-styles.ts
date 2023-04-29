import React from 'react'

interface Styles {
   graphContainer: React.CSSProperties
   headerContainer: React.CSSProperties
   headerStyle: React.CSSProperties
}

export const styles: Styles = {
   graphContainer: {
      flexGrow: 1,
      backgroundColor: 'var(--white)',
      height: '100%',
      minWidth: 'fit-content',
   },
   headerContainer: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      paddingBottom: '10px',
      borderBottom: 'var(--border-gray)',
   },
   headerStyle: {
      color: 'var(--gray-2)',
      fontWeight: 500,
   },
}
