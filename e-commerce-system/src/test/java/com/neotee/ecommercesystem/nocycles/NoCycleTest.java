package com.neotee.ecommercesystem.nocycles;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class NoCycleTest {
    private JavaClasses importedClasses = new ClassFileImporter().importPackages("com.neotee.ecommercesystem.shopsystem");

    private SliceAssignment inAllClasses() {
        return new SliceAssignment() {
            @Override
            public String getDescription() {
                return "every class in it's own slice";
            }

            @Override
            public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
                return SliceIdentifier.of("In-All-Classes", javaClass.getPackageName(), javaClass.getName());
            }
        };
    }

    @Test
    public void testNoCyclicDependenciesBetweenClasses() {
        slices().assignedFrom(inAllClasses())
                .namingSlices("$1[$2.$3]")
                .should().beFreeOfCycles()
                .check(importedClasses);
    }

    @Test
    public void testNoCyclicDependenciesBetweenPackages() {
        ArchRule rule = slices().matching("..shopsystem.(*)..").should().beFreeOfCycles();
        rule.check(importedClasses);
    }
}
