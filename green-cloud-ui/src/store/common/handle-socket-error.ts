import { toast } from 'react-toastify'
import { put, select } from 'redux-saga/effects'
import { cloudNetworkActions, selectConnectionToast } from '../cloud-network'

/**
 * Method provides default handler for connection error
 *
 * @param err - error that was caught
 * @param socketName - name of the socket that has thrown the error
 */
export function* handleConnectionError(err: any, socketName: string) {
   if (err.code === 'ERR_NETWORK') {
      console.error(`${socketName} socket server is disconnected`)

      const connectionToast: boolean | null = yield select(selectConnectionToast)

      if (connectionToast) {
         toast.dismiss()
         toast.error(`${socketName} socket server is disconnected`)
      }
      yield put(cloudNetworkActions.closeServerConnection())
   }
   console.error(`An error occured while fetching the ${socketName.toLowerCase()} data: ` + err)
}
