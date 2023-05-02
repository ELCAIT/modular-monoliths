package com.example.modularmonoliths;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularMonolithArchitectureTests {

	ApplicationModules modules = ApplicationModules.of(ModulithWithSpringModulithApplication.class);

	@Test
	void verifyModularity() {
		modules.verify();
	}

	@Test
	void writeDocumentationSnippets() {

		new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
	}
}
