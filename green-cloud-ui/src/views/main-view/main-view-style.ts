import React from 'react'

interface Styles {
   mainContainer: React.CSSProperties
   menuContainer: React.CSSProperties
   contentContainer: React.CSSProperties
   sectionContainer: React.CSSProperties
   leftSectionContainer: React.CSSProperties
   rightSectionContainer: React.CSSProperties
   mainPanelContainer: React.CSSProperties
   livePanelContainer: React.CSSProperties
   graphPanelContainer: React.CSSProperties
}

export const styles: Styles = {
   mainContainer: {
      backgroundColor: 'var(--beige-1)',
      height: '100%',
      width: '100%',
      display: 'flex',
      flexDirection: 'row',
      overflow: 'hidden',
      marginBottom: '5px'
   },
   menuContainer: {
      width: '50px',
      height: '100%',
      minWidth: 'fit-content',
      minHeight: 'fit-content'
   },
   contentContainer: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      height: '100%',
      flexGrow: 1
   },
   sectionContainer: {
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center',
      height: '95%'
   },
   leftSectionContainer: {
      width: '45%'
   },
   rightSectionContainer: {
      width: '55%'
   },
   mainPanelContainer: {
      height: '40%',
      marginBottom: '20px'
   },
   livePanelContainer: {
      height: '56%'
   },
   graphPanelContainer: {
      height: '100%',
      marginLeft: '20px',
      marginRight: '20px'
   }
}
