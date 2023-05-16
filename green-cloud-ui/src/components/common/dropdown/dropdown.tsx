import { styles } from './dropdown-styles'
import Select, { ActionMeta, MultiValue, SingleValue, StylesConfig } from 'react-select'
import { useState } from 'react'
import { FilterOptionOption } from 'react-select/dist/declarations/src/filters'
import { DropdownIsOptionSelected, DropdownOnChangeFunction, GroupedOption, DropdownOption } from '@types'

interface Props {
   value: DropdownOption | DropdownOption[]
   options: DropdownOption[] | GroupedOption[]
   onChange: DropdownOnChangeFunction
   isOptionSelected?: DropdownIsOptionSelected
   noOptionsMessage?: () => string
   header?: string
   placeholder?: string
   isMulti?: boolean
   isClearable?: boolean
   selectStyle?: StylesConfig<DropdownOption>
}

const MAX_DROPDOWN_HEIGHT = 150
const MIN_DROPDOWN_HEIGHT = 0

/**
 * Component representing dropdown selector
 *
 * @param {DropdownOption | DropdownOption[]}[value] - value of the selected element
 * @param {DropdownOption[] | DropdownOption}[options] - map of relevant options
 * @param {func}[onChange] - function used to update currently selected value/values
 * @param {func | undefined}[isOptionSelected] - optional function overriding behaviour executed upon option selection change
 * @param {func | undefined}[noOptionsMessage] - optional message displayed when no option is selected
 * @param {string | undefined}[header] - optional dropdown header
 * @param {string | undefined}[placeholder] - optional dropdown placeholder
 * @param {boolean | undefined}[isMulti] - optional value indicating if choice of multiple options is possible
 * @param {boolean | undefined}[isClearable] - optional value indicating if input is clearable
 * @param {StylesConfig<DropdownOption>}[selectStyle] - optional styling of the select component
 * @returns JSX Element
 */
const Dropdown = ({
   value,
   options,
   onChange,
   isOptionSelected,
   noOptionsMessage,
   header,
   placeholder,
   isMulti,
   isClearable = true,
   selectStyle,
}: Props) => {
   const [isFocus, setIsFocus] = useState(false)
   const { select, selectTheme, headerStyle } = styles

   const styleSelect = selectStyle ? { ...select, ...selectStyle } : select

   const customFilter = (option: FilterOptionOption<DropdownOption>, inputValue: string) =>
      option.label.includes(inputValue.toUpperCase())

   const getValueObject = () => ({
      value: (value as DropdownOption).value,
      label: !isFocus ? (value as DropdownOption).label : '',
   })

   const handleOnChange = (
      newValue: SingleValue<DropdownOption> | MultiValue<DropdownOption>,
      actionMeta: ActionMeta<DropdownOption>
   ) => {
      setIsFocus(false)
      onChange(newValue, actionMeta)
   }

   return (
      <>
         {header && <div style={headerStyle}>{header.toUpperCase()}</div>}
         <Select
            {...{
               value: Array.isArray(value) ? value : getValueObject(),
               options,
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
            }}
         />
      </>
   )
}

export default Dropdown
