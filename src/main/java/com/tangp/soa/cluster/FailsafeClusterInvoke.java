package com.tangp.soa.cluster;

import com.tangp.soa.invoke.Invocation;
import com.tangp.soa.invoke.Invoke;

/**
 * 调用节点失败，直接忽略
 * 
 * @author tangpeng
 *
 */
public class FailsafeClusterInvoke implements Cluster {

	@Override
	public String invoke(Invocation invocation) throws Exception {
		Invoke invoke = invocation.getInvoke();

		try {
			return invoke.invoke(invocation);
		} catch (Exception e) {
			e.printStackTrace();
			return "忽略";
		}
	}

}
