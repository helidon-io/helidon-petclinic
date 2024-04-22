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
import io.helidon.samples.petclinic.rest.VisitResource;
import io.helidon.samples.petclinic.rest.dto.VisitDto;
import jakarta.inject.Inject;
import jakarta.json.JsonArray;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link VisitResource}
 */
@HelidonTest
class VisitResourceIT {
    @Inject
    private WebTarget target;

    @Test
    void testGetVisit() {
        var visit = getVisit(1).orElseThrow();
        assertThat(visit.getId(), is(1));
        assertThat(visit.getDescription(), equalTo("rabies shot"));
    }

    @Test
    void testGetAllVisits() {
        var vets = target
                .path("/petclinic/api/visits")
                .request()
                .get(JsonArray.class);

        assertThat(vets.size(), greaterThan(0));
        assertThat(vets.getJsonObject(0).getInt("id"), is(1));
        assertThat(vets.getJsonObject(0).getString("description"), equalTo("rabies shot"));
    }

    @Test
    void testCreateVisit() {
        var visitDto = new VisitDto(null, 1);
        visitDto.setDate(LocalDate.now());
        visitDto.setDescription("rabies shot");

        var response = target
                .path("/petclinic/api/visits")
                .request()
                .post(Entity.entity(visitDto, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(201));

        var returnedVisit = response.readEntity(VisitDto.class);
        assertNotNull(returnedVisit.getId());

        var retrievedVisit = getVisit(returnedVisit.getId()).orElseThrow();
        assertThat(retrievedVisit.getDescription(), equalTo(visitDto.getDescription()));
        assertThat(retrievedVisit.getDate(), equalTo(visitDto.getDate()));
        assertThat(retrievedVisit.getPetId(), equalTo(visitDto.getPetId()));
    }

    @Test
    void testUpdateVisit() {
        var visitId = 3;
        var visit = getVisit(visitId).orElseThrow();
        visit.setDescription("Updated Description");
        visit.setDate(LocalDate.now());

        var response = target
                .path("/petclinic/api/visits/" + visitId)
                .request()
                .put(Entity.entity(visit, MediaType.APPLICATION_JSON));
        assertThat(response.getStatus(), is(200));

        var retrievedVisit = getVisit(visitId).orElseThrow();
        assertThat(retrievedVisit.getDate(), equalTo(visit.getDate()));
        assertThat(retrievedVisit.getDescription(), equalTo(visit.getDescription()));
    }

    @Test
    void testDeleteVisit() {
        var visitId = 2;
        var response = target
                .path("/petclinic/api/visits/" + visitId)
                .request()
                .delete();
        assertThat(response.getStatus(), is(204));
        assertTrue(getVisit(visitId).isEmpty());
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
