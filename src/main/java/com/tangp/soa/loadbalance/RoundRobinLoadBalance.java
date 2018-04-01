package com.tangp.soa.loadbalance;

import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class RoundRobinLoadBalance implements BaseLoadBalace {
	private static Integer index = 0;

	@Override
	public NodeInfo doSelect(List<String> registryInfo) {
		synchronized (index) {
			if (index > registryInfo.size()) {
				index = 0;
			}

			String registry = registryInfo.get(index);
			index++;
			JSONObject registryJo = JSONObject.parseObject(registry);
			// 获取protocol，和service
			Collection values = registryJo.values();
			JSONObject node = new JSONObject();
			for (Object value : values) {
				node = JSONObject.parseObject(value.toString());
			}
			JSONObject protocol = node.getJSONObject("protocol");
			NodeInfo nodeinfo = new NodeInfo();
			nodeinfo.setHost(protocol.get("host") != null ? protocol.getString("host") : "");
			nodeinfo.setPort(protocol.get("port") != null ? protocol.getString("port") : "");
			nodeinfo.setContextpath(protocol.get("contextpath") != null ? protocol.getString("contextpath") : "");
			return nodeinfo;
		}
	}

}
