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
import io.helidon.samples.petclinic.rest.dto.PetDto;
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
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
class PetResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testAddPet() {
        var petType = new PetTypeDto(2);
        petType.setName("dog");

        var pet = new PetDto(null, 1, Collections.emptyList());
        pet.setBirthDate(LocalDate.now());
        pet.setType(petType);
        pet.setName("Arnold");

        var response = target
                .path("/petclinic/api/pets")
                .request()
                .post(Entity.entity(pet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedPet = response.readEntity(PetDto.class);
        assertNotNull(returnedPet.getId());

        var retrievedPet = getPet(returnedPet.getId()).orElseThrow();
        assertThat(retrievedPet.getName(), equalTo(pet.getName()));
        assertThat(retrievedPet.getBirthDate(), equalTo(pet.getBirthDate()));
        assertThat(retrievedPet.getType().getId(), equalTo(petType.getId()));
        assertThat(retrievedPet.getOwnerId(), equalTo(pet.getOwnerId()));
    }

    @Test
    void testDeletePet() {
        var petType = new PetTypeDto(2);
        petType.setName("dog");

        var pet = new PetDto(null, 1, Collections.emptyList());
        pet.setBirthDate(LocalDate.now());
        pet.setType(petType);
        pet.setName("Arnold");

        var response = target
                .path("/petclinic/api/pets")
                .request()
                .post(Entity.entity(pet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedPet = response.readEntity(PetDto.class);
        assertNotNull(returnedPet);
        var petId = returnedPet.getId();
        assertNotNull(petId);

        response = target
                .path("/petclinic/api/pets/" + petId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getPet(petId).isEmpty());
    }

    @Test
    void testGetPet() {
        var pet = getPet(1).orElseThrow();
        assertThat(pet.getId(), is(1));
        assertThat(pet.getName(), equalTo("Jacka"));
    }

    @Test
    void testListPets() {
        var pets = target
                .path("/petclinic/api/pets")
                .request()
                .get(JsonArray.class);

        assertThat(pets.size(), greaterThan(0));
        assertThat(pets.getJsonObject(0).getInt("id"), is(1));
        assertThat(pets.getJsonObject(0).getString("name"), equalTo("Jacka"));
    }

    @Test
    void testUpdatePet() {
        var petId = 5;
        var pet = getPet(petId).orElseThrow();
        pet.setName("Arnold");
        pet.setBirthDate(LocalDate.now());

        var response = target
                .path("/petclinic/api/pets/" + petId)
                .request()
                .put(Entity.entity(pet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedPet = getPet(petId).orElseThrow();
        assertThat(retrievedPet.getName(), equalTo(pet.getName()));
        assertThat(retrievedPet.getBirthDate(), equalTo(pet.getBirthDate()));
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
}