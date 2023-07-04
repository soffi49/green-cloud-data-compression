import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { clientActions } from '../actions'
import { getClientData } from './get-client-data'
import { handleConnectionError } from 'store/common'
import { MenuTab } from '@types'
import { cloudNetworkActions } from 'store/cloud-network'

/**
 * Method retrieves clients from backend
 *
 */
export function* fetchClientsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_CLIENTS_FRONTEND_URL + '/clients', {
               timeout: 2000
            })
         )
         yield put(clientActions.setClients(data.clients))
         yield put(cloudNetworkActions.openServerConnection(MenuTab.CLIENTS))
         yield call(getClientData)

         yield delay(200)
      } catch (err: any) {
         yield handleConnectionError(err, 'Clients', MenuTab.CLIENTS)
      }
   }
}
