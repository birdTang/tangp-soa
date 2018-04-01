package com.tangp.soa.configBean;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.tangp.soa.netty.NettyUtil;
import com.tangp.soa.rmi.RmiUtil;

/**
 * 通信协议实体类
 * 
 * @author tangpeng
 *
 */
public class Protocol extends BaseConfigBean
		implements Serializable, InitializingBean, ApplicationListener<ContextRefreshedEvent> {

	private static final long serialVersionUID = -4440572489780264395L;

	private String name;
	private String host;
	private String port;
	private String contextpath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getContextpath() {
		return contextpath;
	}

	public void setContextpath(String contextpath) {
		this.contextpath = contextpath;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (name.equalsIgnoreCase("rmi")) {
			RmiUtil.startRmiServer(host, port, "tangpsoarmi");
		}
	}

	/**
	 * 监听ContextRefreshedEvent事件 ContextRefreshedEvent事件是在spring加载完后触发的事件
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (!ContextRefreshedEvent.class.getName().equals(event.getClass().getName())) {
			return;
		}
		if (!"netty".equalsIgnoreCase(name)) {
			return;
		}
		/**
		 * 在spring加载完毕后，另启动一个线程来启动netty服务。 防止与netty启动连接后一直阻塞在等待客户端消息处，导致tomcat启动不成功。
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					NettyUtil.startServer(port);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();

	}

}
