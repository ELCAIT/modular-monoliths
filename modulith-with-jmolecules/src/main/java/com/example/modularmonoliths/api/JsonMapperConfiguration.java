package com.example.modularmonoliths.api;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.example.modularmonoliths.common.json.JsonMixins;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.val;

@Configuration
class JsonMapperConfiguration {

    public static final ZoneId ZUERICH = ZoneId.of("Europe/Zurich");

    @Bean
    Jackson2ObjectMapperBuilder objectMapperBuilder(Module appModule) {
        val builder = new Jackson2ObjectMapperBuilder();
        builder.visibility(PropertyAccessor.FIELD, Visibility.ANY);
        builder.annotationIntrospector(new PojoBuilderAnnotationsIntrospector());
        builder.modules(new JavaTimeModule(), new Jdk8Module(), appModule);
        builder.timeZone(TimeZone.getTimeZone(ZUERICH));

        configureSerialization(builder);
        configureDeserialization(builder);

        return builder;
    }
    
	@Bean
	Module appModule(List<JsonMixins> mixins) {
		val annotations = mixins.stream()
				.map(JsonMixins::getMixins)
				.reduce(new HashMap<>(), (left, right) -> {
					left.putAll(right);
					return left;
				});
		return new AppModule(annotations);
	}
	    
    private void configureSerialization(Jackson2ObjectMapperBuilder builder) {
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.failOnEmptyBeans(false);
        builder.indentOutput(true);
        builder.serializationInclusion(Include.NON_NULL);
        builder.serializers(LocalDateSerializer.INSTANCE);
    }

    private void configureDeserialization(Jackson2ObjectMapperBuilder builder) {
        builder.failOnUnknownProperties(false);
        builder.featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        builder.featuresToEnable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

	/**
	 * Module to register Jackson annotations which we don't want to put on the
	 * domain entities to keep them free from technology. Each feature module of the
	 * application declares required Jackson mixins in a configuration located in the 
	 * web package of the feature.
	 */
	static class AppModule extends SimpleModule {
		private static final long serialVersionUID = 1L;
		
		public AppModule(Map<Class<?>, Class<?>> mixins) {
			mixins.entrySet().forEach(it -> setMixInAnnotation(it.getKey(), it.getValue()));
		}
	}

}
