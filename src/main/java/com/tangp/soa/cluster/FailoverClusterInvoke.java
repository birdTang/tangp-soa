package com.tangp.soa.cluster;

import com.tangp.soa.invoke.Invocation;
import com.tangp.soa.invoke.Invoke;

/**
 * 集群容错--失败后重连N次
 * 
 * @author tangpeng
 *
 */
public class FailoverClusterInvoke implements Cluster {

	@Override
	public String invoke(Invocation invocation) throws Exception {
		String retries = invocation.getReference().getRetries();
		int retryInt = Integer.parseInt(retries);

		for (int i = 0; i < retryInt; i++) {
			try {
				Invoke invoke = invocation.getInvoke();
				return invoke.invoke(invocation);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			
		}
		throw new RuntimeException("retries" + retryInt + "全部失败");
	}

}
