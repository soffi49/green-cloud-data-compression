package com.greencloud.application.agents.cloudnetwork.domain;

import com.greencloud.application.agents.cloudnetwork.behaviour.df.AskServerForPowerInformation;

/**
 * Enum with types that describe a way in which Cloud Network should update its maximum capacity.
 * Types are used in {@link AskServerForPowerInformation} for selecting appropriate handlers executed
 * upon receiving responses from Server Agents.
 */
public enum CloudNetworkPowerUpdateType {
	UPDATE_ALL, DECREMENT_CAPACITY, INCREMENT_CAPACITY
}
