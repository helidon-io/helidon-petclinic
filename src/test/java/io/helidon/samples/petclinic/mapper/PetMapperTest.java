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
import io.helidon.samples.petclinic.model.Owner;
import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.model.PetType;
import io.helidon.samples.petclinic.rest.dto.PetDto;
import io.helidon.samples.petclinic.rest.dto.PetFieldsDto;
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@HelidonTest
class PetMapperTest {
    @Inject
    private PetMapper petMapper;

    @Test
    void testToPetDto() {
        var owner = new Owner();
        owner.setId(1);

        var petType = new PetType();
        petType.setId(3);
        petType.setName("Dog");

        var pet = new Pet();
        pet.setId(2);
        pet.setName("PetName");
        pet.setBirthDate(LocalDate.now());
        pet.setOwner(owner);
        pet.setType(petType);

        var petDto = petMapper.toPetDto(pet);

        assertNotNull(petDto);
        assertEquals(pet.getId(), petDto.getId());
        assertEquals(pet.getName(), petDto.getName());
        assertEquals(pet.getBirthDate(), petDto.getBirthDate());
        assertEquals(pet.getType().getId(), petDto.getType().getId());
        assertEquals(pet.getType().getName(), petDto.getType().getName());
    }

    @Test
    void testToPetDtos() {
        var owner = new Owner();
        owner.setId(1);

        var petType = new PetType();
        petType.setName("dog");

        var pet1 = new Pet();
        pet1.setName("Pet1");
        pet1.setType(petType);
        pet1.setOwner(owner);
        pet1.setBirthDate(LocalDate.now());

        var pet2 = new Pet();
        pet2.setName("Pet2");
        pet2.setType(petType);
        pet2.setOwner(owner);
        pet2.setBirthDate(LocalDate.now());

        var pets = Arrays.asList(pet1, pet2);

        var petDtos = petMapper.toPetDtos(pets);

        assertNotNull(petDtos);
        assertEquals(2, petDtos.size());

        for (int i = 0; i < pets.size(); i++) {
            var pet = pets.get(i);
            var petDto = petDtos.get(i);
            assertEquals(pet.getId(), petDto.getId());
            assertEquals(pet.getName(), petDto.getName());
            assertEquals(pet.getBirthDate(), petDto.getBirthDate());
            assertEquals(pet.getType().getId(), petDto.getType().getId());
            assertEquals(pet.getType().getName(), petDto.getType().getName());
            assertEquals(pet.getOwner().getId(), petDto.getOwnerId());
        }
    }

    @Test
    void testToPet() {
        var petTypeDto = new PetTypeDto();
        petTypeDto.setName("dog");

        var petDto = new PetDto(1, 1, new ArrayList<>());
        petDto.setName("Falco");
        petDto.setBirthDate(LocalDate.now());
        petDto.setType(petTypeDto);

        var pet = petMapper.toPet(petDto);

        assertNotNull(pet);
        assertEquals(petDto.getName(), pet.getName());
        assertEquals(petDto.getBirthDate(), pet.getBirthDate());
        assertEquals(petDto.getType().getName(), pet.getType().getName());
        assertTrue(pet.getVisits().isEmpty());
    }

    @Test
    void testToPets() {
        var petTypeDto = new PetTypeDto();
        petTypeDto.setName("dog");

        var petDto1 = new PetDto(1, 1, new ArrayList<>());
        petDto1.setName("Jasha");
        petDto1.setBirthDate(LocalDate.now());
        petDto1.setType(petTypeDto);

        var petDto2 = new PetDto(2, 2, new ArrayList<>());
        petDto2.setName("Filimon");
        petDto2.setBirthDate(LocalDate.now());
        petDto2.setType(petTypeDto);

        List<PetDto> petDtos = List.of(petDto1, petDto2);
        List<Pet> pets = petMapper.toPets(petDtos);

        assertNotNull(pets);
        assertEquals(petDtos.size(), pets.size());

        for (int i = 0; i < pets.size(); i++) {
            assertEquals(petDtos.get(i).getName(), pets.get(i).getName());
            assertEquals(petDtos.get(i).getBirthDate(), pets.get(i).getBirthDate());
            assertEquals(petDtos.get(i).getType().getName(), pets.get(i).getType().getName());
            assertNotNull(pets.get(i).getVisits());
            assertTrue(pets.get(i).getVisits().isEmpty());
        }
    }

    @Test
    void testToPet_fromFieldsDto() {
        var petTypeDto = new PetTypeDto();
        petTypeDto.setName("dog");

        var petFieldsDto = new PetFieldsDto();
        petFieldsDto.setName("Falco");
        petFieldsDto.setBirthDate(LocalDate.now());
        petFieldsDto.setType(petTypeDto);

        var pet = petMapper.toPet(petFieldsDto);

        assertNotNull(pet);
        assertEquals(petFieldsDto.getName(), pet.getName());
        assertEquals(petFieldsDto.getBirthDate(), pet.getBirthDate());
        assertEquals(petFieldsDto.getType().getName(), pet.getType().getName());
        assertTrue(pet.getVisits().isEmpty());
    }
}