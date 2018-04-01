package com.tangp.soa.netty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tangp.soa.configBean.Service;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyServerInHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelActive(ctx);
	}

	/**
	 * netty客户端有消息过来的时候回调用到该方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 从buf中获取客户端的请求消息
		ByteBuf result = (ByteBuf) msg;
		byte[] result1 = new byte[result.readableBytes()];
		result.readBytes(result1);

		String resultStr = new String(result1);
		System.out.println(resultStr);

		result.release();

		String response = invokeService(resultStr);

		// 发送响应消息到buf中
		ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
		encoded.writeBytes(response.getBytes());
		ctx.writeAndFlush(encoded);
		ctx.close();
	}

	private String invokeService(String param) {
		JSONObject requestparam = JSONObject.parseObject(param);
		// 要从远程的生产者的spring容器中拿到对应的serviceid实例
		String serviceId = requestparam.getString("serviceId");
		String methodName = requestparam.getString("methodName");
		JSONArray paramTypes = requestparam.getJSONArray("paramTypes");
		// 这个对应的方法参数
		JSONArray methodParamJa = requestparam.getJSONArray("methodParams");
		// 这个就是反射的参数
		Object[] objs = null;
		if (methodParamJa != null) {
			objs = new Object[methodParamJa.size()];
			int i = 0;
			for (Object o : methodParamJa) {
				objs[i++] = o;
			}
		}

		// 拿到spring的上下文
		ApplicationContext application = Service.getApplicationContext();
		// 服务层的实例
		Object serviceBean = application.getBean(serviceId);

		// 这个方法的获取，要考虑到这个方法的重载
		Method method = getMethod(serviceBean, methodName, paramTypes);

		if (method != null) {

			Object result;
			try {
				result = method.invoke(serviceBean, objs);
				return result.toString();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			return "---------------------------------nosuchmethod-----------------------------";
		}
		return null;
	}

	public static Method getMethod(Object bean, String methodName, JSONArray paramsTypes) {
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
