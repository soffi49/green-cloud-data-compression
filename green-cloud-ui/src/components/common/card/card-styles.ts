import React from 'react'

interface Styles {
   cardContainer: React.CSSProperties
   cardHeader: React.CSSProperties
   cardHeaderWithSubheader: React.CSSProperties
   cardSubHeader: React.CSSProperties
   cardSubHeaderText: React.CSSProperties
   cardHeaderContainer: React.CSSProperties
   cardContent: React.CSSProperties
   cardContentScroll: React.CSSProperties
}

export const styles: Styles = {
   cardContainer: {
      display: 'flex',
      flexDirection: 'column',
      padding: '15px',
      boxShadow: 'var(--card-shadow)',
      boxSizing: 'border-box',
      borderRadius: 20
   },
   cardHeader: {
      color: 'var(--gray-2)',
      fontWeight: '500'
   },
   cardHeaderWithSubheader: {
      width: '60%'
   },
   cardSubHeader: {
      width: '30%',
      paddingRight: '10px',
      minWidth: 'fit-content',
      margin: '0 15px',
      borderLeft: 'none',
      borderRight: 'var(--border-bold-green)'
   },
   cardSubHeaderText: {
      textAlign: 'right',
      width: '100%',
      fontWeight: 500,
      fontSize: 'var(--font-size-3)'
   },
   cardHeaderContainer: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center'
   },
   cardContent: {
      flexGrow: 1,
      height: '100%',
      marginTop: '20px',
      color: 'var(--gray-2)',
      fontWeight: '300',
      minHeight: 0
   },
   cardContentScroll: {
      overflowY: 'scroll',
      overflowX: 'hidden'
   }
}
