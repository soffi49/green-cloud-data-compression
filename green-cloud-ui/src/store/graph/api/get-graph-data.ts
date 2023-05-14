import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { handleConnectionError } from 'store/common'
import { graphActions } from 'store/graph'

/**
 * Method retrieves graph data from backend
 *
 */
export function* fetchGraphState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_AGENTS_FRONTEND_URL + '/graph', {
               timeout: 2000,
            })
         )
         yield put(graphActions.setGraphData(data))

         yield delay(200)
      } catch (err: any) {
         handleConnectionError(err, 'Graph')
      }
   }
}
