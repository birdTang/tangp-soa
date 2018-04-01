package com.tangp.soa.loadbalance;

import java.util.List;

public interface BaseLoadBalace {
	NodeInfo doSelect(List<String> registryInfo);
}
