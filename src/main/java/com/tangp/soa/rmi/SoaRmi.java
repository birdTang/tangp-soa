package com.tangp.soa.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 固定写法： 必须继承Remote，必须抛出RemoteException
 * 
 * @author tangpeng
 *
 */
public interface SoaRmi extends Remote {
	public String invoke(String params) throws RemoteException;
}
