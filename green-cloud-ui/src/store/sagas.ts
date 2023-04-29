import { all, call, put, race, take, delay, select } from 'redux-saga/effects'
import axios from 'axios'
import { agentsActions } from './agent'
import { cloudNetworkActions, selectConnectionToast } from './cloud-network'
import { toast } from 'react-toastify'
import { managinSystemActions } from './managing-system'
import { graphActions } from './graph'
import { reportsActions, selectSystemStartTime } from './reports'

/**
 * Saga responsible for watching the state fetching action
 */
export function* watchFetchState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Saga responsible for fetching the reports data
 */
export function* fetchReport() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchReportState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Root store sagas
 */
export default function* rootSagas() {
   yield all([watchFetchState(), fetchReport()])
}

function* fetchState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_FRONTEND_URL, {
               timeout: 2000,
            })
         )
         yield all([
            put(agentsActions.setAgentsData(data.agents)),
            put(cloudNetworkActions.setNetworkData(data.network)),
            put(managinSystemActions.setAdaptationData(data.managingSystem)),
            put(graphActions.setGraphData(data.graph)),
         ])
         yield put(cloudNetworkActions.openServerConnection())

         const startTime: number | null = yield select(selectSystemStartTime)

         if (startTime === null) {
            yield put(
               reportsActions.updateSystemTime({ time: data.systemStartTime, secondsPerHour: data.secondsPerHour })
            )
         }

         yield delay(200)
      } catch (err: any) {
         if (err.code === 'ERR_NETWORK') {
            console.error('Server is disconnected')

            const connectionToast: boolean | null = yield select(selectConnectionToast)

            if (connectionToast) {
               toast.dismiss()
               toast.error('Server is disconnected')
            }
            yield put(cloudNetworkActions.closeServerConnection())
         }
         console.error('An error occured while fetching the agent data: ' + err)
      }
   }
}

function* fetchReportState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_FRONTEND_URL + '/reports', {
               timeout: 2000,
            })
         )
         yield put(reportsActions.updateReports(data))
         yield delay(1000)
      } catch (err: any) {
         if (err.code === 'ERR_NETWORK') {
            console.error('Server is disconnected')
         }
         console.error('An error occured while fetching the reports data: ' + err)
      }
   }
}
