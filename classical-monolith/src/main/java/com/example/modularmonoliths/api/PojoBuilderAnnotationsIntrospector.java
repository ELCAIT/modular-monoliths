package com.example.modularmonoliths.api;

import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import lombok.val;

final class PojoBuilderAnnotationsIntrospector extends JacksonAnnotationIntrospector {

    private static final long serialVersionUID = -478092554174530754L;

    @Override
    public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
        return useEmptyPrefix(ac);
    }

    @Override
    public Class<?> findPOJOBuilder(AnnotatedClass ac) {
        return useBuilderIfExists(ac);
    }

    /**
     * Use empty string for Builder method calls. So we can call builder().value() instead of builder().withValue()
     */
    private JsonPOJOBuilder.Value useEmptyPrefix(AnnotatedClass ac) {
        if (ac.hasAnnotation(JsonPOJOBuilder.class)) {
            return super.findPOJOBuilderConfig(ac);
        }
        // If no annotation present use default as empty prefix
        return new JsonPOJOBuilder.Value("build", "");
    }

    /**
     * Use the class from the builder() Method as builder. So we don't have to write JsonDeserialize annotation.
     */
    private Class<?> useBuilderIfExists(AnnotatedClass ac) {
        if (!ac.hasAnnotation(JsonPOJOBuilder.class)) {// If no annotation present use class from builder() method
            try {
                val methodToFind = ac.getAnnotated().getMethod("builder", (Class<?>[]) null);
                return methodToFind.invoke(null).getClass();
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                     | InvocationTargetException e) {
                // no builder method found, use default
            }
        }
        return super.findPOJOBuilder(ac);
    }
}
