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
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@HelidonTest
class PetTypeResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testAddPetType() {
        var petType = new PetTypeDto();
        petType.setName("human");

        var response = target
                .path("/petclinic/api/pettypes")
                .request()
                .post(Entity.entity(petType, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedPetType = response.readEntity(PetTypeDto.class);
        assertNotNull(returnedPetType.getId());

        var retrievedPetType = getPetType(returnedPetType.getId()).orElseThrow();
        assertThat(retrievedPetType.getName(), equalTo(petType.getName()));
    }

    @Test
    void testDeletePetType() {
        var petType = new PetTypeDto();
        petType.setName("to be deleted");

        var response = target
                .path("/petclinic/api/pettypes")
                .request()
                .post(Entity.entity(petType, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedPetType = response.readEntity(PetTypeDto.class);
        assertNotNull(returnedPetType.getId());

        var petTypeId = returnedPetType.getId();
        response = target
                .path("/petclinic/api/pettypes/" + petTypeId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getPetType(petTypeId).isEmpty());
    }

    @Test
    void testGetPetType() {
        var petType = getPetType(1).orElseThrow();
        assertThat(petType.getId(), is(1));
        assertThat(petType.getName(), equalTo("cat"));
    }

    @Test
    void testListPettypes() {
        var pettypes = target
                .path("/petclinic/api/pettypes")
                .request()
                .get(JsonArray.class);

        assertThat(pettypes.size(), greaterThan(0));
        assertThat(pettypes.getJsonObject(0).getInt("id"), is(1));
        assertThat(pettypes.getJsonObject(0).getString("name"), equalTo("cat"));
        assertThat(pettypes.getJsonObject(1).getInt("id"), is(2));
        assertThat(pettypes.getJsonObject(1).getString("name"), equalTo("dog"));
    }

    @Test
    void testUpdatePetType() {
        var petTypeId = 3;
        var petType = getPetType(petTypeId).orElseThrow();
        petType.setName("crocodile");

        var response = target
                .path("/petclinic/api/pettypes/" + petTypeId)
                .request()
                .put(Entity.entity(petType, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedPetType = getPetType(petTypeId).orElseThrow();
        assertThat(retrievedPetType.getName(), equalTo(petType.getName()));
    }

    private Optional<PetTypeDto> getPetType(int id) {
        var response = target
                .path("/petclinic/api/pettypes/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(PetTypeDto.class));
        }
    }
}