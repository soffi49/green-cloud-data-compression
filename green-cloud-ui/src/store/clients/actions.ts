import { createAction } from '@reduxjs/toolkit'
import { clientSlice } from './slice'
import { GET_CLIENT_DATA } from 'store/saga-types'

/**
 * Set of clients actions
 */
export const clientActions = clientSlice.actions

/**
 * Action that fetches client data
 */
export const getClientDataAction = createAction(GET_CLIENT_DATA)
