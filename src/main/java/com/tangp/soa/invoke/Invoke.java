package com.tangp.soa.invoke;

/**
 * RPC 调用协议 公共接口
 * 
 * @author tangpeng
 *
 */
public interface Invoke {
	String invoke(Invocation invocation) throws Exception;
}
