import {
   agentsActions,
   AppDispatch,
   RootState,
   selectChosenNetworkAgentId,
   selectExistingConnections,
   selectNetworkNodes
} from '@store'
import { connect } from 'react-redux'
import { DisplayGraph } from './graph'
import { GET_AGENT_DATA } from 'store/saga-types'

const mapDispatchToProps = (dispatch: AppDispatch) => {
   return {
      setSelectedAgent: (id: string) => dispatch(agentsActions.setSelectedAgent(id)),
      updateAgentData: () => dispatch({ type: GET_AGENT_DATA })
   }
}

const mapStateToProps = (state: RootState) => {
   return {
      nodes: selectNetworkNodes(state),
      connections: selectExistingConnections(state),
      selectedAgent: selectChosenNetworkAgentId(state)
   }
}

export default connect(mapStateToProps, mapDispatchToProps)(DisplayGraph)
