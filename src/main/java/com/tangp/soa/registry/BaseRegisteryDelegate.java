package com.tangp.soa.registry;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.tangp.soa.configBean.Registery;

/**
 * 注册中心委托类
 * 
 * @author tangpeng
 *
 */
public class BaseRegisteryDelegate {
	/**
	 * 向注册中心注册服务
	 * 
	 * @param ref
	 * @param applicationContext
	 */
	public static void registry(String ref, ApplicationContext applicationContext) {
		// 获取注册中心信息，redis/zk....
		Registery registery = applicationContext.getBean(Registery.class);
		String protocol = registery.getProtocol();
		// 策略模式解耦实现不同注册中心的注册方式
		BaseRegistry registryBean = registery.getRegistrys().get(protocol);
		registryBean.registry(ref, applicationContext);
	}

	/**
	 * 从注册中心获取服务列表
	 * 
	 * @param id
	 *            ：注册中心存储的服务id == <service ref="XXXXX">
	 * @param applicationContext
	 * @return
	 */
	public static List<String> getRegistry(String id, ApplicationContext applicationContext) {
		// 获取注册中心信息，redis/zk....
		Registery registery = applicationContext.getBean(Registery.class);
		String protocol = registery.getProtocol();
		// 策略模式解耦实现不同注册中心的注册方式
		BaseRegistry registryBean = registery.getRegistrys().get(protocol);
		return registryBean.getRegistry(id, applicationContext);
	}
}
