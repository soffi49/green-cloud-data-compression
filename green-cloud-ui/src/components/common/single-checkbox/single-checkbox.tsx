import { styles } from './single-checkbox-styles'
import { Checkbox } from 'pretty-checkbox-react'
import '@djthoms/pretty-checkbox'
import { DropdownOption } from '@types'

interface Props {
   option: DropdownOption
   onChange: (status: string, isSelected: boolean) => void
}

/**
 * Component representing checkbox with single options
 *
 * @param {DropdownOption}[option] - option data
 * @param {func}[onChange] - function being executed when value of checkbox is changed
 * @returns JSX Element
 */
const SingleCheckBox = ({ option, onChange }: Props) => {
   const { checkBoxStyle } = styles
   const { value, isSelected } = option

   return (
      <Checkbox
         {...{
            className: isSelected ? 'checkbox-selected' : '',
            key: value as any,
            value: value as string,
            style: checkBoxStyle,
            checked: isSelected,
            color: 'success',
            shape: 'curve',
            animation: 'smooth',
            onChange: () => onChange(value as string, isSelected as boolean)
         }}
      >
         {value as string}
      </Checkbox>
   )
}

export default SingleCheckBox
