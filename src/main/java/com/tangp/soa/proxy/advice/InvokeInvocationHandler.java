package com.tangp.soa.proxy.advice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.tangp.soa.cluster.Cluster;
import com.tangp.soa.configBean.Reference;
import com.tangp.soa.invoke.Invocation;
import com.tangp.soa.invoke.Invoke;

/**
 * InvokeInvocationHandler是一个advice,在这里面进行了RPC的远程调用 rmi,http,netty等
 * 
 * @author tangpeng
 *
 */
public class InvokeInvocationHandler implements InvocationHandler {

	private Invoke invoke;
	private Reference reference;

	public InvokeInvocationHandler(Invoke invoke, Reference reference) {
		this.invoke = invoke;
		this.reference = reference;
	}

	/**
	 * 在这个invoke里面，最终要调用provider提供的服务
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("已经获取代理示例，调用到InvokeInvocationHandler.invoke方法");
		Invocation invocation = new Invocation();
		invocation.setMethod(method);
		invocation.setArgs(args);
		invocation.setReference(reference);

		invocation.setInvoke(invoke);
		// 获取集群容错
		Cluster cluster = Reference.getClusters().get(reference.getCluster());

		String result = cluster.invoke(invocation);
		// invoke.invoke(invocation);
		return result;
	}

}
