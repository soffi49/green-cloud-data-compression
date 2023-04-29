import { RootState, selectChosenNetworkAgent } from '@store'
import { connect } from 'react-redux'
import { MainPanel } from './main-panel'

const mapStateToProps = (state: RootState) => {
   return {
      selectedAgent: selectChosenNetworkAgent(state),
   }
}

export default connect(mapStateToProps, null)(MainPanel)
