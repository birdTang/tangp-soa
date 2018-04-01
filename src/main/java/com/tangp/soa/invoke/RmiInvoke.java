package com.tangp.soa.invoke;

import java.rmi.RemoteException;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.tangp.soa.configBean.Reference;
import com.tangp.soa.loadbalance.NodeInfo;
import com.tangp.soa.rmi.RmiUtil;
import com.tangp.soa.rmi.SoaRmi;

public class RmiInvoke implements Invoke {

	@Override
	public String invoke(Invocation invocation) throws Exception {
		// 获取服务提供者列表
		try {
			List<String> registerInfo = invocation.getReference().getRegistryInfo();
			// 负载均衡算法
			Reference reference = invocation.getReference();
			String loadBalance = reference.getLoadbalance();
			NodeInfo nodeinfo = reference.getLoadBalances().get(loadBalance).doSelect(registerInfo);
			// 我们调用远程的生产者是传输的json字符串
			// 根据serviceid去对端生产者的spring容器中获取serviceid对应的实例
			// 根据methodName和methodType获取实例的method对象
			// 然后反射调用method方法
			JSONObject sendparam = new JSONObject();
			sendparam.put("methodName", invocation.getMethod().getName());
			sendparam.put("methodParams", invocation.getArgs());
			sendparam.put("serviceId", reference.getId());
			sendparam.put("paramTypes", invocation.getMethod().getParameterTypes());

			SoaRmi soaRmi = RmiUtil.startRmiClient(nodeinfo, "tangpsoarmi");
			return soaRmi.invoke(sendparam.toJSONString());

		} catch (RemoteException e) {
			throw e;
		}

	}

}
