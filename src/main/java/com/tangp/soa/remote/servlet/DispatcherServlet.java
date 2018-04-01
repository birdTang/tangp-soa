package com.tangp.soa.remote.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tangp.soa.configBean.Service;

/**
 * 这是该框架中给服务生产者接收请求用的Servlet, 必须采用http协议的才调的到
 * 
 * 生产者要用该servlet 需要在web.xml中配置该servlet---这也是使用http协议不好的地方。
 * 
 * @author tangpeng
 *
 */
public class DispatcherServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4056391010892398847L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONObject requestparam = httpProcess(req, resp);
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
			if (method != null) {
				Object result;
				result = method.invoke(serviceBean, objs);

				PrintWriter writer = resp.getWriter();
				writer.write(result.toString());
			} else {
				PrintWriter pw = resp.getWriter();
				pw.write("该方法不存在：" + serviceId + "." + methodName);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
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

	/**
	 * 封装请求参数
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public static JSONObject httpProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStream is = req.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is, "uft-8"));

		String s = "";
		while ((s = br.readLine()) != null) {
			sb.append(s);
		}
		if (sb.toString().length() <= 0) {
			return null;
		} else {
			return JSONObject.parseObject(sb.toString());
		}

	}

}
