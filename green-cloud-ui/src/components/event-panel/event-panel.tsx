import { styles } from './event-panel-styles'

import EventContainer from './event-container/event-container'
import { Agent, PowerShortageEventData } from '@types'
import { DetailsField, Modal } from 'components/common'

interface Props {
   selectedAgent?: Agent
   triggerPowerShortage: (data: PowerShortageEventData) => void
   isOpen: boolean
   setIsOpen: (state: boolean) => void
}

const modalHeader = 'TRIGGER AGENT EVENT'

/**
 * Component represents panel that can be used by the administrators to handle events condcuted on cloud network agents
 *
 * @param {boolean}[isOpen] - flag indicating if the modal is open
 * @param {func}[setIsOpen] - function changing the state of the modal
 * @returns JSX Element
 */
export const EventPanel = ({ selectedAgent, triggerPowerShortage, isOpen, setIsOpen }: Props) => {
   const { modalContainer, singleEventParentContainer, headerContainer } = styles

   const mapToEventFields = () => {
      return selectedAgent?.events.map((event) => {
         const key = [selectedAgent.name, event.type].join('_')
         return <EventContainer {...{ selectedAgent, event, key, triggerPowerShortage }} />
      })
   }

   const generateHeader = () => (
      <DetailsField
         {...{
            label: 'Selected Agent',
            value: selectedAgent?.name,
            isHeader: true,
            fieldContainerStyle: headerContainer,
         }}
      />
   )

   return (
      <>
         <Modal {...{ isOpen, setIsOpen, header: modalHeader, contentStyle: modalContainer }}>
            <div>
               {generateHeader()}
               <div style={singleEventParentContainer}>{mapToEventFields()}</div>
            </div>
         </Modal>
      </>
   )
}
