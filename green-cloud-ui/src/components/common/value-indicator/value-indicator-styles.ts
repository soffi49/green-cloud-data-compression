import { IconProps } from '@types'
import React from 'react'

interface Styles {
   wrapper: React.CSSProperties
   titleStyle: React.CSSProperties
   container: React.CSSProperties
   valueStyle: React.CSSProperties
   iconStyle: IconProps
}

export const styles: Styles = {
   wrapper: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'space-around',
      width: '80%',
      padding: '20px',
      minWidth: 'fit-content',
      boxShadow: 'var(--event-shadow)',
      borderRadius: 20,
      margin: '5px 0px 10px 0px'
   },
   container: {
      display: 'flex',
      justifyContent: 'space-evenly',
      alignItems: 'center',
      width: '100%'
   },
   titleStyle: {
      fontSize: '1vw',
      fontWeight: 500,
      color: 'var(--gray-3)',
      marginBottom: '25px',
      textAlign: 'left',
      width: '100%'
   },
   valueStyle: {
      minHeight: '70px',
      display: 'flex',
      alignItems: 'center',
      fontSize: '1.3vw',
      borderLeft: 'var(--border-semi-bold-green)',
      paddingLeft: '10px',
      width: '50%'
   },
   iconStyle: {
      size: '70px',
      color: 'var(--green-1)'
   }
}
