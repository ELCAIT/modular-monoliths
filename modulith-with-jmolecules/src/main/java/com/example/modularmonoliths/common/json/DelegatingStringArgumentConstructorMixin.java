package com.example.modularmonoliths.common.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

public class DelegatingStringArgumentConstructorMixin {
	
	@JsonCreator(mode = Mode.DELEGATING)
	public DelegatingStringArgumentConstructorMixin(String stringValue) {}
}