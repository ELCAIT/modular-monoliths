package com.example.modularmonoliths;

import org.jmolecules.archunit.JMoleculesArchitectureRules;
import org.jmolecules.archunit.JMoleculesDddRules;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.example.modularmonoliths")
class ArchitectureTests {

	@ArchTest
	static ArchRule dddRules = JMoleculesDddRules.all();

	@ArchTest
	static ArchRule onion = JMoleculesArchitectureRules.ensureOnionSimple();

}
