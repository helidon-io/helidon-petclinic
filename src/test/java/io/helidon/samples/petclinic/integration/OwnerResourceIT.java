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
package io.helidon.samples.petclinic.integration;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.samples.petclinic.rest.dto.*;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@HelidonTest
class OwnerResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testAddOwner() {
        var ownerDto = createOwnerDto();
        var response = target
                .path("/petclinic/api/owners")
                .request()
                .post(Entity.entity(ownerDto, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedOwner = response.readEntity(OwnerDto.class);
        assertNotNull(returnedOwner.getId());

        var retrievedOwner = getOwner(returnedOwner.getId()).orElseThrow();
        assertThat(retrievedOwner.getFirstName(), equalTo(ownerDto.getFirstName()));
        assertThat(retrievedOwner.getLastName(), equalTo(ownerDto.getLastName()));
        assertThat(retrievedOwner.getAddress(), equalTo(ownerDto.getAddress()));
        assertThat(retrievedOwner.getCity(), equalTo(ownerDto.getCity()));
        assertThat(retrievedOwner.getTelephone(), equalTo(ownerDto.getTelephone()));
        assertTrue(returnedOwner.getPets().isEmpty());
    }

    @Test
    void testDeleteOwner() {
        var ownerDto = createOwnerDto();
        var response = target
                .path("/petclinic/api/owners")
                .request()
                .post(Entity.entity(ownerDto, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedOwner = response.readEntity(OwnerDto.class);
        assertNotNull(returnedOwner.getId());

        var ownerId = returnedOwner.getId();
        response = target
                .path("/petclinic/api/owners/" + ownerId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getOwner(ownerId).isEmpty());
    }

    @Test
    void testGetOwner() {
        var owner = getOwner(1).orElseThrow();
        assertThat(owner.getId(), is(1));
        assertThat(owner.getPets().size(), is(1));
        assertThat(owner.getCity(), equalTo("Madison"));
        assertThat(owner.getFirstName(), equalTo("George"));
        assertThat(owner.getLastName(), equalTo("Franklin"));
    }

    @Test
    void testListOwners() {
        var owners = target
                .path("/petclinic/api/owners")
                .request()
                .get(JsonArray.class);

        assertThat(owners.size(), greaterThan(0));
        assertThat(owners.getJsonObject(0).getInt("id"), is(1));
        assertThat(owners.getJsonObject(0).getString("firstName"), equalTo("George"));
        assertThat(owners.getJsonObject(0).getString("lastName"), equalTo("Franklin"));
        assertThat(owners.getJsonObject(1).getInt("id"), is(2));
        assertThat(owners.getJsonObject(1).getString("firstName"), equalTo("Betty"));
        assertThat(owners.getJsonObject(1).getString("lastName"), equalTo("Davis"));
    }

    @Test
    void testUpdateOwner() {
        var ownerId = 3;
        var owner = getOwner(ownerId).orElseThrow();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("Mladoboleslavska 33");
        owner.setCity("Praha");
        owner.setTelephone("604223322");

        var response = target
                .path("/petclinic/api/owners/" + ownerId)
                .request()
                .put(Entity.entity(owner, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedOwner = getOwner(ownerId).orElseThrow();
        assertThat(retrievedOwner.getFirstName(), equalTo(owner.getFirstName()));
        assertThat(retrievedOwner.getLastName(), equalTo(owner.getLastName()));
        assertThat(retrievedOwner.getAddress(), equalTo(owner.getAddress()));
        assertThat(retrievedOwner.getCity(), equalTo(owner.getCity()));
        assertThat(retrievedOwner.getTelephone(), equalTo(owner.getTelephone()));
    }

    @Test
    void testAddPetToOwner() {
        var petType = new PetTypeDto(2);
        petType.setName("dog");

        var pet = new PetDto(null, 1, Collections.emptyList());
        pet.setBirthDate(LocalDate.now());
        pet.setType(petType);
        pet.setName("Arnold");

        var response = target
                .path("/petclinic/api/owners/1/pets")
                .request()
                .post(Entity.entity(pet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedPet = response.readEntity(PetDto.class);
        assertNotNull(returnedPet.getId());

        var retrievedPet = getPet(returnedPet.getId()).orElseThrow();
        assertThat(retrievedPet.getName(), equalTo(pet.getName()));
        assertThat(retrievedPet.getBirthDate(), equalTo(pet.getBirthDate()));
        assertThat(retrievedPet.getType().getId(), equalTo(petType.getId()));
        assertThat(retrievedPet.getOwnerId(), is(1));
    }

    @Test
    void testUpdateOwnersPet() {
        var petId = 5;
        var pet = getPet(petId).orElseThrow();
        pet.setName("Arnold");
        pet.setBirthDate(LocalDate.now());

        var response = target
                .path("/petclinic/api/owners/4/pets/" + petId)
                .request()
                .put(Entity.entity(pet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(204));

        var retrievedPet = getPet(petId).orElseThrow();
        assertThat(retrievedPet.getName(), equalTo(pet.getName()));
        assertThat(retrievedPet.getBirthDate(), equalTo(pet.getBirthDate()));

    }

    @Test
    public void testAddVisitToOwner() {
        var visitFieldsDto = new VisitFieldsDto();
        visitFieldsDto.setDate(LocalDate.now());
        visitFieldsDto.setDescription("emasculation");

        var response = target
                .path("/petclinic/api/owners/1/pets/1/visits")
                .request()
                .post(Entity.entity(visitFieldsDto, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedVisit = response.readEntity(VisitDto.class);
        assertNotNull(returnedVisit.getId());

        var retrievedVisit = getVisit(returnedVisit.getId()).orElseThrow();
        assertThat(retrievedVisit.getDescription(), equalTo(visitFieldsDto.getDescription()));
        assertThat(retrievedVisit.getDate(), equalTo(visitFieldsDto.getDate()));
        assertThat(retrievedVisit.getPetId(), is(1));
    }

    private OwnerDto createOwnerDto() {
        var ownerDto = new OwnerDto();
        ownerDto.setFirstName("John");
        ownerDto.setLastName("Doe");
        ownerDto.setAddress("Mladoboleslavska 33");
        ownerDto.setCity("Praha");
        ownerDto.setTelephone("604223322");
        return ownerDto;
    }

    private Optional<OwnerDto> getOwner(int id) {
        var response = target
                .path("/petclinic/api/owners/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(OwnerDto.class));
        }
    }

    private Optional<PetDto> getPet(int id) {
        var response = target
                .path("/petclinic/api/pets/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(PetDto.class));
        }
    }

    private Optional<VisitDto> getVisit(int id) {
        var response = target
                .path("/petclinic/api/visits/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(VisitDto.class));
        }
    }
}