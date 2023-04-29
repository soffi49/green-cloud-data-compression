import { styles } from './dropdown-styles'
import Select, { ActionMeta, MultiValue, Options, SingleValue, StylesConfig } from 'react-select'
import { useState } from 'react'
import { FilterOptionOption } from 'react-select/dist/declarations/src/filters'
import { GroupedOption, SelectOption } from './dropdown-config'
import { MAX_DROPDOWN_HEIGHT, MIN_DROPDOWN_HEIGHT } from './dropdown-config'

interface Props {
   value: SelectOption | SelectOption[]
   onChange:
      | ((value: SingleValue<SelectOption> | MultiValue<SelectOption>, actionMeta: ActionMeta<SelectOption>) => void)
      | ((value: SingleValue<SelectOption> | MultiValue<SelectOption>) => void)
   options: SelectOption[] | GroupedOption[]
   header?: string
   isOptionSelected?: (option: SelectOption, selectValue: Options<SelectOption>) => boolean
   placeholder?: string
   noOptionsMessage?: () => string
   isMulti?: boolean
   isClearable?: boolean
   selectStyle?: StylesConfig<SelectOption>
}

/**
 * Component representing dropdown selector
 *
 * @param {SelectOption | SelectOption[]}[value] - value of the selected element
 * @param {func}[onChange] - function used to update currently selected value/values
 * @param {SelectOption[] | SelectOption}[options] - map of relevant options
 * @param {string | undefined}[header] - optional header
 * @param {func | undefined}[isOptionSelected] - optional function overriding behaviour executed upon option selection change
 * @param {string | undefined}[placeholder] - optional placeholder
 * @param {func | undefined}[noOptionsMessage] - optional message displayed when no option is selected
 * @param {boolean | undefined}[isMulti] - optional value indicating if choice of multiple options is possible
 * @param {boolean | undefined}[isClearable] - optional value indicating if input is clearable
 * @param {StylesConfig<SelectOption>}[selectStyle] - optional styling of the select component
 * @returns JSX Element
 */
const Dropdown = ({
   value,
   onChange,
   options,
   header,
   isOptionSelected,
   placeholder,
   noOptionsMessage,
   isMulti,
   isClearable = true,
   selectStyle,
}: Props) => {
   const [isFocus, setIsFocus] = useState(false)
   const { select, selectTheme, headerStyle } = styles

   const styleSelect = selectStyle ? { ...select, ...selectStyle } : select
   const customFilter = (option: FilterOptionOption<SelectOption>, inputValue: string) =>
      option.label.includes(inputValue.toUpperCase())

   const handleOnChange = (
      newValue: SingleValue<SelectOption> | MultiValue<SelectOption>,
      actionMeta: ActionMeta<SelectOption>
   ) => {
      setIsFocus(false)
      onChange(newValue, actionMeta)
   }

   return (
      <>
         {header && <div style={headerStyle}>{header.toUpperCase()}</div>}
         <Select
            {...{
               value: Array.isArray(value)
                  ? value
                  : {
                       value: value.value,
                       label: !isFocus ? value.label : '',
                    },
               onFocus: () => setIsFocus(true),
               onBlur: () => setIsFocus(false),
               onChange: handleOnChange,
               placeholder,
               noOptionsMessage,
               styles: styleSelect,
               theme: selectTheme,
               maxMenuHeight: MAX_DROPDOWN_HEIGHT,
               minMenuHeight: MIN_DROPDOWN_HEIGHT,
               isSearchable: true,
               isClearable,
               isOptionSelected,
               isMulti,
               menuPortalTarget: document.getElementById('root'),
               menuPosition: 'fixed',
               filterOption: customFilter,
               options,
            }}
         />
      </>
   )
}

export default Dropdown
