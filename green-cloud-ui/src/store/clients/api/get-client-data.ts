import axios from 'axios'
import { call, put, select } from 'redux-saga/effects'
import { clientActions } from '../actions'
import { selectChosenClientId } from '../selectors'

/**
 * Method retrieves client data from backend

 */
export function* getClientData() {
   const selectedClient: string | null = yield select(selectChosenClientId)
   if (selectedClient) {
      try {
         const url = `/client?name=${selectedClient}`
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_CLIENTS_FRONTEND_URL + url, {
               timeout: 2000
            })
         )
         yield put(clientActions.setClientData(data))
      } catch (err: any) {
         console.error(`An error occured while fetching the client data: ` + err)
         throw err
      }
   }
}
