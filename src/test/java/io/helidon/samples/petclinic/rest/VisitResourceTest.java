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
import io.helidon.samples.petclinic.mapper.VisitMapper;
import io.helidon.samples.petclinic.model.Owner;
import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.model.PetType;
import io.helidon.samples.petclinic.model.Visit;
import io.helidon.samples.petclinic.rest.dto.VisitDto;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@HelidonTest
@ExtendWith(MockitoExtension.class)
public class VisitResourceTest {
    ClinicService clinicService;

    @Inject
    VisitMapper visitMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    VisitResource visitResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        visitResource = new VisitResource(clinicService, visitMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddVisit() {
        var visitDto = createVisitDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findPetById(1)).thenReturn(Optional.of(createPet(1)));

        var response = visitResource.addVisit(visitDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/visits/1"));

        var returnedDto = (VisitDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(visitDto.getId()));
        assertThat(returnedDto.getDate(), equalTo(visitDto.getDate()));
        assertThat(returnedDto.getDescription(), equalTo(visitDto.getDescription()));
    }

    @Test
    void testDeleteVisit() {
        Mockito.when(clinicService.findVisitById(1)).thenReturn(Optional.of(createVisit(1)));
        var response = visitResource.deleteVisit(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetVisits() {
        Mockito.when(clinicService.findVisitById(1)).thenReturn(Optional.of(createVisit(1)));
        var response = visitResource.getVisit(1);
        var visit = (VisitDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(visit.getId(), is(1));
        assertThat(visit.getDescription(), equalTo("visit"));
    }

    @Test
    void testListVisits() {
        var visits = new ArrayList<Visit>();
        visits.add(createVisit(1));
        visits.add(createVisit(2));

        Mockito.when(clinicService.findAllVisits()).thenReturn(visits);

        var response = visitResource.listVisits();
        assertThat(response.getStatus(), is(200));

        var visitDtoList = (List<VisitDto>)response.getEntity();
        assertThat(visitDtoList.size(), is(2));
        assertThat(visitDtoList.get(0).getId(), is(1));
        assertThat(visitDtoList.get(1).getId(), is(2));
    }

    @Test
    void testUpdateVisit() {
        var visitDto = createVisitDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findVisitById(1)).thenReturn(Optional.of(createVisit(1)));

        var response = visitResource.updateVisit(1, visitDto);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/visits/1"));

        var returnedDto = (VisitDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(visitDto.getId()));
    }

    private VisitDto createVisitDto(int id) {
        var visitDto = new VisitDto(id, 1);
        visitDto.setDate(LocalDate.now());
        visitDto.setDescription("visit");
        return visitDto;
    }

    private Visit createVisit(int id) {
        var visit = new Visit();
        visit.setId(id);
        visit.setPet(createPet(1));
        visit.setDate(LocalDate.now());
        visit.setDescription("visit");
        return visit;
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
