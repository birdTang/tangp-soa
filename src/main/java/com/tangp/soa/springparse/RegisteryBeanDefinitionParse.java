package com.tangp.soa.springparse;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Register 标签解析类
 * 
 * @author tangpeng
 *
 */
public class RegisteryBeanDefinitionParse implements BeanDefinitionParser {

	// Registery 对象
	private Class<?> beanClass;

	public RegisteryBeanDefinitionParse(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition();
		// spring 会把beanClass实例化 --》 大循化 BeanDefinitionNames容器
		beanDefinition.setBeanClass(beanClass);
		beanDefinition.setLazyInit(false);
		String protocol = element.getAttribute("protocol");
		String address = element.getAttribute("address");

		if (protocol == null || "".equals(protocol)) {
			throw new RuntimeException("Registery protocol 不能为空");
		}
		if (address == null || "".equals(address)) {
			throw new RuntimeException("Registery address 不能为空");
		}
		beanDefinition.getPropertyValues().addPropertyValue("protocol", protocol);
		beanDefinition.getPropertyValues().addPropertyValue("address", address);
		parserContext.getRegistry().registerBeanDefinition("registery" + address, beanDefinition);
		return beanDefinition;
	}

}
