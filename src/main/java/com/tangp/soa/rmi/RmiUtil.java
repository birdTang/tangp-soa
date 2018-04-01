package com.tangp.soa.rmi;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import com.tangp.soa.loadbalance.NodeInfo;

/**
 * Rmi 通信协议工具类
 * 
 * @author tangpeng
 *
 */
public class RmiUtil {
	/**
	 * 启动rmi服务
	 * 
	 * @param host
	 * @param port
	 * @param id
	 */
	public static void startRmiServer(String host, String port, String id) {
		try {
			SoaRmi soaRmi = new SoaRmiImpl();
			LocateRegistry.createRegistry(Integer.valueOf(port));

			Naming.bind("rmi://" + host + ":" + port + "/" + id, soaRmi);
			System.out.println("rmi 服务启动成功");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * rmi客户端连接
	 * 
	 * @param host
	 * @param port
	 * @param id
	 */
	public static SoaRmi startRmiClient(NodeInfo nodeInfo, String id) {
		String host = nodeInfo.getHost();
		String port = nodeInfo.getPort();

		try {
			return (SoaRmi) Naming.lookup("rmi://" + host + ":" + port + "/" + id);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
