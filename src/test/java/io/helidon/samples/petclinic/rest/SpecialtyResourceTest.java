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
import io.helidon.samples.petclinic.mapper.SpecialtyMapper;
import io.helidon.samples.petclinic.model.Specialty;
import io.helidon.samples.petclinic.rest.dto.SpecialtyDto;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@HelidonTest
@ExtendWith(MockitoExtension.class)
public class SpecialtyResourceTest {
    ClinicService clinicService;

    @Inject
    SpecialtyMapper specialtyMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    SpecialtyResource specialtyResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        specialtyResource = new SpecialtyResource(clinicService, specialtyMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddSpecialty() {
        var specialtyDto = createSpecialtyDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));

        var response = specialtyResource.addSpecialty(specialtyDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/specialities/1"));

        var dto = (SpecialtyDto)response.getEntity();
        assertThat(dto.getId(), equalTo(specialtyDto.getId()));
        assertThat(dto.getName(), equalTo(specialtyDto.getName()));
    }

    @Test
    void testDeleteSpecialty() {
        Mockito.when(clinicService.findSpecialtyById(1)).thenReturn(Optional.of(createSpecialty(1)));
        var response = specialtyResource.deleteSpecialty(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetSpecialty() {
        Mockito.when(clinicService.findSpecialtyById(1)).thenReturn(Optional.of(createSpecialty(1)));
        var response = specialtyResource.getSpecialty(1);
        var specialty = (SpecialtyDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(specialty.getId(), is(1));
        assertThat(specialty.getName(), equalTo("surgery"));
    }

    @Test
    void testListSpecialties() {
        var specialties = new ArrayList<Specialty>();
        specialties.add(createSpecialty(1));
        specialties.add(createSpecialty(2));

        Mockito.when(clinicService.findAllSpecialties()).thenReturn(specialties);

        var response = specialtyResource.listSpecialties();
        assertThat(response.getStatus(), is(200));

        var specialtyDtoList = (List<SpecialtyDto>)response.getEntity();
        assertThat(specialtyDtoList.size(), is(2));
        assertThat(specialtyDtoList.get(0).getId(), is(1));
        assertThat(specialtyDtoList.get(1).getId(), is(2));
    }

    @Test
    void testUpdateSpecialty() {
        var specialtyDto = createSpecialtyDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findSpecialtyById(1)).thenReturn(Optional.of(createSpecialty(1)));

        var response = specialtyResource.updateSpecialty(1, specialtyDto);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/specialities/1"));

        var returnedDto = (SpecialtyDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(specialtyDto.getId()));
    }

    private SpecialtyDto createSpecialtyDto(int id) {
        var specialtyDto = new SpecialtyDto(id);
        specialtyDto.setName("surgery");
        return specialtyDto;
    }

    private Specialty createSpecialty(int id) {
        var specialty = new Specialty();
        specialty.setId(id);
        specialty.setName("surgery");
        return specialty;
    }
}
