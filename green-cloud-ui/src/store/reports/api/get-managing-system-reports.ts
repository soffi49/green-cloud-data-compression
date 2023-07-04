import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { reportsActions } from '../actions'
import { handleConnectionError } from 'store/common'
import { MenuTab } from '@types'
import { cloudNetworkActions } from 'store/cloud-network'

/**
 * Method retrieves managing system reports from backend
 *
 */
export function* fetchManagingSystemReportsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_MANAGING_FRONTEND_URL + '/reports/managing', {
               timeout: 2000
            })
         )
         yield put(reportsActions.updateManagingReports(data))
         yield put(cloudNetworkActions.openServerConnection(MenuTab.ADAPTATION))

         yield delay(200)
      } catch (err: any) {
         yield handleConnectionError(err, 'Managing reports', MenuTab.ADAPTATION)
      }
   }
}
