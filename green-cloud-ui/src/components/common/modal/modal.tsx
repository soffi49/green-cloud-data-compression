import React from 'react'
import ReactModal from 'react-modal'
import { styles } from './modal-styles'

interface Props {
   isOpen: boolean
   setIsOpen: (state: boolean) => void
   children?: React.ReactNode | React.ReactNode[]
   header: React.ReactNode | string
   contentStyle?: React.CSSProperties
   headerStyle?: React.CSSProperties
   isNested?: boolean
   disableScroll?: boolean
}

/**
 * Component represents a pop-up modal
 *
 * @param {boolean}[isOpen] - flag indicating if the modal is currently open
 * @param {func}[setIsMenuOpen] - function changing the state of the modal
 * @param {React.ReactNode | React.ReactNode[] | undefined}[children] - content of the modal
 * @param {React.ReactNode | string}[header] - title of the modal
 * @param {React.CSSProperties}[contentStyle] - optional modal style
 * @param {React.CSSProperties}[headerStyle] - optional header style
 * @param {boolean}[isNested] - optional flag indicating if the modal is nested
 * @param {boolean}[disableScroll] - optional flag indicating if the modal content should have scroll
 * @returns JSX Element
 */
const Modal = ({ isOpen, setIsOpen, header, children, contentStyle, headerStyle, isNested, disableScroll }: Props) => {
   const { modalStyle, modalTitle, mainContainer, contentWrapper, nested } = styles
   const { content, overlay, ...otherStyles } = modalStyle
   const styleModal = {
      content: { ...contentStyle, ...content },
      overlay: { ...overlay, ...(isNested ? nested : undefined) },
      ...otherStyles,
   }
   const styleHeader = { ...modalTitle, ...headerStyle }
   const scroll: React.CSSProperties = { overflowY: disableScroll ? 'hidden' : 'auto' }
   const styleMainContainer = { ...mainContainer, ...scroll }

   return (
      <ReactModal
         {...{
            style: styleModal,
            isOpen,
            appElement: document.getElementById('root') as HTMLElement,
            onRequestClose: () => setIsOpen(false),
            shouldCloseOnOverlayClick: true,
         }}
      >
         <div style={contentWrapper}>
            <div style={styleHeader}>{header}</div>
            <div style={styleMainContainer}>{children}</div>
         </div>
      </ReactModal>
   )
}

export default Modal
