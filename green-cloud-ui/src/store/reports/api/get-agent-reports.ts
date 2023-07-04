import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { reportsActions } from '../actions'
import { handleConnectionError } from 'store/common'
import { MenuTab } from '@types'
import { cloudNetworkActions } from 'store/cloud-network'

/**
 * Method retrieves agent reports from backend
 *
 */
export function* fetchAgentReportsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_AGENTS_FRONTEND_URL + '/reports/agent', {
               timeout: 2000
            })
         )
         yield put(reportsActions.updateAgentsReports(data))
         yield put(cloudNetworkActions.openServerConnection(MenuTab.AGENTS))

         yield delay(200)
      } catch (err: any) {
         yield handleConnectionError(err, 'Agent reports', MenuTab.AGENTS)
      }
   }
}
