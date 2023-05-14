import { all, call, race, select, take } from 'redux-saga/effects'
import { fetchAgentsState } from './agent'
import { cloudNetworkActions } from './cloud-network'
import { fetchManagingkState } from './managing-system'
import { fetchClientsState, getClientData } from './clients'
import { GET_CLIENT_DATA } from './saga-types'
import { fetchNetworkState } from './cloud-network/api'
import { navigatorActions, selectSelectedTab } from './navigator'
import { MenuTab } from '@types'
import { fetchGraphState } from './graph'

/**
 * Saga responsible for watching the agents' state fetching action
 */
export function* watchFetchAgentsState() {
   while (true) {
      yield race({
         resetConnection: take(cloudNetworkActions.resetServerConnection),
         selectTab: take(navigatorActions.setSelectedTab),
      })
      const selectedTab: MenuTab = yield select(selectSelectedTab)

      if (selectedTab === MenuTab.AGENTS) {
         yield race({
            fetch: call(fetchAgentsState),
            finish: take(cloudNetworkActions.closeServerConnection),
            changeTab: take(navigatorActions.setSelectedTab),
         })
      }
   }
}

/**
 * Saga responsible for watching the graph' state fetching action
 */
export function* watchFetchGraphState() {
   while (true) {
      yield take(cloudNetworkActions.resetServerConnection)

      yield race({
         fetch: call(fetchGraphState),
         finish: take(cloudNetworkActions.closeServerConnection),
      })
   }
}

/**
 * Saga responsible for watching the agents' state fetching action
 */
export function* watchGetClientState() {
   while (true) {
      yield take(GET_CLIENT_DATA)
      yield call(getClientData)
   }
}

/**
 * Saga responsible for watching the clients' state fetching action
 */
export function* watchFetchClientsState() {
   while (true) {
      yield race({
         resetConnection: take(cloudNetworkActions.resetServerConnection),
         selectTab: take(navigatorActions.setSelectedTab),
      })
      const selectedTab: MenuTab = yield select(selectSelectedTab)

      if (selectedTab === MenuTab.CLIENTS) {
         yield race({
            fetch: call(fetchClientsState),
            finish: take(cloudNetworkActions.closeServerConnection),
            changeTab: take(navigatorActions.setSelectedTab),
         })
      }
   }
}

/**
 * Saga responsible for watching the managing's state fetching action
 */
export function* watchFetchManagingState() {
   while (true) {
      yield race({
         resetConnection: take(cloudNetworkActions.resetServerConnection),
         selectTab: take(navigatorActions.setSelectedTab),
      })
      const selectedTab: MenuTab = yield select(selectSelectedTab)

      if (selectedTab === MenuTab.ADAPTATION) {
         yield race({
            fetch: call(fetchManagingkState),
            finish: take(cloudNetworkActions.closeServerConnection),
            changeTab: take(navigatorActions.setSelectedTab),
         })
      }
   }
}

/**
 * Saga responsible for watching the network statistic's state fetching action
 */
export function* watchFetchNetworkState() {
   while (true) {
      yield race({
         resetConnection: take(cloudNetworkActions.resetServerConnection),
         selectTab: take(navigatorActions.setSelectedTab),
      })
      const selectedTab: MenuTab = yield select(selectSelectedTab)

      if (selectedTab === MenuTab.CLOUD_SUMMARY) {
         yield race({
            fetch: call(fetchNetworkState),
            finish: take(cloudNetworkActions.closeServerConnection),
            changeTab: take(navigatorActions.setSelectedTab),
         })
      }
   }
}

/**
 * Root store sagas
 */
export default function* rootSagas() {
   yield all([
      watchFetchAgentsState(),
      watchFetchGraphState(),
      watchFetchClientsState(),
      watchFetchNetworkState(),
      watchFetchManagingState(),
      watchGetClientState(),
   ])
}
