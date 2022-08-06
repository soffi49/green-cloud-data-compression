package com.gui.agents.domain;

public class AgentLocation {

	private final String latitude;
	private final String longitude;

	public AgentLocation(final String latitude, final String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}
}

