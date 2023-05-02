package com.example.modularmonoliths.common.json;

import java.util.Map;

public interface JsonMixins {

	Map<Class<?>, Class<?>> getMixins();

}
