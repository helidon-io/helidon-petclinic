/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.samples.petclinic.rest;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.samples.petclinic.mapper.OwnerMapper;
import io.helidon.samples.petclinic.mapper.PetMapper;
import io.helidon.samples.petclinic.mapper.VisitMapper;
import io.helidon.samples.petclinic.model.Owner;
import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.model.PetType;
import io.helidon.samples.petclinic.model.Visit;
import io.helidon.samples.petclinic.rest.dto.*;
import io.helidon.samples.petclinic.service.ClinicService;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@HelidonTest
@ExtendWith(MockitoExtension.class)
public class OwnerResourceTest {
    ClinicService clinicService;

    OwnerMapper ownerMapper;

    PetMapper petMapper;

    VisitMapper visitMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    OwnerResource ownerResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        ownerMapper = Mockito.mock(OwnerMapper.class);
        petMapper = Mockito.mock(PetMapper.class);
        visitMapper = Mockito.mock(VisitMapper.class);
        ownerResource = new OwnerResource(clinicService, ownerMapper, petMapper, visitMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddOwner() {
        var ownerFieldsDto = createOwnerFieldsDto();
        var owner = createOwner(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(ownerMapper.toOwner(ownerFieldsDto)).thenReturn(owner);

        var response = ownerResource.addOwner(ownerFieldsDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/owners/1"));
    }

    @Test
    void testAddPetToOwner() {
        var petFieldsDto = createPetFieldsDto();
        var owner = createOwner(1);
        var pet = createPet(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findOwnerById(1)).thenReturn(Optional.of(owner));
        Mockito.when(petMapper.toPet(petFieldsDto)).thenReturn(pet);

        var response = ownerResource.addPetToOwner(1, petFieldsDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/pets/1"));
    }

    @Test
    void testAddVisitToOwner() {
        var visitFieldsDto = createVisitFieldsDto();
        var owner = createOwner(1);
        var pet = createPet(1);
        pet.setOwner(owner);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(pet));
        Mockito.when(visitMapper.toVisit(visitFieldsDto)).thenReturn(createVisit(1));

        var response = ownerResource.addVisitToOwner(1, 1, visitFieldsDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/visits/1"));
    }

    @Test
    void testDeleteOwner() {
        Mockito.when(clinicService.findOwnerById(1)).thenReturn(Optional.of(createOwner(1)));
        var response = ownerResource.deleteOwner(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetOwner() {
        Mockito.when(ownerMapper.toOwnerDto(Mockito.any())).thenReturn(createOwnerDto(1));
        Mockito.when(clinicService.findOwnerById(1)).thenReturn(Optional.of(createOwner(1)));

        var response = ownerResource.getOwner(1);
        var owner = (OwnerDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(owner.getId(), is(1));
    }

    @Test
    void testGetOwnersPet() {
        var pet = createPet(1);
        pet.setOwner(createOwner(1));
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(pet));
        Mockito.when(petMapper.toPetDto(pet)).thenReturn(createPetDto(1));

        var response = ownerResource.getOwnersPet(1, 1);
        var petDto = (PetDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(petDto.getId(), is(1));
    }

    @Test
    void testListOwners() {
        var owners = new ArrayList<Owner>();
        owners.add(createOwner(1));
        owners.add(createOwner(2));

        var ownerDtos = new ArrayList<OwnerDto>();
        ownerDtos.add(createOwnerDto(1));
        ownerDtos.add(createOwnerDto(2));

        Mockito.when(clinicService.findAllOwners()).thenReturn(owners);
        Mockito.when(ownerMapper.toOwnerDtos(owners)).thenReturn(ownerDtos);

        var response = ownerResource.listOwners("Lastname");
        assertThat(response.getStatus(), is(200));

        var ownerDtoList = (List<OwnerDto>)response.getEntity();
        assertThat(ownerDtoList.size(), is(2));
    }

    @Test
    void testUpdateOwner() {
        var ownerFieldsDto = createOwnerFieldsDto();
        var owner = createOwner(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findOwnerById(1)).thenReturn(Optional.of(owner));
        Mockito.when(ownerMapper.toOwnerDto(owner)).thenReturn(createOwnerDto(1));

        var response = ownerResource.updateOwner(1, ownerFieldsDto);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/owners/1"));

        var returnedOwner = (OwnerDto)response.getEntity();
        assertThat(returnedOwner.getId(), is(1));
    }

    @Test
    void testUpdateOwnersPet() {
        var petFieldsDto = createPetFieldsDto();
        var pet = createPet(1);

        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(pet));

        var response = ownerResource.updateOwnersPet(1, 1, petFieldsDto);
        assertThat(response.getStatus(), is(204));
    }

    private OwnerFieldsDto createOwnerFieldsDto() {
        var ownerFiedlsDto = new OwnerFieldsDto();
        ownerFiedlsDto.setAddress("Address");
        ownerFiedlsDto.setCity("City");
        ownerFiedlsDto.setTelephone("Telephone");
        ownerFiedlsDto.setFirstName("John");
        ownerFiedlsDto.setLastName("Doe");

        return ownerFiedlsDto;
    }

    private OwnerDto createOwnerDto(int id) {
        var ownerDto = new OwnerDto(id, null);
        ownerDto.setAddress("Address");
        ownerDto.setCity("City");
        ownerDto.setTelephone("Telephone");
        ownerDto.setFirstName("John");
        ownerDto.setLastName("Doe");

        return ownerDto;
    }

    private Owner createOwner(int id) {
        var owner = new Owner();
        owner.setId(id);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        return owner;
    }

    private PetFieldsDto createPetFieldsDto() {
        var petType = new PetTypeDto(1);
        petType.setName("dog");

        var petFieldsDto = new PetFieldsDto();
        petFieldsDto.setBirthDate(LocalDate.now());
        petFieldsDto.setType(petType);
        petFieldsDto.setName("Falco");

        return petFieldsDto;
    }

    private PetDto createPetDto(int id) {
        var petType = new PetTypeDto(1);
        petType.setName("dog");

        var petDto = new PetDto(id, 1, null);
        petDto.setBirthDate(LocalDate.now());
        petDto.setType(petType);
        petDto.setName("Falco");

        return petDto;
    }

    private Pet createPet(int id) {
        var petType = new PetType();
        petType.setId(1);
        petType.setName("dog");

        var pet = new Pet();
        pet.setId(id);
        pet.setType(petType);
        pet.setName("Falco");

        var owner = createOwner(1);
        owner.addPet(pet);

        return pet;
    }

    private VisitFieldsDto createVisitFieldsDto() {
        var visitFieldsDto = new VisitFieldsDto();
        visitFieldsDto.setDate(LocalDate.now());
        visitFieldsDto.setDescription("description");
        return visitFieldsDto;
    }

    private Visit createVisit(int id) {
        var visit = new Visit();
        visit.setId(id);
        visit.setDate(LocalDate.now());
        visit.setDescription("description");
        return visit;
    }
}
