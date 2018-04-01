package com.tangp.soa.configBean;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tangp.soa.registry.BaseRegisteryDelegate;

/**
 * 服务暴露 实体 ApplicationContextAware:用来获取spring上下文（容器）
 * InitializingBean:该接口afterPropertiesSet方法在Service对象示例化之后调用
 * 用来将service服务注册对应注册中心
 * 
 * @author tangpeng
 *
 */
public class Service extends BaseConfigBean implements Serializable, InitializingBean, ApplicationContextAware {

	private static final long serialVersionUID = -6641042171949701031L;

	private String intf;
	private String ref;
	private String protocol;
	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getIntf() {
		return intf;
	}

	public void setIntf(String intf) {
		this.intf = intf;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 委托模式注册服务信息，与注册中心解耦合
		BaseRegisteryDelegate.registry(ref, applicationContext);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Service.applicationContext = applicationContext;
	}

}
