import axios from 'axios'
import { all, call, delay, put } from 'redux-saga/effects'
import { reportsActions } from 'store/reports'
import { handleConnectionError } from 'store/common'
import { agentsActions } from '../actions'

/**
 * Method retrieves agents from backend
 *
 */
export function* fetchAgentsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_AGENTS_FRONTEND_URL + '/agents', {
               timeout: 2000,
            })
         )
         yield all([
            put(agentsActions.setAgentsData(data.agents)),
            put(reportsActions.updateAgentsReports(data.agentsReports)),
         ])

         yield delay(200)
      } catch (err: any) {
         handleConnectionError(err, 'Agents')
      }
   }
}
