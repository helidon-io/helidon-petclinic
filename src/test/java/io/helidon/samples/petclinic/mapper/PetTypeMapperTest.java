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
import io.helidon.samples.petclinic.model.PetType;
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
import io.helidon.samples.petclinic.rest.dto.PetTypeFieldsDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@HelidonTest
class PetTypeMapperTest {
    @Inject
    private PetTypeMapper petTypeMapper;

    @Test
    void testToPetType() {
        var petTypeDto = new PetTypeDto(1);
        petTypeDto.setName("Test");

        var petType = petTypeMapper.toPetType(petTypeDto);

        assertNotNull(petType);
        assertEquals(petTypeDto.getId(), petType.getId());
        assertEquals(petTypeDto.getName(), petType.getName());
    }

    @Test
    void testToPetType_fromFieldsDto() {
        var petTypeFieldsDto = new PetTypeFieldsDto();
        petTypeFieldsDto.setName("Test");

        var petType = petTypeMapper.toPetType(petTypeFieldsDto);

        assertNotNull(petType);
        assertEquals(petTypeFieldsDto.getName(), petType.getName());
    }

    @Test
    void testToPetTypeDto() {
        var petType = new PetType();
        petType.setId(1);
        petType.setName("Test");

        var petTypeDto = petTypeMapper.toPetTypeDto(petType);

        assertNotNull(petTypeDto);
        assertEquals(petType.getId(), petTypeDto.getId());
        assertEquals(petType.getName(), petTypeDto.getName());
    }

    @Test
    void testToPetTypeDtos() {
        var petType = new PetType();
        petType.setId(1);
        petType.setName("Test");

        var petTypeDtos = petTypeMapper.toPetTypeDtos(Collections.singletonList(petType));

        assertNotNull(petTypeDtos);
        assertFalse(petTypeDtos.isEmpty());

        var petTypeDto = petTypeDtos.iterator().next();
        assertEquals(petType.getId(), petTypeDto.getId());
        assertEquals(petType.getName(), petTypeDto.getName());
    }
}