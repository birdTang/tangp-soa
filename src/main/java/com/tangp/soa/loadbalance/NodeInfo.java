package com.tangp.soa.loadbalance;

/**
 * 对负载均衡选中服务提供者的封装 不同协议调用方式不同，但host,port，contextpath都需要的
 * 
 * @author tangpeng
 *
 */
public class NodeInfo {
	private String host;
	private String port;
	private String contextpath;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getContextpath() {
		return contextpath;
	}

	public void setContextpath(String contextpath) {
		this.contextpath = contextpath;
	}

}
