package com.tangp.soa.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.tangp.soa.configBean.Protocol;
import com.tangp.soa.configBean.Registery;
import com.tangp.soa.configBean.Service;
import com.tangp.soa.redis.RedisApi;

/**
 * redis 注册中心注册服务
 * 
 * @author tangpeng
 *
 */
public class RedisRegistry implements BaseRegistry {

	@Override
	public void registry(String ref, ApplicationContext application) {
		try {
			Protocol protocol = application.getBean(Protocol.class);
			// 获取service所有实例对象--xml中会有多个service标签
			Map<String, Service> services = application.getBeansOfType(Service.class);
			// 获取注册中心地址
			Registery registery = application.getBean(Registery.class);

			RedisApi.createJedisPool(registery.getAddress());

			for (Map.Entry<String, Service> entry : services.entrySet()) {
				if (entry.getValue().getRef().equals(ref)) {
					JSONObject jo = new JSONObject();
					jo.put("protocol", JSONObject.toJSONString(protocol));
					jo.put("service", JSONObject.toJSONString(entry.getValue()));

					JSONObject ipport = new JSONObject();
					ipport.put(protocol.getHost() + ":" + protocol.getPort(), jo);
					lpush(ipport, ref);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void lpush(JSONObject ipport, String key) {
		// key==ref的值，存在，
		// 则需要对相同key服务的不同服务实例（分布式，集群环境中）做去重
		if (RedisApi.exists(key)) {
			Set<String> keys = ipport.keySet();
			String ipportStr = "";
			// 该循环只会循环一次，key--->host:port
			for (String kk : keys) {
				ipportStr = kk;
			}

			// 获取redis中存放的老信息
			List<String> oldRegistryInfo = RedisApi.lrange(key);
			List<String> newRegistryInfo = new ArrayList<>();
			boolean isOld = false;

			for (String node : oldRegistryInfo) {
				JSONObject jo = JSONObject.parseObject(node);
				if (jo.containsKey(ipportStr)) {
					// 该节点服务实例已存在则用新的替换
					newRegistryInfo.add(ipport.toJSONString());
					isOld = true;
				} else {
					// 不存在，在新的容器中，保存原来的节点服务信息
					newRegistryInfo.add(node);
				}
			}
			if (isOld) {
				// 这里是老机器启动去重
				if (newRegistryInfo.size() > 0) {
					RedisApi.del(key);
					String[] newReStr = new String[newRegistryInfo.size()];
					for (int i = 0; i < newRegistryInfo.size(); i++) {
						newReStr[i] = newRegistryInfo.get(i);
					}
					RedisApi.lpush(key, newReStr);
				}
			} else {
				// 这里是加入新启动的机器
				RedisApi.lpush(key, ipport.toJSONString());
			}

		} else {
			RedisApi.lpush(key, ipport.toJSONString());
		}
	}

	@Override
	public List<String> getRegistry(String id, ApplicationContext application) {
		// 获取注册中心地址
		Registery registery = application.getBean(Registery.class);

		RedisApi.createJedisPool(registery.getAddress());
		if (RedisApi.exists(id)) {
			return RedisApi.lrange(id);
		}
		return null;
	}
}
