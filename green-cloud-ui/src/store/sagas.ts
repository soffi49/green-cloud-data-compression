import { all, call, put, race, take, delay, select } from 'redux-saga/effects'
import axios from 'axios'
import { agentsActions } from './agent'
import { cloudNetworkActions, selectConnectionToast } from './cloud-network'
import { toast } from 'react-toastify'
import { managinSystemActions } from './managing-system'
import { graphActions } from './graph'
import { reportsActions, selectSystemStartTime } from './reports'

/**
 * Saga responsible for watching the agents' state fetching action
 */
export function* watchFetchAgentsState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchAgentsState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Saga responsible for watching the clients' state fetching action
 */
export function* watchFetchClientsState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchClientsState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Saga responsible for watching the managing's state fetching action
 */
export function* watchFetchManagingState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchManagingkState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Saga responsible for watching the network statistic's state fetching action
 */
export function* watchFetchNetworkState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)
      yield race({
         fetch: call(fetchNetworkState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

// /**
//  * Saga responsible for fetching the reports data
//  */
// export function* fetchReport() {
//    while (true) {
//       yield take(cloudNetworkActions.resetServerConnection)
//       yield race({
//          fetch: call(fetchReportState),
//          finish: take(cloudNetworkActions.closeServerConnection),
//       })
//    }
// }

/**
 * Root store sagas
 */
export default function* rootSagas() {
   yield all([watchFetchAgentsState(), watchFetchClientsState(), watchFetchNetworkState(), watchFetchManagingState()])
}

function* fetchAgentsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_AGENTS_FRONTEND_URL + '/agents', {
               timeout: 2000,
            })
         )
         yield all([
            put(agentsActions.setAgentsData(data.agents)),
            put(graphActions.setGraphData(data.graph)),
            put(reportsActions.updateAgentsReports(data.agentsReports)),
         ])

         yield delay(200)
      } catch (err: any) {
         if (err.code === 'ERR_NETWORK') {
            console.error('Agents server is disconnected')

            const connectionToast: boolean | null = yield select(selectConnectionToast)

            if (connectionToast) {
               toast.dismiss()
               toast.error('Agents server is disconnected')
            }
            yield put(cloudNetworkActions.closeServerConnection())
         }
         console.error('An error occured while fetching the agents data: ' + err)
      }
   }
}

function* fetchClientsState() {
   while (true) {
      try {
         const { data } = yield call(() =>
            axios.get(process.env.REACT_APP_WEB_SOCKET_CLIENTS_FRONTEND_URL + '/clients', {
               timeout: 2000,
            })
         )
         yield all([
            put(agentsActions.setClientsData(data.clients)),
            put(reportsActions.updateClientsReports(data.clientsReports)),
         ])

         yield delay(200)
      } catch (err: any) {
         if (err.code === 'ERR_NETWORK') {
            console.error('Clients server is disconnected')

            const connectionToast: boolean | null = yield select(selectConnectionToast)

            if (connectionToast) {
               toast.dismiss()
               toast.error('Clients server is disconnected')
            }
            yield put(cloudNetworkActions.closeServerConnection())
         }
         console.error('An error occured while fetching the clients data: ' + err)
      }
   }
}

function* fetchNetworkState() {
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
         if (err.code === 'ERR_NETWORK') {
            console.error('Server is disconnected')

            const connectionToast: boolean | null = yield select(selectConnectionToast)

            if (connectionToast) {
               toast.dismiss()
               toast.error('Server is disconnected')
            }
            yield put(cloudNetworkActions.closeServerConnection())
         }
         console.error('An error occured while fetching the data: ' + err)
      }
   }
}

function* fetchManagingkState() {
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
         if (err.code === 'ERR_NETWORK') {
            console.error('Managing socket server is disconnected')

            const connectionToast: boolean | null = yield select(selectConnectionToast)

            if (connectionToast) {
               toast.dismiss()
               toast.error('Managing socket server is disconnected')
            }
            yield put(cloudNetworkActions.closeServerConnection())
         }
         console.error('An error occured while fetching the managing system data: ' + err)
      }
   }
}

// function* fetchState() {
//    while (true) {
//       try {
//          const { data } = yield call(() =>
//             axios.get(process.env.REACT_APP_WEB_SOCKET_FRONTEND_URL, {
//                timeout: 2000,
//             })
//          )
//          yield all([
//             put(agentsActions.setAgentsData(data.agents)),
//             put(cloudNetworkActions.setNetworkData(data.network)),
//             put(managinSystemActions.setAdaptationData(data.managingSystem)),
//             put(graphActions.setGraphData(data.graph)),
//          ])
//          yield put(cloudNetworkActions.openServerConnection())

//          const startTime: number | null = yield select(selectSystemStartTime)

//          if (startTime === null) {
//             yield put(
//                reportsActions.updateSystemTime({ time: data.systemStartTime, secondsPerHour: data.secondsPerHour })
//             )
//          }

//          yield delay(200)
//       } catch (err: any) {
//          if (err.code === 'ERR_NETWORK') {
//             console.error('Server is disconnected')

//             const connectionToast: boolean | null = yield select(selectConnectionToast)

//             if (connectionToast) {
//                toast.dismiss()
//                toast.error('Server is disconnected')
//             }
//             yield put(cloudNetworkActions.closeServerConnection())
//          }
//          console.error('An error occured while fetching the agent data: ' + err)
//       }
//    }
// }

// function* fetchReportState() {
//    while (true) {
//       try {
//          const { data } = yield call(() =>
//             axios.get(process.env.REACT_APP_WEB_SOCKET_FRONTEND_URL + '/reports', {
//                timeout: 2000,
//             })
//          )
//          yield put(reportsActions.updateReports(data))
//          yield delay(1000)
//       } catch (err: any) {
//          if (err.code === 'ERR_NETWORK') {
//             console.error('Server is disconnected')
//          }
//          console.error('An error occured while fetching the reports data: ' + err)
//       }
//    }
// }
