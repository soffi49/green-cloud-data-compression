package com.database.knowledge.domain.agent;

/**
 * Record represents AMS agent data
 * @param name name of the ams agent
 * @param address address of the agent platform for given ams
 */
public record AMSData(String name, String address) {
}
