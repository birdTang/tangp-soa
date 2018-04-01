package com.tangp.soa.registry;

import java.util.List;

import org.springframework.context.ApplicationContext;

public interface BaseRegistry {
	void registry(String param, ApplicationContext application);

	List<String> getRegistry(String id, ApplicationContext application);
}
