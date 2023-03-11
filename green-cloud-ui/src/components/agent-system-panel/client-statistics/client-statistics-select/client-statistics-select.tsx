import { styles } from './client-statistics-select-styles'
import { SingleValue } from 'react-select'
import { ClientAgent } from '@types'
import { useEffect, useState } from 'react'
import { JOB_STATUS_MAP } from '../client-statistics-config'
import StatusFilterBox from '../../../agent-system-panel/client-statistics/client-statistics-select/status-filter-box/status-filter-box'
import ClientDropdown from '../../../agent-system-panel/client-statistics/client-statistics-select/client-dropdown/client-dropdown'
import Collapse from 'components/common/collapse/collapse'
import { SelectOption } from 'components/common'

interface Props {
   clients: ClientAgent[]
   selectedClient: ClientAgent | null
   setSelectedClient: (client: string | null) => void
}

const collapseOpen = 'Hide Client Selector'
const collapseClose = 'Display Client Selector'

/**
 * Component representing collapsible header containing client selector dropdown and associated to it filters
 *
 * @returns JSX Element
 */
const ClientStatisticsSelect = ({ clients, selectedClient, setSelectedClient }: Props) => {
   const { collapseStyle, collapseOpenStyle, collapseCloseStyle } = styles
   const [jobStatusMap, setJobStatusMap] = useState<SelectOption[]>(JOB_STATUS_MAP)
   const [splitFilter, setSplitFilter] = useState<boolean | null>(null)

   const styleOpen = { ...collapseStyle, ...collapseOpenStyle }
   const styleClosed = { ...collapseStyle, ...collapseCloseStyle }

   useEffect(() => {
      if (clients.length === 0) {
         setSelectedClient(null)
      }
   }, [clients])

   const changeSelectedClient = (value: SingleValue<SelectOption>) => {
      setSelectedClient(value?.label ?? null)
   }

   return (
      <Collapse
         {...{
            title: collapseOpen,
            titleClosed: collapseClose,
            triggerStyle: styleOpen,
            triggerClosedStyle: styleClosed,
         }}
      >
         <StatusFilterBox {...{ jobStatusMap, setJobStatusMap, setSplitFilter }} />
         <ClientDropdown {...{ selectedClient, clients, jobStatusMap, changeSelectedClient, splitFilter }} />
      </Collapse>
   )
}

export default ClientStatisticsSelect
