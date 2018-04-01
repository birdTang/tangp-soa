package com.tangp.soa.springparse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Reference 标签解析类
 * 
 * @author tangpeng
 *
 */
public class ReferenceBeanDefinitionParse implements BeanDefinitionParser {

	// Reference 对象
	private Class<?> beanClass;

	public ReferenceBeanDefinitionParse(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		// spring 会把beanClass实例化 --》 大循化 BeanDefinitionNames容器
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String id = element.getAttribute("id");
		String intf = element.getAttribute("interface");
		String loadbalance = element.getAttribute("loadbalance");
		String protocol = element.getAttribute("protocol");
		String cluster = element.getAttribute("cluster");
		String retries = element.getAttribute("retries");
		if (id == null || "".equals(id)) {
			throw new RuntimeException("Reference id 不能为空");
		}
		if (intf == null || "".equals(intf)) {
			throw new RuntimeException("Reference interface 不能为空");
		}
		if (protocol == null || "".equals(protocol)) {
			throw new RuntimeException("Reference protocol 不能为空");
		}
		if (loadbalance == null || "".equals(loadbalance)) {
			throw new RuntimeException("Reference loadbalance 不能为空");
		}
		beanDefinition.getPropertyValues().addPropertyValue("id", id);
		beanDefinition.getPropertyValues().addPropertyValue("intf", intf);
		beanDefinition.getPropertyValues().addPropertyValue("loadbalance", loadbalance);
		beanDefinition.getPropertyValues().addPropertyValue("protocol", protocol);
		beanDefinition.getPropertyValues().addPropertyValue("cluster", cluster);
		beanDefinition.getPropertyValues().addPropertyValue("retries", retries);
		// 注册beanDefinition
		parserContext.getRegistry().registerBeanDefinition("reference" + id, beanDefinition);
		return beanDefinition;
	}

}
