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
package io.helidon.samples.petclinic.mapper;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.model.Visit;
import io.helidon.samples.petclinic.rest.dto.VisitDto;
import io.helidon.samples.petclinic.rest.dto.VisitFieldsDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@HelidonTest
class VisitMapperTest {
    @Inject
    VisitMapper visitMapper;

    @Test
    void testToVisitDto() {
        var visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.now());
        visit.setDescription("emasculation");

        var pet = new Pet();
        pet.setId(21);
        pet.addVisit(visit);
        pet.addVisit(visit);

        var visitDto = visitMapper.toVisitDto(visit);
        assertThat(visitDto.getId(), equalTo(visit.getId()));
        assertThat(visitDto.getDate(), equalTo(visit.getDate()));
        assertThat(visitDto.getDescription(), equalTo(visit.getDescription()));
        assertThat(visitDto.getPetId(), is(21));
    }

    @Test
    void testToVisit() {
        var visitDto = new VisitDto(1, 11);
        visitDto.setDate(LocalDate.now());
        visitDto.setDescription("emasculation");

        var visit = visitMapper.toVisit(visitDto);
        assertThat(visit.getId(), equalTo(visitDto.getId()));
        assertNull(visit.getPet());
        assertThat(visit.getDate(), equalTo(visitDto.getDate()));
        assertThat(visit.getDescription(), equalTo(visitDto.getDescription()));
    }

    @Test
    void testToVisitFromVisitFieldsDto() {
        var visitFieldsDto = new VisitFieldsDto();
        visitFieldsDto.setDate(LocalDate.now());
        visitFieldsDto.setDescription("emasculation");

        var visit = visitMapper.toVisit(visitFieldsDto);
        assertNull(visit.getId());
        assertNull(visit.getPet());
        assertThat(visit.getDate(), equalTo(visitFieldsDto.getDate()));
        assertThat(visit.getDescription(), equalTo(visitFieldsDto.getDescription()));
    }

    @Test
    void testToVisitDtos() {
        var visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("emasculation");

        var visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.now());
        visit2.setDescription("vaccination");

        var pet = new Pet();
        pet.setId(21);
        pet.addVisit(visit1);
        pet.addVisit(visit2);

        var visits = List.of(visit1, visit2);
        var visitDtos = visitMapper.toVisitDtos(visits);

        assertNotNull(visitDtos);
        assertEquals(visits.size(), visitDtos.size());

        for(int i = 0; i < visitDtos.size(); i++) {
            assertEquals(visits.get(i).getId(), visitDtos.get(i).getId());
            assertEquals(visits.get(i).getDate(), visitDtos.get(i).getDate());
            assertEquals(visits.get(i).getDescription(), visitDtos.get(i).getDescription());
        }
    }
}
