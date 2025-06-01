package com.neotee.ecommercesystem.solution.client.application.mapper;

import com.neotee.ecommercesystem.domainprimitives.Email;
import com.neotee.ecommercesystem.domainprimitives.HomeAddress;
import com.neotee.ecommercesystem.domainprimitives.ZipCode;
import com.neotee.ecommercesystem.solution.client.application.dto.ClientDTO;
import com.neotee.ecommercesystem.solution.client.domain.Client;
import com.neotee.ecommercesystem.solution.client.domain.ClientId;
import com.neotee.ecommercesystem.usecases.domainprimitivetypes.ZipCodeType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ClientMapper {
    @Mapping(target = "clientId", source = "id", qualifiedByName = "mapUUIDToClientId")
    @Mapping(target = "homeAddress", source = ".", qualifiedByName = "mapCityAndZipCodeAndStreetToAddress")
    @Mapping(target = "email", source = "emailString", qualifiedByName = "mapStringToEmail")
    public abstract Client toEntity(ClientDTO clientDTO);

    @Mapping(target = "id", source = "clientId", qualifiedByName = "mapClientIdToUUID")
    @Mapping(target = "city", source = "homeAddress", qualifiedByName = "mapHomeAddressToCity")
    @Mapping(target = "street", source = "homeAddress", qualifiedByName = "mapHomeAddressToStreet")
    @Mapping(target = "zipCodeString", source = "homeAddress", qualifiedByName = "mapHomeAddressToZipCode")
    @Mapping(target = "emailString", source = "email", qualifiedByName = "mapEmailToString")
    public abstract ClientDTO toDto(Client client);


    @Named("mapUUIDToClientId")
    public ClientId mapUUIDToClientId(UUID id) {
        return new ClientId(id);
    }

    @Named("mapClientIdToUUID")
    public UUID mapClientIdToUUID(ClientId clientId) {
        return clientId.getId();
    }


    @Named("mapCityAndZipCodeAndStreetToAddress")
    public HomeAddress mapCityAndZipCodeAndStreetToAddress(ClientDTO clientDTO) {
        ZipCodeType zipCodeObj = ZipCode.of(clientDTO.getZipCodeString());
        return (HomeAddress) HomeAddress.of(clientDTO.getStreet(), clientDTO.getCity(), zipCodeObj);
    }


    @Named("mapHomeAddressToCity")
    public String mapHomeAddressToCity(HomeAddress homeAddress) {
        return homeAddress.getCity();
    }

    @Named("mapHomeAddressToStreet")
    public String mapHomeAddressToStreet(HomeAddress homeAddress) {
        return homeAddress.getStreet();
    }

    @Named("mapHomeAddressToZipCode")
    public String mapHomeAddressToZipCode(HomeAddress homeAddress) {
        return homeAddress.getZipCode().getZipCode();
    }

    @Named("mapEmailToString")
    public String mapEmailToString(Email email) {
        return email.getEmailAddress();
    }

    @Named("mapStringToEmail")
    public Email mapStringToEmail(String email) {
        return Email.of(email);
    }

}
