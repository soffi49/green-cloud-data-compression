import { DropdownOption } from '@types'
import React from 'react'
import { StylesConfig } from 'react-select'

interface Styles {
   chartTitle: React.CSSProperties
   chartWrapper: React.CSSProperties
   headerWrapper: React.CSSProperties
   selectStyle: StylesConfig<DropdownOption>
}

export const styles: Styles = {
   chartWrapper: {
      height: '100%',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'flex-start',
      boxShadow: 'var(--event-shadow)',
      borderRadius: 20,
      padding: '0 10px 15px 10px'
   },
   chartTitle: {
      color: 'var(--gray-6)',
      fontWeight: 600,
      fontSize: 'var(--font-size-4)',
      marginRight: '10px'
   },
   headerWrapper: {
      height: '5%',
      margin: '20px 20px 25px 20px',
      display: 'flex',
      width: '100%',
      alignItems: 'center',
      justifyContent: 'space-between'
   },
   selectStyle: {
      container: (styles: any) => ({
         ...styles,
         width: 'fit-content',
         paddingBottom: '0',
         paddingRight: '30px'
      }),
      input: (styles: any) => ({
         ...styles,
         width: '60px'
      }),
      control: (styles: any) => ({
         ...styles,
         fontSize: 'var(--font-size-2)',
         height: '20px',
         minHeight: '30px',
         textTransform: 'uppercase',
         boxShadow: 'none',
         border: 0,
         backgroundColor: 'var(--gray-4)'
      }),
      valueContainer: (styles: any) => ({
         ...styles,
         minHeight: '30px',
         height: '30px'
      }),
      indicatorsContainer: (styles: any) => ({
         ...styles,
         minHeight: '30px',
         height: '30px'
      }),
      dropdownIndicator: (styles: any) => ({
         ...styles,
         color: 'var(--gray-3)'
      }),
      indicatorSeparator: (styles: any) => ({
         ...styles,
         color: 'var(--gray-3)'
      }),
      singleValue: (styles: any) => ({
         ...styles,
         color: 'var(--gray-3)',
         fontWeight: 600
      })
   }
}
