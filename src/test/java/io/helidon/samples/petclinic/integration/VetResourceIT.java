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
import io.helidon.samples.petclinic.rest.VetResource;
import io.helidon.samples.petclinic.rest.dto.VetDto;
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

/**
 * Test class for {@link VetResource}
 */
@HelidonTest
class VetResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testGetVet() {
        var vet = getVet(1).orElseThrow();
        assertThat(vet.getId(), is(1));
        assertThat(vet.getFirstName(), equalTo("James"));
        assertThat(vet.getLastName(), equalTo("Carter"));
        assertTrue(vet.getSpecialties().isEmpty());
    }

    @Test
    void testGetAllVets() {
        var vets = target
                .path("/petclinic/api/vets")
                .request()
                .get(JsonArray.class);

        assertThat(vets.size(), greaterThan(0));
        assertThat(vets.getJsonObject(0).getInt("id"), is(1));
        assertThat(vets.getJsonObject(0).getString("firstName"), equalTo("James"));
        assertThat(vets.getJsonObject(1).getInt("id"), is(2));
        assertThat(vets.getJsonObject(1).getString("firstName"), equalTo("Helen"));
    }

    @Test
    void testCreateVet() {
        var vet = new VetDto();
        vet.setFirstName("John");
        vet.setLastName("Doe");

        var response = target
                .path("/petclinic/api/vets")
                .request()
                .post(Entity.entity(vet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedVet = response.readEntity(VetDto.class);
        assertNotNull(returnedVet.getId());

        var retrievedVet = getVet(returnedVet.getId()).orElseThrow();
        assertThat(retrievedVet.getFirstName(), equalTo(vet.getFirstName()));
        assertThat(retrievedVet.getLastName(), equalTo(vet.getLastName()));
    }

    @Test
    void testUpdateVet() {
        var vetId = 3;
        var vet = getVet(vetId).orElseThrow();
        vet.setFirstName("John");
        vet.setLastName("Doe");

        var response = target
                .path("/petclinic/api/vets/" + vetId)
                .request()
                .put(Entity.entity(vet, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedVet = getVet(vetId).orElseThrow();
        assertThat(retrievedVet.getFirstName(), equalTo(vet.getFirstName()));
        assertThat(retrievedVet.getLastName(), equalTo(vet.getLastName()));
    }

    @Test
    void testDeleteVet() {
        var vetId = 4;
        var response = target
                .path("/petclinic/api/vets/" + vetId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getVet(vetId).isEmpty());
    }

    private Optional<VetDto> getVet(int id) {
        var response = target
                .path("/petclinic/api/vets/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(VetDto.class));
        }
    }
}
