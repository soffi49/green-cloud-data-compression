import axios from 'axios'
import { call, delay, put } from 'redux-saga/effects'
import { managinSystemActions } from '../actions'
import { handleConnectionError } from 'store/common'
import { MenuTab } from '@types'
import { cloudNetworkActions } from 'store/cloud-network'

/**
 * Method retrieves clients from backend
 *
 */
export function* fetchManagingState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_MANAGING_FRONTEND_URL + '/managing', {
               timeout: 2000
            })
         )
         yield put(managinSystemActions.setAdaptationData(data.managing))
         yield put(cloudNetworkActions.openServerConnection(MenuTab.ADAPTATION))

         yield delay(200)
      } catch (err: any) {
         handleConnectionError(err, 'Managing', MenuTab.ADAPTATION)
      }
   }
}
