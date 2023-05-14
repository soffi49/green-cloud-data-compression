import { AppDispatch, clientActions, RootState, selectChosenClient, selectClients } from '@store'
import { connect } from 'react-redux'
import { ClientPanel } from './client-panel'
import { GET_CLIENT_DATA } from 'store/saga-types'

const mapStateToProps = (state: RootState) => {
   return {
      clients: selectClients(state),
      selectedClient: selectChosenClient(state),
   }
}

const mapDispatchToProps = (dispatch: AppDispatch) => {
   return {
      setSelectedClient: (client: string | null) => dispatch(clientActions.setSelectedClient(client)),
      updateClientData: () => dispatch({ type: GET_CLIENT_DATA }),
   }
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientPanel)
