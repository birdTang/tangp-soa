package com.tangp.soa.rmi;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tangp.soa.configBean.Service;

/**
 * rmi实现类，负责处理rmi的调用，生产者端使用的类，与http协议的DispatcherServlet的功能一致
 * 
 * @author tangpeng
 *
 */
public class SoaRmiImpl extends UnicastRemoteObject implements SoaRmi {

	/**
	 * 必须要有无参构造函数
	 * 
	 * @throws RemoteException
	 */
	protected SoaRmiImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 66372107769780698L;

	@Override
	public String invoke(String params) throws RemoteException {

		try {
			JSONObject requestparam = JSONObject.parseObject(params);
			// 从生产者的spring容器中拿到对应serviceId实例
			String serviceId = requestparam.getString("serviceId");
			// 方法名
			String methodName = requestparam.getString("methodName");
			// 参数类型
			JSONArray methodTypes = requestparam.getJSONArray("paramTypes");
			// 方法的参数
			JSONArray methodParams = requestparam.getJSONArray("methodParams");

			// 将客户端传的方法参数封装为数组
			Object[] objs = null;
			if (methodParams != null) {
				objs = new Object[methodParams.size()];
				int i = 0;
				for (Object o : methodParams) {
					objs[i++] = 0;
				}
			}

			// 拿到生产者的spring上下文
			ApplicationContext application = Service.getApplicationContext();
			// 从spring上下中获取实例
			Object serviceBean = application.getBean(serviceId);

			// 方法的获取，要考虑方法重载的情况
			Method method = getMehtod(serviceBean, methodName, methodTypes);
			Object result;
			if (method != null) {
				result = method.invoke(serviceBean, objs);
				return result.toString();
			} else {
				return "该方法不存在：" + serviceId + "." + methodName;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	public static Method getMehtod(Object bean, String methodName, JSONArray paramsTypes) {
		Method[] methods = bean.getClass().getMethods();
		List<Method> retMethod = new ArrayList<>();
		for (Method method : methods) {
			// 把方法名和methodName相同的放入list容器中
			if (methodName.trim().equals(method.getName())) {
				retMethod.add(method);
			}
		}
		// 如果只有一个同名方法，直接返回
		if (retMethod.size() == 1) {
			return retMethod.get(0);
		}

		// 参数个数是否相同
		boolean isSameSize = false;
		// 参数类型是否相同
		boolean isSameType = false;
		tangp: for (Method method : retMethod) {
			Class<?>[] types = method.getParameterTypes();
			if (types.length == paramsTypes.size()) {
				isSameSize = true;
			}
			if (!isSameSize) {
				continue;
			}

			for (int i = 0; i < types.length; i++) {
				if (types[i].toString().contains(paramsTypes.getString(i))) {
					isSameType = true;
				} else {
					isSameType = false;
				}

				if (!isSameType) {
					continue tangp;
				}
			}
			if (isSameType) {
				return method;
			}
		}
		return null;
	}

}
