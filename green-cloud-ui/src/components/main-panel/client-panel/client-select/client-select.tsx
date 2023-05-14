import { styles } from './client-select-styles'
import { SingleValue } from 'react-select'
import { ClientAgent, ClientAgentStatus } from '@types'
import { useEffect, useState } from 'react'
import { JOB_STATUS_MAP } from '../client-panel-config'
import FilterModal from './filter-modal/filter-modal'
import ClientDropdown from './client-dropdown/client-dropdown'
import { SelectOption } from 'components/common'
import { IconSettings } from '@assets'

interface Props {
   clients: ClientAgentStatus[]
   selectedClient: ClientAgent | null
   setSelectedClient: (client: string | null) => void
   updateClientData: () => void
}

/**
 * Component representing collapsible header containing client selector dropdown and associated to it filters
 *
 * @returns JSX Element
 */
const ClientStatisticsSelect = ({ clients, selectedClient, setSelectedClient, updateClientData }: Props) => {
   const { selectorContainer, iconContainer } = styles
   const [jobStatusMap, setJobStatusMap] = useState<SelectOption[]>(JOB_STATUS_MAP)
   const [splitFilter, setSplitFilter] = useState<boolean | null>(null)
   const [isOpen, setIsOpen] = useState<boolean>(false)

   useEffect(() => {
      if (clients.length === 0) {
         setSelectedClient(null)
      }
   }, [clients])

   const changeSelectedClient = (value: SingleValue<SelectOption>) => {
      setSelectedClient(value?.label ?? null)
      updateClientData()
   }

   const changeSplitFilter = (newValue: boolean | null) => {
      setSelectedClient(null)
      setSplitFilter(newValue)
   }

   return (
      <div>
         <FilterModal
            {...{
               jobStatusMap,
               setJobStatusMap,
               setSplitFilter: changeSplitFilter,
               isOpen,
               setIsOpen,
            }}
         />
         <div style={selectorContainer}>
            <div style={iconContainer} onClick={() => setIsOpen(!isOpen)}>
               <IconSettings size="55px" className={'filter-button'} />
            </div>
            <ClientDropdown
               {...{
                  selectedClient,
                  clients,
                  jobStatusMap,
                  changeSelectedClient,
                  splitFilter,
               }}
            />
         </div>
      </div>
   )
}

export default ClientStatisticsSelect
