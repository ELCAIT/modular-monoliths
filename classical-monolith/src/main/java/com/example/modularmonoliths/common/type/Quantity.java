package com.example.modularmonoliths.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class Quantity {

    @NonNull
    @JsonValue
    Integer intValue;

    @JsonCreator(mode = Mode.DELEGATING)
    public Quantity(int intValue) {
        this.intValue = intValue;
    }

}
