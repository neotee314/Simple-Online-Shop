package com.neotee.ecommercesystem.basictests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@ArchTag("layerRules")
@AnalyzeClasses(packages = "com.neotee.ecommercesystem")
@SuppressWarnings("PMD")
public class BasicNoOutsideSolutionRulesTest {

    @ArchTest
    static final ArchRule noClassesOutsideSolutionAndDomainprimitives =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Domainprimitives").definedBy("com.neotee.ecommercesystem.domainprimitives..")
                    .layer("ShopSystemClasses").definedBy("com.neotee.ecommercesystem.shopsystem..")
                    .layer("Tests").definedBy("com.neotee.ecommercesystem.basictests..")

                    .whereLayer("Domainprimitives").mayOnlyBeAccessedByLayers("ShopSystemClasses", "Tests")
                    .whereLayer("ShopSystemClasses").mayOnlyBeAccessedByLayers("Tests");
}
