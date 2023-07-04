import { styles } from './filter-modal-styles'
import { useState } from 'react'
import { ActionMeta, MultiValue, SingleValue } from 'react-select'
import { Dropdown, Modal, MultiCheckBox, SingleCheckBox } from 'components/common'
import { ALL_STATUS, INITIAL_JOB_SPLIT_SELECT_OPTIONS } from '../../client-panel-config'
import { DropdownOption } from '@types'

interface Props {
   jobStatusMap: DropdownOption[]
   setJobStatusMap: React.Dispatch<React.SetStateAction<DropdownOption[]>>
   setSplitFilter: (newValue: boolean | null) => void
   isOpen: boolean
   setIsOpen: (state: boolean) => void
}

/**
 * Component representing collapsible box containing job status filters
 *
 * @param {SelectOption[]}[jobStatusMap] - map containing relevant job statuses
 * @param {React.Dispatch<React.SetStateAction<SelectOption[]>>}[setJobStatusMap] - function used to update job statuses' properties
 * @param {React.Dispatch<React.SetStateAction<boolean | null>>}[setSplitFilter] - function used to update job filtering with regard to split
 * @param {boolean}[isOpen] - flag indicating if the modal is open
 * @param {func}[setIsOpen] - function changing the state of isOpen flag
 * @returns JSX Element
 */
const FilterModal = ({ jobStatusMap, setJobStatusMap, setSplitFilter, isOpen, setIsOpen }: Props) => {
   const [jobSplitFilterOptions, setJobSplitFilterOptions] = useState<DropdownOption[]>(
      INITIAL_JOB_SPLIT_SELECT_OPTIONS
   )
   const [isAllSelected, setIsAllSelected] = useState<DropdownOption>(ALL_STATUS)

   const { checkBoxContainer, modalContainer, modalHeader } = styles

   const setSelectedJobStatuses = jobStatusMap.filter((jobStatus) => jobStatus.isSelected)

   const handleJobSplitSelectChange = (status: string, isSelected: boolean) => {
      setJobSplitFilterOptions((prevState) =>
         prevState.map((jobStatus) => {
            if (isSelected) {
               const newStatus = jobStatus.value === 'ALL JOBS'
               return { ...jobStatus, isSelected: newStatus }
            }
            return jobStatus.value === status
               ? { ...jobStatus, isSelected: !jobStatus.isSelected }
               : { ...jobStatus, isSelected: false }
         })
      )

      if (!isSelected) {
         const isSplit = status === 'ALL JOBS' ? null : status === 'JOBS SPLIT TO PARTS'
         setSplitFilter(isSplit)
      } else {
         setSplitFilter(null)
      }
   }

   const onChange = (
      selectedValues: SingleValue<DropdownOption> | MultiValue<DropdownOption>,
      action: ActionMeta<DropdownOption>
   ) => {
      const values = selectedValues as MultiValue<DropdownOption>
      const removedValue = action.removedValue
      const areAllSelected = values.every((value) => value.isSelected) && !removedValue && action.action !== 'clear'

      setIsAllSelected((prevState) => ({ ...prevState, isSelected: areAllSelected }))
      setJobStatusMap((prevState) =>
         prevState.map((jobStatus) => {
            if (removedValue) {
               const isSelected = jobStatus === removedValue ? false : jobStatus.isSelected
               return { ...jobStatus, isSelected }
            }
            return { ...jobStatus, isSelected: values.includes(jobStatus) }
         })
      )
   }

   const onChangeAll = (state: string, isSelected: boolean) => {
      setIsAllSelected((prevState) => ({ ...prevState, isSelected: !prevState.isSelected }))

      if (!isSelected) {
         setJobStatusMap((prevState) => prevState.map((jobStatus) => ({ ...jobStatus, isSelected: true })))
      }
   }

   return (
      <Modal
         {...{ isOpen, setIsOpen, header: 'FILTER CLIENTS', contentStyle: modalContainer, headerStyle: modalHeader }}
      >
         <div>
            <Dropdown
               {...{
                  value: setSelectedJobStatuses,
                  options: jobStatusMap,
                  header: 'FILTER BY STATUS',
                  isOptionSelected: (option) => option.isSelected ?? true,
                  placeholder: 'Select job statuses',
                  onChange,
                  isMulti: true
               }}
            />
            <SingleCheckBox {...{ option: isAllSelected, onChange: onChangeAll }} />
         </div>
         <div style={checkBoxContainer}>
            <MultiCheckBox
               {...{
                  options: jobSplitFilterOptions,
                  onChange: handleJobSplitSelectChange,
                  header: 'FILTER BY JOB SPLIT STATE'
               }}
            />
         </div>
      </Modal>
   )
}

export default FilterModal
