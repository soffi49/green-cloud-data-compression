import { RootState, selectChosenNetworkAgent } from '@store'
import { connect } from 'react-redux'
import { MainPanel } from './main-panel'
import { selectSelectedTab } from 'store/navigator'

const mapStateToProps = (state: RootState) => {
   return {
      selectedAgent: selectChosenNetworkAgent(state),
      selectedTab: selectSelectedTab(state)
   }
}

export default connect(mapStateToProps, null)(MainPanel)
