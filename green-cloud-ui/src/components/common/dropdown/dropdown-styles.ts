import { DropdownOption } from '@types'
import { StylesConfig, ThemeConfig, Theme } from 'react-select'

interface Styles {
   select: StylesConfig<DropdownOption>
   selectTheme: ThemeConfig
   headerStyle: React.CSSProperties
}

export const styles: Styles = {
   headerStyle: {
      fontSize: 'var(--font-size-4)',
      fontFamily: 'var(--font-1)',
      color: 'var(--gray-3)',
      fontWeight: 600,
      paddingLeft: '5px',
      paddingBottom: '10px'
   },
   select: {
      container: (styles: any) => ({
         ...styles,
         paddingBottom: '10px',
         width: '100%'
      }),
      control: (styles: any) => ({
         ...styles,
         width: '99.5%',
         margin: 'auto'
      }),
      placeholder: (styles: any) => ({
         ...styles,
         fontSize: 'var(--font-size-2)',
         fontFamily: 'var(--font-1)',
         textTransform: 'uppercase'
      }),
      multiValueLabel: (styles: any) => ({
         ...styles,
         fontFamily: 'var(--font-1)',
         fontSize: 'var(--font-size-7)'
      }),
      groupHeading: (styles: any) => ({
         ...styles,
         color: 'var(--green-1)',
         opacity: 0.8,
         fontFamily: 'var(--font-1)',
         fontSize: 'var(--font-size-7)',
         fontWeight: 400,
         marginLeft: '5px',
         width: '50%',
         borderBottom: 'var(--border-thin-dark-gray)'
      }),
      noOptionsMessage: (styles: any) => ({
         ...styles,
         color: 'var(--gray2)'
      }),
      menu: (styles: any) => ({
         ...styles,
         marginTop: '2px',
         paddingBottom: '5px',
         border: '1px solid var(--gray-5)'
      }),
      menuPortal: (styles: any) => ({
         ...styles,
         zIndex: 999999
      }),
      multiValueRemove: (styles: any) => ({
         ...styles,
         ':hover': {
            backgroundColor: 'var(--gray-8)'
         }
      })
   },
   selectTheme: (theme: Theme) => {
      return {
         ...theme,
         borderRadius: 7,
         colors: {
            ...theme.colors,
            text: 'orangered',
            primary50: 'var(--gray-5)',
            primary25: 'var(--white)',
            primary: 'var(--green-2)'
         }
      }
   }
}
