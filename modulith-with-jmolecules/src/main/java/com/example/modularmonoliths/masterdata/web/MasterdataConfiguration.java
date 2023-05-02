package com.example.modularmonoliths.masterdata.web;

import java.util.Map;

import org.springframework.context.annotation.Configuration;

import com.example.modularmonoliths.common.json.DelegatingStringArgumentConstructorMixin;
import com.example.modularmonoliths.common.json.JsonMixins;
import com.example.modularmonoliths.masterdata.Product.ProductIdentifier;

@Configuration(proxyBeanMethods = false)
class MasterdataConfiguration implements JsonMixins {

	@Override
	public Map<Class<?>, Class<?>> getMixins() {

		return Map.of(
				ProductIdentifier.class, DelegatingStringArgumentConstructorMixin.class
		);
	}

}