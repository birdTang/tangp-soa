package com.tangp.soa.invoke;

import java.lang.reflect.Method;

import com.tangp.soa.configBean.Reference;

/**
 * 封装代理类参数
 * 
 * @author tangpeng
 *
 */
public class Invocation {

	private Method method;

	private Object[] args;

	private Reference reference;

	private Invoke invoke;

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Invoke getInvoke() {
		return invoke;
	}

	public void setInvoke(Invoke invoke) {
		this.invoke = invoke;
	}

}
