import { ActionMeta, MultiValue, SingleValue } from 'react-select'
import { DropdownOption } from './dropdown-option'

/**
 * Type used in onChange function in dropdown component that aims to change the selection
 */
export type DropdownOnChangeFunction =
   | ((value: SingleValue<DropdownOption> | MultiValue<DropdownOption>, actionMeta: ActionMeta<DropdownOption>) => void)
   | ((value: SingleValue<DropdownOption> | MultiValue<DropdownOption>) => void)
