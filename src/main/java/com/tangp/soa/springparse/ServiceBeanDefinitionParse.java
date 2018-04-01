package com.tangp.soa.springparse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Service 标签解析类
 * 
 * @author tangpeng
 *
 */
public class ServiceBeanDefinitionParse implements BeanDefinitionParser {

	// Service 对象
	private Class<?> beanClass;

	public ServiceBeanDefinitionParse(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		// spring 会把beanClass实例化 --》 大循化 BeanDefinitionNames容器
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String intf = element.getAttribute("interface");
		String ref = element.getAttribute("ref");
		String protocol = element.getAttribute("protocol");
		if (intf == null || "".equals(intf)) {
			throw new RuntimeException("Service interface 不能为空");
		}
		/*
		 * if(protocol==null || "".equals(protocol)) { throw new
		 * RuntimeException("Service protocol 不能为空"); }
		 */
		if (ref == null || "".equals(ref)) {
			throw new RuntimeException("Service ref 不能为空");
		}
		beanDefinition.getPropertyValues().addPropertyValue("intf", intf);
		beanDefinition.getPropertyValues().addPropertyValue("ref", ref);
		beanDefinition.getPropertyValues().addPropertyValue("protocol", protocol);
		parserContext.getRegistry().registerBeanDefinition("service" + ref + intf, beanDefinition);
		return beanDefinition;
	}

}
