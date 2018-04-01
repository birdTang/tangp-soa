package com.tangp.soa.configBean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.tangp.soa.registry.BaseRegistry;
import com.tangp.soa.registry.RedisRegistry;

public class Registery extends BaseConfigBean implements Serializable {

	private static final long serialVersionUID = -4320351582474714527L;
	private String address;
	private String protocol;
	// 初始化注册中心集合
	private static Map<String, BaseRegistry> registrys = new HashMap<>();

	static {
		registrys.put("redis", new RedisRegistry());
	}

	public static Map<String, BaseRegistry> getRegistrys() {
		return registrys;
	}

	public static void setRegistrys(Map<String, BaseRegistry> registrys) {
		Registery.registrys = registrys;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
