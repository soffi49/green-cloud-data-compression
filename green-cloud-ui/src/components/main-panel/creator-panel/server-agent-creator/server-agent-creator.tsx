import { useState, useEffect } from 'react'
import { Agent, AgentType, DropdownOption, ResourceMap, ServerCreator } from '@types'
import { Button, Modal, ResourceForm, SubtitleContainer, UploadJSONButton } from 'components/common'
import { UpdateServerForm } from '../creator-panel'
import { CreatorInputField } from '../creator-input-field/creator-input-field'
import { CreatorDropdownField } from '../creator-dropdown-field/creator-dropdown-field'
import { styles } from './server-agent-creator-styles'
import { UpdateResourceReset } from 'components/common/resource-form/resource-form'
import { CreatorButtonField } from '../creator-button-field/creator-button-field'

interface Props {
   serverAgentData: ServerCreator
   setServerAgentData: UpdateServerForm
   agents: Agent[]
   resetData: boolean
   setResetData: UpdateResourceReset
}

const getAvailableRMAOptions = (agents: Agent[]): DropdownOption[] =>
   agents
      .filter((agent) => agent.type === AgentType.REGIONAL_MANAGER)
      .map((agent) => agent.name)
      .map((agentName) => {
         return { value: agentName, label: agentName as string, isSelected: false }
      })

const EMPTY_OPTION = { value: '', label: '', isSelected: false }

/**
 * Component represents a view allowing to create new server agent
 *
 * @param {JobCreator}[serverAgentData] - data modified using creator
 * @param {UpdateGreenSourceForm}[setServerAgentData] - method used to modify server data
 * @param {boolean}[resetData] - flag indicating if resources should be reset
 * @param {UpdateResourceReset}[setResetData] - method used to modify information if data should be reset
 *
 * @returns JSX Element
 */
export const ServerAgentCreator = ({ serverAgentData, setServerAgentData, agents, resetData, setResetData }: Props) => {
   const [isOpen, setIsOpen] = useState<boolean>(false)
   const [selectedRMA, setSelectedRMA] = useState<DropdownOption>(EMPTY_OPTION)
   const [resources, setResources] = useState<ResourceMap>(serverAgentData.resources)
   const { container, rmaWrapper, modalContainer, modalContent, modalWrapper } = styles

   const closeButtonStyle = ['large-green-button', 'large-green-button-active', 'full-width-button'].join(' ')

   useEffect(() => {
      if (resetData) {
         setSelectedRMA(EMPTY_OPTION)
         setResources({})
         setResetData(false)
      }
   }, [resetData])

   useEffect(() => {
      if (serverAgentData.resources) {
         const rma =
            serverAgentData?.regionalManager &&
            agents
               .filter((agent) => agent.type === AgentType.REGIONAL_MANAGER)
               .map((agent) => agent.name)
               .includes(serverAgentData?.regionalManager)
               ? serverAgentData.regionalManager
               : ''

         updateServerAgentValue(rma, 'regionalManager')
         setSelectedRMA({ label: rma, value: rma, isSelected: false })
         setResources(serverAgentData.resources)
      }
   }, [serverAgentData])

   const updateServerAgentValue = (newValue: string | number | ResourceMap, valueKey: keyof ServerCreator) => {
      setServerAgentData((prevData) => {
         return {
            ...prevData,
            [valueKey]: newValue
         }
      })
   }

   const getResourceModal = () => (
      <Modal
         {...{
            isOpen,
            setIsOpen,
            header: 'Specify resources for server'.toUpperCase(),
            contentStyle: modalContent,
            containerStyle: modalContainer
         }}
      >
         <div style={modalWrapper}>
            <div>
               <ResourceForm
                  {...{
                     newResources: resources,
                     setNewResources: setResources,
                     resetResource: resetData,
                     setResetResource: setResetData
                  }}
               />
            </div>
            <Button
               {...{
                  title: 'Apply'.toUpperCase(),
                  onClick: () => {
                     updateServerAgentValue(resources, 'resources')
                     setIsOpen(false)
                  },
                  buttonClassName: closeButtonStyle
               }}
            />
         </div>
      </Modal>
   )

   return (
      <div>
         {getResourceModal()}
         {agents.filter((agent) => agent.type === AgentType.REGIONAL_MANAGER).length === 0 ? (
            <SubtitleContainer
               {...{
                  text: 'Server cannot be created because there are no Regional Managers in the systems to which it can be attached'
               }}
            />
         ) : (
            <div>
               <UploadJSONButton
                  {...{
                     buttonText: `Upload Server configuration`,
                     handleUploadedContent: setServerAgentData
                  }}
               />
               <div style={container}>
                  <CreatorDropdownField
                     {...{
                        title: 'Regional manager to connect with',
                        description: 'Select Regional Manager with which Server is to be connected',
                        options: getAvailableRMAOptions(agents),
                        selectedData: selectedRMA,
                        setSelectedData: setSelectedRMA,
                        modifyData: (data: any) => updateServerAgentValue(data, 'regionalManager'),
                        wrapperStyle: rmaWrapper
                     }}
                  />
                  <CreatorInputField
                     {...{
                        title: 'Name',
                        description: 'Provide Server name',
                        fieldName: 'name',
                        dataToModify: serverAgentData,
                        dataModificationFunction: updateServerAgentValue
                     }}
                  />
                  <CreatorButtonField
                     {...{
                        title: 'Resources',
                        buttonName: 'Specify resources',
                        onClick: () => setIsOpen(!isOpen)
                     }}
                  />
                  <CreatorInputField
                     {...{
                        title: 'Maximal power',
                        description: "Provide maximal server's power consumption",
                        fieldName: 'maxPower',
                        dataToModify: serverAgentData,
                        dataModificationFunction: updateServerAgentValue,
                        isNumeric: true
                     }}
                  />
                  <CreatorInputField
                     {...{
                        title: 'Idle power',
                        description: 'Provide power consumption when server is in idle mode',
                        fieldName: 'idlePower',
                        dataToModify: serverAgentData,
                        dataModificationFunction: updateServerAgentValue,
                        isNumeric: true
                     }}
                  />
                  <CreatorInputField
                     {...{
                        title: 'Job processing limit',
                        description: 'Provide maximal number of jobs which execution server can process at once',
                        fieldName: 'jobProcessingLimit',
                        dataToModify: serverAgentData,
                        dataModificationFunction: updateServerAgentValue,
                        isNumeric: true
                     }}
                  />
                  <CreatorInputField
                     {...{
                        title: 'Price',
                        description: 'Provide price per power unit',
                        fieldName: 'price',
                        dataToModify: serverAgentData,
                        dataModificationFunction: updateServerAgentValue,
                        isNumeric: true
                     }}
                  />
               </div>
            </div>
         )}
      </div>
   )
}
