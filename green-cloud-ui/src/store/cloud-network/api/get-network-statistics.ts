import axios from 'axios'
import { all, call, delay, put, select } from 'redux-saga/effects'
import { reportsActions, selectSystemStartTime } from 'store/reports'
import { handleConnectionError } from 'store/common'
import { cloudNetworkActions } from '../actions'

/**
 * Method retrieves network statistics from backend
 *
 */
export function* fetchNetworkState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_NETWORK_FRONTEND_URL + '/network', {
               timeout: 2000,
            })
         )
         yield all([
            put(cloudNetworkActions.setNetworkData(data.network)),
            put(reportsActions.updateNetworkReports(data.networkReport)),
         ])
         yield put(cloudNetworkActions.openServerConnection())

         const startTime: number | null = yield select(selectSystemStartTime)

         if (startTime === null) {
            yield put(
               reportsActions.updateSystemTime({
                  time: data.simulation.systemStartTime,
                  secondsPerHour: data.simulation.secondsPerHour,
               })
            )
         }

         yield delay(200)
      } catch (err: any) {
         handleConnectionError(err, 'Network')
      }
   }
}
