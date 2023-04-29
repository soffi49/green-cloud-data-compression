import { agentsActions, AppDispatch, cloudNetworkActions, RootState, selectConnectionState } from '@store'
import { connect } from 'react-redux'
import { reportsActions } from 'store/reports/actions'
import { MenuModal } from './menu-modal'

const mapStateToProps = (state: RootState) => {
   return { isServerConnected: selectConnectionState(state) }
}

const mapDispatchToProps = (dispatch: AppDispatch) => {
   return {
      resetCloudNetwork: () => dispatch(cloudNetworkActions.resetCloudNetwork()),
      resetAgents: () => dispatch(agentsActions.resetAgents()),
      resetReports: () => dispatch(reportsActions.resetReports()),
      closeServerConnection: () => dispatch(cloudNetworkActions.closeServerConnection()),
      resetServerConnection: () => dispatch(cloudNetworkActions.resetServerConnection()),
   }
}

export default connect(mapStateToProps, mapDispatchToProps)(MenuModal)
