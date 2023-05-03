import { styles } from './client-panel-styles'
import { ClientAgent } from '@types'
import SubtitleContainer from 'components/common/subtitle-container/subtitle-container'
import { useEffect, useState } from 'react'
import { CLIENT_STATISTICS } from './client-panel-config'
import DetailsField from 'components/common/details-field/details-field'
import Badge from 'components/common/badge/badge'
import ClientStatisticsSelect from './client-select/client-select'
import { Button, ModalButton } from 'components/common'
import ClientSplitJobModal from './client-job-split-modal/client-job-split-modal'
import ClientJobDurationModal from './client-job-duration-modal/client-job-duration-modal'
import { convertTimeToString } from 'utils/time-utils'

const description = 'Select client from the list to diplay current job statistics'

interface Props {
   clients: ClientAgent[]
   selectedClient: ClientAgent
   setSelectedClient: (client: string | null) => void
}

/**
 * Component representing panel displaying details regarding network clients
 *
 * @returns JSX Element
 */
export const ClientPanel = ({ clients, selectedClient, setSelectedClient }: Props) => {
   const [isOpen, setIsOpen] = useState(false)
   const [isDurationOpen, setIsDurationOpen] = useState(false)
   const { clientContent, clientStatistics } = styles

   useEffect(() => {
      if (clients.length === 0) {
         setSelectedClient(null)
      }
   }, [clients])

   const generateClientInfo = () => {
      if (selectedClient) {
         return CLIENT_STATISTICS.map((field) => {
            const { key, label } = field
            const clientVal = {
               ...(selectedClient.job as any),
               status: selectedClient.status as any,
               durationMap: selectedClient.durationMap as any,
            }[key]
            const value = getClientValue(key, clientVal)
            const property = ['status', 'durationMap'].includes(key) ? 'valueObject' : 'value'

            return <DetailsField {...{ label, [property]: value, key }} />
         })
      }
   }

   const getClientValue = (key: string, clientVal: any) => {
      if (key === 'status') {
         return <Badge text={clientVal} />
      }
      if (key === 'durationMap') {
         return (
            <Button
               buttonClassName="small-gray-button"
               title="STATUS DURATION"
               onClick={() => {
                  setIsDurationOpen(true)
               }}
            />
         )
      }
      if (key === 'start' || key === 'end') {
         return typeof clientVal === 'number' ? convertTimeToString(clientVal) : clientVal
      }
      return clientVal
   }

   const getModalButton = (
      <ModalButton
         {...{
            buttonClassName: 'small-green-button',
            setIsOpen,
            title: 'SPLIT JOBS',
         }}
      />
   )

   const getSplitJobModal = () => {
      if (selectedClient?.isSplit) {
         return (
            <ClientSplitJobModal
               {...{
                  isOpen,
                  setIsOpen,
                  client: selectedClient,
               }}
            />
         )
      }
   }

   const getJobDurationModal = () => {
      if (selectedClient.durationMap) {
         return (
            <ClientJobDurationModal
               {...{
                  isOpen: isDurationOpen,
                  setIsOpen: setIsDurationOpen,
                  client: selectedClient,
               }}
            />
         )
      }
   }

   return (
      <div style={clientContent}>
         <ClientStatisticsSelect {...{ clients, selectedClient, setSelectedClient }} />
         {!selectedClient || clients.length === 0 ? (
            <SubtitleContainer text={description} />
         ) : (
            <>
               <div style={clientStatistics}>
                  <DetailsField
                     {...{ label: 'Client job parts', valueObject: selectedClient?.isSplit && getModalButton }}
                  />
                  {generateClientInfo()}
               </div>
               {getSplitJobModal()}
               {getJobDurationModal()}
            </>
         )}
      </div>
   )
}
