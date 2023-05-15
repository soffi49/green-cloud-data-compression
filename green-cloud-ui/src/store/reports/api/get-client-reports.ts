import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { reportsActions } from '../actions'
import { handleConnectionError } from 'store/common'

/**
 * Method retrieves client reports from backend
 *
 */
export function* fetchClientReportsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_CLIENTS_FRONTEND_URL + '/reports/client', {
               timeout: 2000,
            })
         )
         yield put(reportsActions.updateClientsReports(data))

         yield delay(200)
      } catch (err: any) {
         yield handleConnectionError(err, 'Client reports')
      }
   }
}
