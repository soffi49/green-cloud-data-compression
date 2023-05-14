import { AppDispatch, cloudNetworkActions } from '@store'
import { MainView } from './main-view'
import { connect } from 'react-redux'
import { MenuTab } from '@types'
import { navigatorActions } from 'store/navigator'

const mapDispatchToProps = (dispatch: AppDispatch) => {
   return {
      resetServerConnection: () => dispatch(cloudNetworkActions.resetServerConnection()),
      setSelectedTab: (tab: MenuTab) => dispatch(navigatorActions.setSelectedTab(tab)),
   }
}

export default connect(null, mapDispatchToProps)(MainView)
