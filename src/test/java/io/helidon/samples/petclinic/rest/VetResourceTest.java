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
import io.helidon.samples.petclinic.mapper.VetMapper;
import io.helidon.samples.petclinic.model.Vet;
import io.helidon.samples.petclinic.rest.dto.VetDto;
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
public class VetResourceTest {
    ClinicService clinicService;

    @Inject
    VetMapper vetMapper;

    @Mock
    UriInfo uriInfo;

    @InjectMocks
    VetResource vetsResource;

    @BeforeEach
    void setup() {
        clinicService = Mockito.mock(ClinicService.class);
        vetsResource = new VetResource(clinicService, vetMapper);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddVet() {
        var vetDto = createVetDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));

        var response = vetsResource.addVet(vetDto);
        assertThat(response.getStatus(), is(201));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/vets/1"));

        var returnedDto = (VetDto)response.getEntity();
        assertThat(returnedDto.getId(), equalTo(vetDto.getId()));
        assertThat(returnedDto.getFirstName(), equalTo(vetDto.getFirstName()));
        assertThat(returnedDto.getLastName(), equalTo(vetDto.getLastName()));
    }

    @Test
    void testDeleteVet() {
        Mockito.when(clinicService.findVetById(1)).thenReturn(Optional.of(createVet(1)));
        var response = vetsResource.deleteVet(1);
        assertThat(response.getStatus(), is(204));
    }

    @Test
    void testGetVets() {
        Mockito.when(clinicService.findVetById(1)).thenReturn(Optional.of(createVet(1)));
        var response = vetsResource.getVet(1);
        var vet = (VetDto)response.getEntity();
        assertThat(response.getStatus(), is(200));
        assertThat(vet.getId(), is(1));
        assertThat(vet.getFirstName(), equalTo("John"));
    }

    @Test
    void testListVets() {
        var vets = new ArrayList<Vet>();
        vets.add(createVet(1));
        vets.add(createVet(2));

        Mockito.when(clinicService.findAllVets()).thenReturn(vets);

        var response = vetsResource.listVets();
        assertThat(response.getStatus(), is(200));

        var vetDtoList = (List<VetDto>)response.getEntity();
        assertThat(vetDtoList.size(), is(2));
        assertThat(vetDtoList.get(0).getId(), is(1));
        assertThat(vetDtoList.get(1).getId(), is(2));
    }

    @Test
    void testUpdateVet() {
        var vetDto = createVetDto(1);

        Mockito.when(uriInfo.getBaseUri()).thenReturn(URI.create("http://localhost:9966/petclinic"));
        Mockito.when(clinicService.findVetById(1)).thenReturn(Optional.of(createVet(1)));

        var response = vetsResource.updateVet(1, vetDto);
        assertThat(response.getStatus(), is(200));
        assertThat(response.getLocation().toString(), equalTo("http://localhost:9966/petclinic/api/vets/1"));

        var returnedPet = (VetDto)response.getEntity();
        assertThat(returnedPet.getId(), equalTo(vetDto.getId()));
    }

    private VetDto createVetDto(int id) {
        var vetDto = new VetDto(id);
        vetDto.setFirstName("John");
        vetDto.setLastName("Doe");
        return vetDto;
    }

    private Vet createVet(int id) {
        var vet = new Vet();
        vet.setId(id);
        vet.setFirstName("John");
        vet.setLastName("John");
        return vet;
    }
}
