package com.example.modularmonoliths.common.type;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class Principal {
	
	@NonNull
	String stringValue;
	
}
