package com.example.modularmonoliths.common.type;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.val;

class QuantityTests {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    void serialize() throws JsonProcessingException {
        val quantity = Quantity.of(3);

        // act && assert
        assertThat(jsonMapper.writeValueAsString(quantity)).isEqualTo("3");
    }

    @Test
    void deserialize() throws JsonProcessingException {
        // act && assert
        assertThat(jsonMapper.readValue("3", Quantity.class)).isEqualTo(Quantity.of(3));
    }

}