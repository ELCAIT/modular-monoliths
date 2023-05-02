package com.example.modularmonoliths;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.val;

@AnalyzeClasses(packagesOf = ArchitectureTests.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTests {
	
	static final JavaClasses classes = new ClassFileImporter().importPackages("com.example.modularmonoliths");
	
	@ArchTest
	public static final ArchRule commonRule = noClasses()
		    .that().resideInAPackage("..common..")
		    .should().dependOnClassesThat().resideInAnyPackage(
		    		"..masterdata..", "..productinventory..", "..productionorder..", "..api..");

	@ArchTest
	public static final ArchRule productInventoryRule = classes()
		    .that().resideInAPackage("..productinventory..")
		    .should().onlyBeAccessed().byAnyPackage("..productinventory..");

	@ArchTest
	public static final ArchRule productionOrderRule = classes()
		    .that().resideInAPackage("..productionorder..")
		    .should().onlyBeAccessed().byAnyPackage("..productionorder..", "..productinventory..");

	@ArchTest
	public static final ArchRule webRule = classes()
		    .that().resideInAPackage("..web..")
		    .should().onlyBeAccessed().byAnyPackage("..web..");

	@BeforeAll
	public void setUp() {
	    val logger = (Logger)LoggerFactory.getLogger("com.tngtech.archunit");
	    logger.setLevel(Level.INFO);
	}

}
