import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { reportsActions } from '../actions'
import { handleConnectionError } from 'store/common'

/**
 * Method retrieves network reports from backend
 *
 */
export function* fetchNetworkReportsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_NETWORK_FRONTEND_URL + '/reports/network', {
               timeout: 2000,
            })
         )
         yield put(reportsActions.updateNetworkReports(data))

         yield delay(200)
      } catch (err: any) {
         yield handleConnectionError(err, 'Network reports')
      }
   }
}
