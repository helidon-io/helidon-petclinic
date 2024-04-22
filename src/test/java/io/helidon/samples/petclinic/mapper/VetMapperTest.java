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
import io.helidon.samples.petclinic.model.Specialty;
import io.helidon.samples.petclinic.model.Vet;
import io.helidon.samples.petclinic.rest.dto.SpecialtyDto;
import io.helidon.samples.petclinic.rest.dto.VetDto;
import io.helidon.samples.petclinic.rest.dto.VetFieldsDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@HelidonTest
class VetMapperTest {
    @Inject
    VetMapper vetMapper;

    @Test
    void testToVetDto() {
        var specialty1 = new Specialty();
        specialty1.setId(1);
        specialty1.setName("Specialty1");

        var specialty2 = new Specialty();
        specialty2.setId(2);
        specialty2.setName("Specialty2");

        var vet = new Vet();
        vet.setId(1);
        vet.setFirstName("FirstName");
        vet.setLastName("LastName");
        vet.setSpecialties(List.of(specialty1, specialty2));

        var vetDto = vetMapper.toVetDto(vet);
        assertThat(vetDto.getId(), equalTo(vet.getId()));
        assertThat(vetDto.getFirstName(), equalTo(vet.getFirstName()));
        assertThat(vetDto.getLastName(), equalTo(vet.getLastName()));
        assertThat(vetDto.getSpecialties().size(), is(2));
        assertThat(vetDto.getSpecialties().get(0).getId(), equalTo(specialty1.getId()));
        assertThat(vetDto.getSpecialties().get(0).getName(), equalTo(specialty1.getName()));
        assertThat(vetDto.getSpecialties().get(1).getId(), equalTo(specialty2.getId()));
        assertThat(vetDto.getSpecialties().get(1).getName(), equalTo(specialty2.getName()));
    }

    @Test
    void testToVet() {
        var specialty1Dto = new SpecialtyDto(11);
        specialty1Dto.setName("Specialty1");

        var specialty2Dto = new SpecialtyDto(12);
        specialty2Dto.setName("Specialty2");

        var vetDto = new VetDto(1);
        vetDto.setFirstName("FirstName");
        vetDto.setLastName("LastName");
        vetDto.setSpecialties(List.of(specialty1Dto, specialty2Dto));

        var vet = vetMapper.toVet(vetDto);
        assertThat(vet.getId(), equalTo(vetDto.getId()));
        assertThat(vet.getFirstName(), equalTo(vetDto.getFirstName()));
        assertThat(vet.getLastName(), equalTo(vetDto.getLastName()));
        assertThat(vet.getSpecialties().size(), is(2));
        assertThat(vet.getSpecialties().get(0).getId(), equalTo(specialty1Dto.getId()));
        assertThat(vet.getSpecialties().get(0).getName(), equalTo(specialty1Dto.getName()));
        assertThat(vet.getSpecialties().get(1).getId(), equalTo(specialty2Dto.getId()));
        assertThat(vet.getSpecialties().get(1).getName(), equalTo(specialty2Dto.getName()));
    }

    @Test
    void testToVetFromVetFieldsDto() {
        var specialty1Dto = new SpecialtyDto(11);
        specialty1Dto.setName("Specialty1");

        var specialty2Dto = new SpecialtyDto(12);
        specialty2Dto.setName("Specialty2");

        var vetFieldsDto = new VetFieldsDto();
        vetFieldsDto.setFirstName("FirstName");
        vetFieldsDto.setLastName("LastName");
        vetFieldsDto.setSpecialties(List.of(specialty1Dto, specialty2Dto));

        var vet = vetMapper.toVet(vetFieldsDto);
        assertThat(vet.getFirstName(), equalTo(vetFieldsDto.getFirstName()));
        assertThat(vet.getLastName(), equalTo(vetFieldsDto.getLastName()));
        assertThat(vet.getSpecialties().size(), is(2));
        assertThat(vet.getSpecialties().get(0).getId(), equalTo(specialty1Dto.getId()));
        assertThat(vet.getSpecialties().get(0).getName(), equalTo(specialty1Dto.getName()));
        assertThat(vet.getSpecialties().get(1).getId(), equalTo(specialty2Dto.getId()));
        assertThat(vet.getSpecialties().get(1).getName(), equalTo(specialty2Dto.getName()));
    }
    @Test
    void testToVetDtos() {
        var specialty1 = new Specialty();
        specialty1.setId(1);
        specialty1.setName("Specialty1");

        var specialty2 = new Specialty();
        specialty2.setId(2);
        specialty2.setName("Specialty2");

        var vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("FirstName1");
        vet1.setLastName("LastName1");
        vet1.setSpecialties(List.of(specialty1, specialty2));

        var vet2 = new Vet();
        vet2.setId(2);
        vet2.setFirstName("FirstName2");
        vet2.setLastName("LastName2");
        vet2.setSpecialties(List.of(specialty1, specialty2));

        var vets = List.of(vet1, vet2);
        var vetDtos = vetMapper.toVetDtos(vets);

        assertNotNull(vetDtos);
        assertEquals(vets.size(), vetDtos.size());

        for(int i = 0; i < vetDtos.size(); i++) {
            assertEquals(vets.get(i).getId(), vetDtos.get(i).getId());
            assertEquals(vets.get(i).getFirstName(), vetDtos.get(i).getFirstName());
            assertEquals(vets.get(i).getLastName(), vetDtos.get(i).getLastName());
            assertEquals(vets.get(i).getSpecialties().size(), vetDtos.get(i).getSpecialties().size());

            for(int j = 0; j < vets.get(i).getSpecialties().size(); j++) {
                assertEquals(vets.get(i).getSpecialties().get(j).getId(), vetDtos.get(i).getSpecialties().get(j).getId());
                assertEquals(vets.get(i).getSpecialties().get(j).getName(), vetDtos.get(i).getSpecialties().get(j).getName());
            }
        }
    }
}
