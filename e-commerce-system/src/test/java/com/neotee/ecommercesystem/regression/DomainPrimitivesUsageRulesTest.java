package com.neotee.ecommercesystem.regression;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTag;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@ArchTag("domainPrimitivesUsageRules")
@AnalyzeClasses(packages = "com.neotee.ecommercesystem.shopsystem")
@SuppressWarnings("PMD")
public class DomainPrimitivesUsageRulesTest {
    private static final String EMAIL_CLASS = "com.neotee.ecommercesystem.domainprimitives.Email";
    private static final String MONEY_CLASS = "com.neotee.ecommercesystem.domainprimitives.Money";
    private static final String HomeAddress_CLASS = "com.neotee.ecommercesystem.domainprimitives.HomeAddress";

    @ArchTest
    static final ArchRule clientShouldReferenceEmail =
            classes()
                    .that().haveSimpleName( "Client" )
                    .should().dependOnClassesThat().haveFullyQualifiedName( EMAIL_CLASS )
                    .because( "Client should use Email instead of String" );

    @ArchTest
    static final ArchRule clientShouldReferenceHomeAddress =
            classes()
                    .that().haveSimpleName( "Client" )
                    .should().dependOnClassesThat().haveFullyQualifiedName( HomeAddress_CLASS )
                    .because( "Client should use HomeAddress instead of whatever else" );

    @ArchTest
    static final ArchRule storageUnitShouldReferenceEmail =
            classes()
                    .that().haveSimpleName( "StorageUnit" )
                    .should().dependOnClassesThat().haveFullyQualifiedName( HomeAddress_CLASS )
                    .because( "StorageUnit should use HomeAddress instead of whatever else" );

    @ArchTest
    static final ArchRule thingShouldReferenceMoney =
            classes()
                    .that().haveSimpleName( "Thing" )
                    .should().dependOnClassesThat().haveFullyQualifiedName( MONEY_CLASS )
                    .because( "Thing should use Money instead of float or whatever else" );
}
