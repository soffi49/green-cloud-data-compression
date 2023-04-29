import React from 'react'

interface Styles {
   menuIcon: React.CSSProperties
   menuIndicator: React.CSSProperties
   menuIndicatorText: React.CSSProperties
}

export const styles: Styles = {
   menuIndicator: {
      borderRadius: 50,
      paddingTop: '20px',
      paddingBottom: '20px',
      backgroundColor: 'var(--gray-3)',
      opacity: 0.9,
      display: 'flex',
      alignItems: 'center',
   },
   menuIndicatorText: {
      textAlign: 'right',
      verticalAlign: 'middle',
      paddingLeft: '65px',
      paddingRight: '10px',
      color: 'var(--white)',
      fontWeight: 300,
      fontSize: 'var(--font-size-8)',
      fontFamily: 'var(--font-1)',
   },
   menuIcon: {
      marginBottom: '20px',
      opacity: 0.9,
      cursor: 'pointer',
   },
}
