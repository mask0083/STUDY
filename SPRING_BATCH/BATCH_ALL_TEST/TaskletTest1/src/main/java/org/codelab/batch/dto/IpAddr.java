package org.codelab.batch.dto;

import lombok.Data;

@Data
public class IpAddr {
	private String ip;
	private boolean filtered = false;

	public IpAddr() {}

	public IpAddr(String ip) {
		this.ip = ip;
	}
}