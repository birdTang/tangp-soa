package com.tangp.soa.configBean;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.tangp.soa.cluster.Cluster;
import com.tangp.soa.cluster.FailfastClusterInvoke;
import com.tangp.soa.cluster.FailoverClusterInvoke;
import com.tangp.soa.cluster.FailsafeClusterInvoke;
import com.tangp.soa.invoke.HttpInvoke;
import com.tangp.soa.invoke.Invoke;
import com.tangp.soa.invoke.RmiInvoke;
import com.tangp.soa.loadbalance.BaseLoadBalace;
import com.tangp.soa.loadbalance.RandomLoadBalance;
import com.tangp.soa.loadbalance.RoundRobinLoadBalance;
import com.tangp.soa.proxy.advice.InvokeInvocationHandler;
import com.tangp.soa.registry.BaseRegisteryDelegate;

/**
 * protocol 协议标签
 * 
 * ApplicationContextAware:获取spring容器中的实例对象，需实现该接口
 * FactoryBean:由于Reference中配置了intf,所以在该类实现，spring初始化时
 * InitializingBean:对象实例化后做相应处理 init-method
 * 
 * @author tangpeng
 *
 */
public class Reference extends BaseConfigBean
		implements Serializable, FactoryBean, ApplicationContextAware, InitializingBean {

	private static final long serialVersionUID = -3501857527026134995L;
	// 客户端接口定义
	private String intf;
	// 客户端负载均衡配置
	private String loadbalance;
	// 客户端通信协议配置
	private String protocol;
	// 记录当前Reference对象定义的协议
	private Invoke invoke;

	// 集群容错
	private String cluster;
	private String retries;

	private static ApplicationContext applicationContext;
	// 通信协议集合
	private static Map<String, Invoke> invokes = new HashMap<>();
	// 负载均衡算法集合
	private static Map<String, BaseLoadBalace> loadBalances = new HashMap<>();
	// 这是多个生产者注册信息的集合
	private List<String> registryInfo = new ArrayList<>();
	// 集群容错处理类集合
	private static Map<String, Cluster> clusters = new HashMap<>();

	static {
		invokes.put("http", new HttpInvoke());
		invokes.put("rmi", new RmiInvoke());
		invokes.put("netty", null);

		loadBalances.put("random", new RandomLoadBalance());
		loadBalances.put("roundrob", new RoundRobinLoadBalance());

		clusters.put("failfast", new FailfastClusterInvoke());
		clusters.put("failover", new FailoverClusterInvoke());
		clusters.put("failsafe", new FailsafeClusterInvoke());
	}

	public Reference() {
		System.out.println("Reference 构造");
	}

	public static Map<String, Cluster> getClusters() {
		return clusters;
	}

	public static void setClusters(Map<String, Cluster> clusters) {
		Reference.clusters = clusters;
	}

	public static Map<String, BaseLoadBalace> getLoadBalances() {
		return loadBalances;
	}

	public static void setLoadBalances(Map<String, BaseLoadBalace> loadBalances) {
		Reference.loadBalances = loadBalances;
	}

	public List<String> getRegistryInfo() {
		return registryInfo;
	}

	public void setRegistryInfo(List<String> registryInfo) {
		this.registryInfo = registryInfo;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getIntf() {
		return intf;
	}

	public void setIntf(String intf) {
		this.intf = intf;
	}

	public String getLoadbalance() {
		return loadbalance;
	}

	public void setLoadbalance(String loadbalance) {
		this.loadbalance = loadbalance;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getRetries() {
		return retries;
	}

	public void setRetries(String retries) {
		this.retries = retries;
	}

	/**
	 * 拿到一个实例，这个方法是spring调用的,spring初始化的时候调用的---getBean方法里面调用
	 * ApplicationContext.getBean("id") getObject方法的返回值会交给spring 管理
	 * 
	 * getObject 返回的是intf接口的代理
	 */
	@Override
	public Object getObject() throws Exception {
		System.out.println("返回 Reference intf 代理对象");
		// 如果reference标签配置了protocol,则使用配置的
		if (protocol != null && !"".equals(protocol)) {
			invoke = invokes.get(protocol);
		} else { // 没有配置，则以protocol标签的为准
			// 根据类型查找容器中的协议对象
			Protocol protocol = applicationContext.getBean(Protocol.class);
			if (protocol != null) {
				invoke = invokes.get(protocol.getName());
			} else {
				invoke = invokes.get("http"); // 容错，没有给默认
			}
		}
		return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { Class.forName(intf) },
				new InvokeInvocationHandler(invoke, this));
	}

	@Override
	public Class getObjectType() {
		if (intf != null && !"".equals(intf)) {
			try {
				return Class.forName(intf);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Reference.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 委托模式，解耦
		registryInfo = BaseRegisteryDelegate.getRegistry(id, applicationContext);
		System.out.println(registryInfo);
	}

}
