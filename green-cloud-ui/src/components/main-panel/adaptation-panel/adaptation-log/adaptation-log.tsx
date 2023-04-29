import React, { useState } from 'react'

import { styles } from './adaptation-log-styles'

import { ModalButton, SubtitleContainer } from 'components/common'
import { AdaptationLog } from '@types'

import FullLogModal from './full-log-modal/full-log-modal'
import LogEntry from './log-entry/log-entry'

interface Props {
   sortedLogs: AdaptationLog[]
}

/**
 * Component represents tab summarizing the latest logs regarding the system adaptation
 *
 * @returns JSX Element
 */
export const AdaptationLogPanel = ({ sortedLogs }: Props) => {
   const [isOpen, setIsOpen] = useState(false)

   const moreButtonTitle = (
      <>
         <span>SHOW MORE</span>
         <span style={styles.buttonIcon}>{'\u00BB'}</span>
      </>
   )

   return (
      <>
         {sortedLogs.length !== 0 ? (
            <div>
               <LogEntry adaptationLog={sortedLogs[0]} />
               <ModalButton {...{ buttonClassName: 'more-button', setIsOpen, title: moreButtonTitle }} />
               <FullLogModal {...{ isOpen, setIsOpen, sortedLogs }} />
            </div>
         ) : (
            <SubtitleContainer {...{ text: 'No adaptation logs to display' }} />
         )}
      </>
   )
}
