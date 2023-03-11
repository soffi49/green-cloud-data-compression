import { styles } from './multi-checkbox-styles'
import { SelectOption, SingleCheckBox } from 'components/common'

interface Props {
   options: SelectOption[]
   onChange: (status: string, isSelected: boolean) => void
   header?: string
}

/**
 * Component representing checkbox with multiple options
 *
 * @param {SelectOption[]}[options] - map containing relevant options
 * @param {func}[onChange] - function being executed when value of checkbox is changed
 * @param {string | undefined}[header] - optional checkbox header
 * @returns JSX Element
 */
const MultiCheckBox = ({ options, onChange, header }: Props) => {
   const { checkContainer, headerStyle } = styles

   const generateJobSplitCheckBoxFields = () => options.map((option) => <SingleCheckBox {...{ option, onChange }} />)

   return (
      <>
         <div style={headerStyle}>{header?.toUpperCase()}</div>
         <div style={checkContainer}>{generateJobSplitCheckBoxFields()}</div>
      </>
   )
}

export default MultiCheckBox
