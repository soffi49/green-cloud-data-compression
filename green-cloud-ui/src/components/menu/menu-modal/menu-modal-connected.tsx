import {
   agentsActions,
   AppDispatch,
   cloudNetworkActions,
   RootState,
   selectConnectionState,
   selectSelectedTab
} from '@store'
import { connect } from 'react-redux'
import { reportsActions } from 'store/reports/actions'
import { MenuModal } from './menu-modal'
import { MenuTab } from '@types'

const mapStateToProps = (state: RootState) => {
   return { isServerConnected: selectConnectionState(state), menuTab: selectSelectedTab(state) }
}

const mapDispatchToProps = (dispatch: AppDispatch) => {
   return {
      resetCloudNetwork: () => dispatch(cloudNetworkActions.resetCloudNetwork()),
      resetAgents: () => dispatch(agentsActions.resetAgents()),
      resetReports: () => dispatch(reportsActions.resetReports()),
      closeServerConnection: (tab: MenuTab) => dispatch(cloudNetworkActions.closeServerConnection(tab)),
      resetServerConnection: () => dispatch(cloudNetworkActions.resetServerConnection())
   }
}

export default connect(mapStateToProps, mapDispatchToProps)(MenuModal)
