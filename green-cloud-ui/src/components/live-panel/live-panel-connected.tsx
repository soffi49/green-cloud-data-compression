import {
   RootState,
   selectAdaptationActions,
   selectAdaptationGoals,
   selectChosenNetworkAgent,
   selectChosenNetworkAgentId,
   selectReports,
   selectReportsForChosenAgent
} from '@store'
import { connect } from 'react-redux'
import LivePanel from './live-panel'
import { selectSelectedTab } from 'store/navigator'

const mapStateToProps = (state: RootState) => {
   return {
      reports: selectReports(state),
      agentReports: selectReportsForChosenAgent(state),
      adaptations: selectAdaptationActions(state),
      agentData: selectChosenNetworkAgent(state),
      selectedAgent: selectChosenNetworkAgentId(state),
      selectedTab: selectSelectedTab(state),
      systemGoals: selectAdaptationGoals(state)
   }
}

export default connect(mapStateToProps, null)(LivePanel)
