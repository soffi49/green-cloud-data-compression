import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { managinSystemActions } from '../actions'
import { handleConnectionError } from 'store/common'

/**
 * Method retrieves clients from backend
 *
 */
export function* fetchManagingkState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_MANAGING_FRONTEND_URL + '/managing', {
               timeout: 2000,
            })
         )
         yield put(managinSystemActions.setAdaptationData(data.managing))

         yield delay(200)
      } catch (err: any) {
         handleConnectionError(err, 'Managing')
      }
   }
}
