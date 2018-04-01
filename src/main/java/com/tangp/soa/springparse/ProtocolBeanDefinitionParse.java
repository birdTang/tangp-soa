package com.tangp.soa.springparse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Protocol 标签解析类
 * 
 * @author tangpeng
 *
 */
public class ProtocolBeanDefinitionParse implements BeanDefinitionParser {

	// Protocol 对象
	private Class<?> beanClass;

	public ProtocolBeanDefinitionParse(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		// spring 会把beanClass实例化 --》 大循化 BeanDefinitionNames容器
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String name = element.getAttribute("name");
		String host = element.getAttribute("host");
		String port = element.getAttribute("port");
		String contextpath = element.getAttribute("contextpath");
		if (name == null || "".equals(name)) {
			throw new RuntimeException("Protocol name 不能为空");
		}
		if (host == null || "".equals(host)) {
			throw new RuntimeException("Protocol host 不能为空");
		}
		if (port == null || "".equals(port)) {
			throw new RuntimeException("Protocol port 不能为空");
		}
		beanDefinition.getPropertyValues().addPropertyValue("name", name);
		beanDefinition.getPropertyValues().addPropertyValue("host", host);
		beanDefinition.getPropertyValues().addPropertyValue("port", port);
		beanDefinition.getPropertyValues().addPropertyValue("contextpath", contextpath);
		parserContext.getRegistry().registerBeanDefinition("protocol" + host + port, beanDefinition);
		return beanDefinition;
	}
}
