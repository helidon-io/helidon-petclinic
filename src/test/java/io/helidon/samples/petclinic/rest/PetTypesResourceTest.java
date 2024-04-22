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
import io.helidon.samples.petclinic.mapper.PetTypeMapper;
import io.helidon.samples.petclinic.model.PetType;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@HelidonTest
@ExtendWith(MockitoExtension.class)
public class PetTypesResourceTest {
    ClinicService clinicService;

    @Inject
    PetTypeMapper petTypeMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    PetTypesResource petTypesResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        petTypesResource = new PetTypesResource(clinicService, petTypeMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPetType() {
        var petTypeDto = createPetTypeDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));

        var response = petTypesResource.addPetType(petTypeDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/pettypes/1"));

        var returnedDto = (PetTypeDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(petTypeDto.getId()));
        assertThat(returnedDto.getName(), equalTo(petTypeDto.getName()));
    }

    @Test
    void testDeletePetType() {
        Mockito.when(clinicService.findPetTypeById(1)).thenReturn(Optional.of(createPetType(1)));
        var response = petTypesResource.deletePetType(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetPetTypes() {
        Mockito.when(clinicService.findPetTypeById(1)).thenReturn(Optional.of(createPetType(1)));
        var response = petTypesResource.getPetType(1);
        var petType = (PetTypeDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(petType.getId(), is(1));
        assertThat(petType.getName(), equalTo("dog"));
    }

    @Test
    void testListPetTypes() {
        var petTypes = new ArrayList<PetType>();
        petTypes.add(createPetType(1));
        petTypes.add(createPetType(2));

        Mockito.when(clinicService.findAllPetTypes()).thenReturn(petTypes);

        var response = petTypesResource.listPetTypes();
        assertThat(response.getStatus(), is(200));

        var petTypeDtoList = (List<PetTypeDto>)response.getEntity();
        assertThat(petTypeDtoList.size(), is(2));
        assertThat(petTypeDtoList.get(0).getId(), is(1));
        assertThat(petTypeDtoList.get(1).getId(), is(2));
    }

    @Test
    void testUpdatePetType() {
        var petTypeDto = createPetTypeDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findPetTypeById(1)).thenReturn(Optional.of(createPetType(1)));

        var response = petTypesResource.updatePetType(1, petTypeDto);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/pettypes/1"));

        var returnedDto = (PetTypeDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(petTypeDto.getId()));
    }

    private PetTypeDto createPetTypeDto(int id) {
        var petTypeDto = new PetTypeDto(id);
        petTypeDto.setName("dog");
        return petTypeDto;
    }

    private PetType createPetType(int id) {
        var petType = new PetType();
        petType.setId(id);
        petType.setName("dog");
        return petType;
    }
}
