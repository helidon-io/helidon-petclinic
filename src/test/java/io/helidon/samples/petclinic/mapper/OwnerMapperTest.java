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
import io.helidon.samples.petclinic.rest.dto.OwnerDto;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@HelidonTest
class OwnerMapperTest {
    @Inject
    private OwnerMapper mapper;

    @Test
    void testToOwnerDto() {
        var owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        var ownerDto = mapper.toOwnerDto(owner);

        assertNotNull(ownerDto);
        assertEquals(owner.getId(), ownerDto.getId());
        assertEquals(owner.getFirstName(), ownerDto.getFirstName());
        assertEquals(owner.getLastName(), ownerDto.getLastName());
    }

    @Test
    void testToOwner() {
        var ownerDto = new OwnerDto(1, new ArrayList<>());
        ownerDto.setFirstName("John");
        ownerDto.setLastName("Doe");

        var owner = mapper.toOwner(ownerDto);

        assertNotNull(owner);
        assertEquals(ownerDto.getId(), owner.getId());
        assertEquals(ownerDto.getFirstName(), owner.getFirstName());
        assertEquals(ownerDto.getLastName(), owner.getLastName());
    }

    @Test
    void testToOwnerCollection() {
        var ownerDto1 = new OwnerDto(1, new ArrayList<>());
        ownerDto1.setFirstName("John");
        ownerDto1.setLastName("Doe");

        var ownerDto2 = new OwnerDto(2, new ArrayList<>());
        ownerDto2.setFirstName("Jane");
        ownerDto2.setLastName("Smith");

        var owners = mapper.toOwners(List.of(ownerDto1, ownerDto2));

        assertNotNull(owners);
        assertEquals(owners.size(), 2);

        var iterator = owners.iterator();

        var owner1 = iterator.next();
        assertEquals(ownerDto1.getId(), owner1.getId());
        assertEquals(ownerDto1.getFirstName(), owner1.getFirstName());
        assertEquals(ownerDto1.getLastName(), owner1.getLastName());

        var owner2 = iterator.next();
        assertEquals(ownerDto2.getId(), owner2.getId());
        assertEquals(ownerDto2.getFirstName(), owner2.getFirstName());
        assertEquals(ownerDto2.getLastName(), owner2.getLastName());
    }
}
