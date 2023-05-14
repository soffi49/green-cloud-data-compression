import { MultiValue, SingleValue } from 'react-select'
import { ClientAgent, ClientAgentStatus } from '@types'
import { useMemo } from 'react'
import { CLIENTS_ORDER } from '../../client-panel-config'
import { Dropdown, GroupedOption, SelectOption } from 'components/common'

const selectPlaceholder = 'Provide client name'
const selectNoOption = 'Client not found'
const selectNoClients = 'Client list is empty'

interface Props {
   selectedClient: ClientAgent | null
   changeSelectedClient: (client: SingleValue<SelectOption>) => void
   clients: ClientAgentStatus[]
   jobStatusMap: SelectOption[]
   splitFilter: boolean | null
}

/**
 * Component representing dropdown client selector
 *
 * @param {ClientAgent | null}[selectedAgent] - currently selected client
 * @param {func}[changeSelectedClient] - function used to update currently selected client
 * @param {ClientAgent[]}[clients] - all clients
 * @param {SelectOption[]}[jobStatusMap] - map of relevant job statuses
 * @param {boolean | null}[splitFilter] - boolean used in filtering dropwdown based on job split
 * @returns JSX Element
 */
const ClientDropdown = ({ selectedClient, changeSelectedClient, clients, jobStatusMap, splitFilter }: Props) => {
   const filteredClientsForJobs = () =>
      clients.filter(
         (client) =>
            jobStatusMap.find((job) => job.value === client.status.toString())?.isSelected &&
            (splitFilter === null || client.isSplit === splitFilter)
      )

   const aggregateOptions = (prev: GroupedOption[], curr: ClientAgentStatus) => {
      const currJob = curr.status.toString()
      const clientName = curr.name.toUpperCase()
      const prevGroup = prev.find((opt) => opt.label === currJob)
      const clientToPush = { label: clientName, value: curr }

      if (prevGroup) {
         prevGroup.options.push(clientToPush)
      } else {
         prev.push({ label: currJob, options: [clientToPush] })
      }
      return prev
   }

   const sortClients = (clientA: GroupedOption, clientB: GroupedOption) =>
      CLIENTS_ORDER.indexOf(clientA.label) - CLIENTS_ORDER.indexOf(clientB.label)

   const selectData = useMemo(
      () =>
         filteredClientsForJobs()
            .reduce((prev, curr) => aggregateOptions(prev, curr), [] as GroupedOption[])
            .sort((a, b) => sortClients(a, b)),
      [clients, jobStatusMap, splitFilter]
   )

   const handleOnChange = (value: SingleValue<SelectOption> | MultiValue<SelectOption>) =>
      changeSelectedClient(value as SingleValue<SelectOption>)

   const handleNoOption = () => (clients.length !== 0 ? selectNoOption : selectNoClients)

   return (
      <Dropdown
         {...{
            value: {
               value: selectedClient,
               label: selectedClient?.name ?? selectPlaceholder,
            },
            onChange: handleOnChange,
            placeholder: selectPlaceholder,
            noOptionsMessage: handleNoOption,
            options: selectData,
            isMulti: false,
         }}
      />
   )
}

export default ClientDropdown
