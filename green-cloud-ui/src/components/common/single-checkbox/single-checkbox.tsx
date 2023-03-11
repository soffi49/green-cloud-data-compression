import { styles } from './single-checkbox-styles'
import { Checkbox } from 'pretty-checkbox-react'
import '@djthoms/pretty-checkbox'
import { SelectOption } from 'components/common'

interface Props {
   option: SelectOption
   onChange: (status: string, isSelected: boolean) => void
}

/**
 * Component representing checkbox with single options
 *
 * @param {SelectOption}[option] - option data
 * @param {func}[onChange] - function being executed when value of checkbox is changed
 * @returns JSX Element
 */
const SingleCheckBox = ({ option, onChange }: Props) => {
   const { checkBoxStyle } = styles
   const { value, isSelected } = option

   return (
      <Checkbox
         {...{
            key: value as any,
            value: value as string,
            style: checkBoxStyle,
            checked: isSelected,
            color: 'success',
            shape: 'curve',
            animation: 'smooth',
            onChange: () => onChange(value as string, isSelected as boolean),
         }}
      >
         {value as string}
      </Checkbox>
   )
}

export default SingleCheckBox
