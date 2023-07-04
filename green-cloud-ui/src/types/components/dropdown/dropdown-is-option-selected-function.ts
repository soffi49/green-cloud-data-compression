import { Options } from 'react-select'
import { DropdownOption } from './dropdown-option'

export type DropdownIsOptionSelected = (option: DropdownOption, selectValue: Options<DropdownOption>) => boolean
