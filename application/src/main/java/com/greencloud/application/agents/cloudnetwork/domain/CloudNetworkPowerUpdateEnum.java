package com.greencloud.application.agents.cloudnetwork.domain;

import com.greencloud.application.agents.cloudnetwork.behaviour.df.initiator.InitiateCapacityUpdate;

/**
 * Enum with types that describe a way in which Cloud Network should update its maximum capacity.
 * Types are used in {@link InitiateCapacityUpdate} for selecting appropriate handlers executed
 * upon receiving power messages from Server Agents.
 */
public enum CloudNetworkPowerUpdateEnum {
	UPDATE_ALL, DECREMENT_CAPACITY, INCREMENT_CAPACITY
}
