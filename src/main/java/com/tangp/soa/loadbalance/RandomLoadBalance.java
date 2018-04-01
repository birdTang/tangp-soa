package com.tangp.soa.loadbalance;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;

/**
 * 随机负载均衡算法
 * 
 * @author tangpeng
 *
 */
public class RandomLoadBalance implements BaseLoadBalace {

	@Override
	public NodeInfo doSelect(List<String> registryInfo) {
		Random random = new Random();
		int index = random.nextInt(registryInfo.size());
		String registry = registryInfo.get(index);
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
