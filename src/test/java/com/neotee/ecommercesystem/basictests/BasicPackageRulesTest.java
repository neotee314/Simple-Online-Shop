package com.neotee.ecommercesystem.basictests;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;


import jakarta.persistence.*;
import java.util.UUID;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

@ArchTag("layerRules")
@AnalyzeClasses(packages = "com.neotee.ecommercesystem.shopsystem")
@SuppressWarnings("PMD")
public class BasicPackageRulesTest {

    @ArchTest
    static final ArchRule noClassesOnTopLevel =
            classes().should().resideInAPackage( "..shopsystem.*.." );

    @ArchTest
    static final ArchRule noNonIdFieldsOfTypeIUUID =
            fields()
                    .that().areDeclaredInClassesThat().areAnnotatedWith( Entity.class )
                    .and().areNotAnnotatedWith( Id.class )
                    .and().areDeclaredInClassesThat().doNotHaveSimpleName( "Thing" )
                    .should().notHaveRawType( UUID.class ).allowEmptyShould( true );

    @ArchTest
    static final ArchRule noNonIdFieldsOfTypeIUUIDInEmbeddables =
            fields()
                    .that().areDeclaredInClassesThat().areAnnotatedWith( Embeddable.class )
                    .should().notHaveRawType( UUID.class ).allowEmptyShould( true );
}
