import { ClientAgent, SplitJob } from '@types'
import { DetailsCard } from 'components/common'
import Modal from 'components/common/modal/modal'
import React from 'react'
import { parseSplitJobId } from 'utils/string-utils'
import { convertTimeToString } from 'utils/time-utils'
import { SPLIT_JOB_STATISTICS } from '../client-panel-config'
import { styles } from './client-job-split-modal-styles'

interface Props {
   isOpen: boolean
   setIsOpen: (state: boolean) => void
   client: ClientAgent
}

/**
 * Component represents a pop-up modal displaying all client job parts
 *
 * @param {boolean}[isOpen] - flag indicating if the modal is currently open
 * @param {func}[setIsOpen] - function changing the state of the modal
 * @param {ClientAgent}[client] - client for which the job was split
 * @returns JSX Element
 */
const ClientSplitJobModal = ({ isOpen, setIsOpen, client }: Props) => {
   const { modalStyle } = styles

   const header = [client.name.toUpperCase(), 'JOB PARTS'].join(' ')

   const getSplitJobs = () => {
      return client.splitJobs.map((job, idx) => {
         const jobTitle = ['JOB', parseSplitJobId(job.splitJobId)].join(' ')
         const parsedJob = Object.assign({}, job, {
            start: convertTimeToString((job as SplitJob).start),
            end: convertTimeToString((job as SplitJob).end)
         })
         return (
            <DetailsCard
               key={jobTitle + idx}
               {...{
                  title: jobTitle,
                  fieldMap: SPLIT_JOB_STATISTICS,
                  objectMap: parsedJob
               }}
            />
         )
      })
   }

   return (
      <Modal
         {...{
            isOpen,
            setIsOpen,
            contentStyle: modalStyle,
            header: header
         }}
      >
         {getSplitJobs()}
      </Modal>
   )
}

export default ClientSplitJobModal
