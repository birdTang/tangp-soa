package com.tangp.soa.cluster;

import com.tangp.soa.invoke.Invocation;
import com.tangp.soa.invoke.Invoke;

/**
 * 集群容错--快速失败 这个如果调用节点异常，直接失败
 * 
 * @author tangpeng
 *
 */
public class FailfastClusterInvoke implements Cluster {

	@Override
	public String invoke(Invocation invocation) throws Exception {
		Invoke invoke = invocation.getInvoke();
		try {
			return invoke.invoke(invocation);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
