package com.tangp.soa.cluster;

import com.tangp.soa.invoke.Invocation;

public interface Cluster {
	String invoke(Invocation invocation) throws Exception;
}
