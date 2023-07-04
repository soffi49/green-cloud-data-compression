import React from 'react'

interface Styles {
   tableWrapper: React.CSSProperties
   table: React.CSSProperties
   tableHeader: React.CSSProperties
   tableColumn: React.CSSProperties
   buttonWrapper: React.CSSProperties
   header: React.CSSProperties
}

export const styles: Styles = {
   tableWrapper: {
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      margin: '20px 5px 10px 5px',
      boxShadow: 'var(--event-shadow)',
      borderRadius: 20,
      width: '100%'
   },
   table: {
      margin: '5px 10px 20px 10px',
      borderCollapse: 'collapse',
      tableLayout: 'fixed'
   },
   tableHeader: {
      color: 'var(--gray-2)',
      backgroundColor: 'var(--gray-13)',
      fontSize: '0.8vw',
      fontWeight: 600,
      padding: '10px 10px 10px 20px',
      textAlign: 'left',
      borderBottom: '1.5px solid var(--gray-4)',
      wordWrap: 'break-word',
      wordBreak: 'break-word'
   },
   tableColumn: {
      color: 'var(--gray-2)',
      fontSize: '0.8vw',
      fontWeight: 500,
      padding: '15px 20px',
      borderBottom: '1.5px solid var(--gray-4)',
      wordWrap: 'break-word',
      wordBreak: 'break-word'
   },
   buttonWrapper: {
      display: 'flex',
      justifyContent: 'flex-end',
      padding: '10px'
   },
   header: {
      color: 'var(--gray-6)',
      fontWeight: 600,
      fontSize: 'var(--font-size-4)',
      paddingLeft: '20px'
   }
}
