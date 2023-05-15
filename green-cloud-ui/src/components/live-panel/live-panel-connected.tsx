import {
   RootState,
   selectChosenNetworkAgent,
   selectChosenNetworkAgentId,
   selectReports,
   selectReportsForChosenAgent,
} from '@store'
import { connect } from 'react-redux'
import LivePanel from './live-panel'
import { selectSelectedTab } from 'store/navigator'

const mapStateToProps = (state: RootState) => {
   return {
      reports: selectReports(state),
      agentReports: selectReportsForChosenAgent(state),
      agentData: selectChosenNetworkAgent(state),
      selectedAgent: selectChosenNetworkAgentId(state),
      selectedTab: selectSelectedTab(state),
   }
}

export default connect(mapStateToProps, null)(LivePanel)
