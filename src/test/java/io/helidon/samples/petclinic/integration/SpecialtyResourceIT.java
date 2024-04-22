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
import io.helidon.samples.petclinic.rest.dto.SpecialtyDto;
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
class SpecialtyResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testAddSpecialty() {
        var specialty = new SpecialtyDto();
        specialty.setName("specialty1");

        var response = target
                .path("/petclinic/api/specialties")
                .request()
                .post(Entity.entity(specialty, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedSpecialty = response.readEntity(SpecialtyDto.class);
        assertNotNull(returnedSpecialty.getId());

        var retrievedSpecialty = getSpecialty(returnedSpecialty.getId()).orElseThrow();
        assertThat(retrievedSpecialty.getName(), equalTo(specialty.getName()));
    }

    @Test
    void testDeleteSpecialty() {
        var specialty = new SpecialtyDto();
        specialty.setName("to be deleted");

        var response = target
                .path("/petclinic/api/specialties")
                .request()
                .post(Entity.entity(specialty, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedSpecialty = response.readEntity(SpecialtyDto.class);
        assertNotNull(returnedSpecialty.getId());

        var specialtyId = returnedSpecialty.getId();
        response = target
                .path("/petclinic/api/specialties/" + specialtyId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getSpecialty(specialtyId).isEmpty());
    }

    @Test
    void testGetSpecialty() {
        var specialty = getSpecialty(1).orElseThrow();
        assertThat(specialty.getId(), is(1));
        assertThat(specialty.getName(), equalTo("radiology"));
    }

    @Test
    void testListSpecialties() {
        var specialties = target
                .path("/petclinic/api/specialties")
                .request()
                .get(JsonArray.class);

        assertThat(specialties.size(), greaterThan(0));
        assertThat(specialties.getJsonObject(0).getInt("id"), is(1));
        assertThat(specialties.getJsonObject(0).getString("name"), equalTo("radiology"));
        assertThat(specialties.getJsonObject(1).getInt("id"), is(2));
        assertThat(specialties.getJsonObject(1).getString("name"), equalTo("surgery"));
    }

    @Test
    void testUpdateSpecialty() {
        var specialtyId = 3;
        var specialty = getSpecialty(specialtyId).orElseThrow();
        specialty.setName("unknown specialty");

        var response = target
                .path("/petclinic/api/specialties/" + specialtyId)
                .request()
                .put(Entity.entity(specialty, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedSpecialty = getSpecialty(specialtyId).orElseThrow();
        assertThat(retrievedSpecialty.getName(), equalTo(specialty.getName()));
    }

    private Optional<SpecialtyDto> getSpecialty(int id) {
        var response = target
                .path("/petclinic/api/specialties/" + id)
                .request()
                .get();

        if (response.getStatus() == 404) {
            return Optional.empty();
        } else {
            return Optional.of(response.readEntity(SpecialtyDto.class));
        }
    }
}