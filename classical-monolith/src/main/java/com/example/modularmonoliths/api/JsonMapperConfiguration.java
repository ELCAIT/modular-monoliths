package com.example.modularmonoliths.api;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import lombok.val;

@Configuration
class JsonMapperConfiguration {

    public static final ZoneId ZUERICH = ZoneId.of("Europe/Zurich");

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        val builder = new Jackson2ObjectMapperBuilder();
        builder.visibility(PropertyAccessor.FIELD, Visibility.ANY);
        builder.annotationIntrospector(new PojoBuilderAnnotationsIntrospector());
        builder.modules(new JavaTimeModule(), new Jdk8Module());
        builder.timeZone(TimeZone.getTimeZone(ZUERICH));

        configureSerialization(builder);
        configureDeserialization(builder);

        return builder;
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
}
