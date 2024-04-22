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
import io.helidon.samples.petclinic.rest.dto.SpecialtyDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@HelidonTest
class SpecialtyMapperTest {
    @Inject
    private SpecialtyMapper specialtyMapper;

    @Test
    void testToSpecialty() {
        var specialtyDto = new SpecialtyDto(1);
        specialtyDto.setName("Test");

        var specialty = specialtyMapper.toSpecialty(specialtyDto);

        assertNotNull(specialty);
        assertEquals(specialtyDto.getId(), specialty.getId());
        assertEquals(specialtyDto.getName(), specialty.getName());
    }

    @Test
    void testToSpecialtyDto() {
        var specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("Test");

        var specialtyDto = specialtyMapper.toSpecialtyDto(specialty);

        assertNotNull(specialtyDto);
        assertEquals(specialty.getId(), specialtyDto.getId());
        assertEquals(specialty.getName(), specialtyDto.getName());
    }

    @Test
    void testToSpecialtyDtos() {
        Specialty specialty = new Specialty();
        specialty.setId(1);
        specialty.setName("Test");

        var specialtyDtos = specialtyMapper.toSpecialtyDtos(Collections.singletonList(specialty));

        assertNotNull(specialtyDtos);
        assertFalse(specialtyDtos.isEmpty());

        SpecialtyDto specialtyDto = specialtyDtos.iterator().next();
        assertEquals(specialty.getId(), specialtyDto.getId());
        assertEquals(specialty.getName(), specialtyDto.getName());
    }

    @Test
    void testToSpecialties() {
        SpecialtyDto specialtyDto = new SpecialtyDto(1);
        specialtyDto.setName("Test");

        var specialties = specialtyMapper.toSpecialties(Collections.singletonList(specialtyDto));

        assertNotNull(specialties);
        assertFalse(specialties.isEmpty());

        var specialty = specialties.iterator().next();
        assertEquals(specialtyDto.getId(), specialty.getId());
        assertEquals(specialtyDto.getName(), specialty.getName());
    }
}