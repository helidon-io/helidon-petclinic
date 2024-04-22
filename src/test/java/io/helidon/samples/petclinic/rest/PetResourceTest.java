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
import io.helidon.samples.petclinic.mapper.PetMapper;
import io.helidon.samples.petclinic.model.Owner;
import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.model.PetType;
import io.helidon.samples.petclinic.rest.dto.PetDto;
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
import io.helidon.samples.petclinic.service.ClinicService;
import jakarta.inject.Inject;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@HelidonTest
@ExtendWith(MockitoExtension.class)
public class PetResourceTest {
    ClinicService clinicService;

    @Inject
    PetMapper petMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    PetResource petResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        petResource = new PetResource(clinicService, petMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPet() {
        var petDto = createPetDto();
        var owner = createOwner();

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findOwnerById(1)).thenReturn(Optional.of(owner));

        var response = petResource.addPet(petDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/pets/1"));

        var returnedPet = (PetDto)response.getEntity();
        assertThat(returnedPet.getId(), equalTo(petDto.getId()));
        assertThat(returnedPet.getName(), equalTo(petDto.getName()));
        assertThat(returnedPet.getOwnerId(), equalTo(petDto.getOwnerId()));
    }

    @Test
    void testDeletePet() {
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(createPet(1)));
        var response = petResource.deletePet(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetPet() {
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(createPet(1)));
        var response = petResource.getPet(1);
        var pet = (PetDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(pet.getId(), is(1));
        assertThat(pet.getName(), equalTo("Falco"));
    }

    @Test
    void testListPets() {
        var pets = new ArrayList<Pet>();
        pets.add(createPet(1));
        pets.add(createPet(2));

        Mockito.when(clinicService.findAllPets()).thenReturn(pets);

        var response = petResource.listPets();
        assertThat(response.getStatus(), is(200));

        var petDtoList = (List<PetDto>)response.getEntity();
        assertThat(petDtoList.size(), is(2));
        assertThat(petDtoList.get(0).getId(), is(1));
        assertThat(petDtoList.get(0).getName(), equalTo("Falco"));
        assertThat(petDtoList.get(1).getId(), is(2));
    }

    @Test
    void testUpdatePet() {
        var petDto = createPetDto();

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(createPet(1)));

        var response = petResource.updatePet(1, petDto);
        assertThat(response.getStatus(), is(200));

        var returnedPet = (PetDto)response.getEntity();
        assertThat(returnedPet.getId(), equalTo(petDto.getId()));
        assertThat(returnedPet.getName(), equalTo(petDto.getName()));
        assertThat(returnedPet.getOwnerId(), equalTo(petDto.getOwnerId()));
    }

    private PetDto createPetDto() {
        var petType = new PetTypeDto(1);
        petType.setName("dog");

        var petDto = new PetDto(1, 1, Collections.emptyList());
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

        var owner = createOwner();
        owner.addPet(pet);

        return pet;
    }

    private Owner createOwner() {
        var owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        return owner;
    }
}
