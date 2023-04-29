import { RootState, selectChosenNetworkAgent } from '@store'
import { connect } from 'react-redux'
import GraphPanel from './graph-panel'

const mapStateToProps = (state: RootState) => {
   return {
      selectedAgent: selectChosenNetworkAgent(state),
   }
}

export default connect(mapStateToProps, null)(GraphPanel)
