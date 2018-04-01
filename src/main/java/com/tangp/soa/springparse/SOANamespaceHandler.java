package com.tangp.soa.springparse;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.tangp.soa.configBean.Protocol;
import com.tangp.soa.configBean.Reference;
import com.tangp.soa.configBean.Registery;
import com.tangp.soa.configBean.Service;

/**
 * 自定义标签解析类--注册点
 * @author tangpeng
 *
 */
public class SOANamespaceHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("registery",new RegisteryBeanDefinitionParse(Registery.class));
		registerBeanDefinitionParser("protocol",new ProtocolBeanDefinitionParse(Protocol.class));
		registerBeanDefinitionParser("reference",new ReferenceBeanDefinitionParse(Reference.class));
		registerBeanDefinitionParser("service",new ServiceBeanDefinitionParse(Service.class));
		
	}


}
