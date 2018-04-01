package com.tangp.soa.test.impl;

import com.tangp.soa.test.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public String eat(String param) {
		System.out.println("吃货方法:" + param);
		return "已经吃了：" + param;
	}

}
