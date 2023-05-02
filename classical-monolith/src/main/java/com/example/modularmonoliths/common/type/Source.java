package com.example.modularmonoliths.common.type;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

@Value(staticConstructor = "of")
@Accessors(fluent = true)
public class Source {
	
	public static final Source UNKNOWN = Source.of("n/a");

	@NonNull
	String stringValue;
	
}