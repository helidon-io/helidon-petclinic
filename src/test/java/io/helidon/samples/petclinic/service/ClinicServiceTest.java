/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.samples.petclinic.service;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Clinic service which don't update the database.
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@HelidonTest
class ClinicServiceTest {
    @Inject
    ClinicService clinicService;

    @Test
    void testFindOwnersByLastName() {
        var owners = clinicService.findOwnerByLastName("Davis");
        assertThat(owners.size(), is(2));

        owners = this.clinicService.findOwnerByLastName("Daviss");
        assertTrue(owners.isEmpty());
    }

    @Test
    void testFindSingleOwnerWithPet() {
        var optOwner = clinicService.findOwnerById(1);
        assertTrue(optOwner.isPresent());

        var owner = optOwner.get();
        assertThat(owner.getLastName(), startsWith("Franklin"));
        assertThat(owner.getPets().size(), is(1));
        assertNotNull(owner.getPets().getFirst().getType());
        assertThat(owner.getPets().getFirst().getType().getName(), is("dog"));
    }

    @Test
    void testFindPetWithCorrectId() {
        var optPet7 = clinicService.findPetById(7);
        assertTrue(optPet7.isPresent());

        var pet7 = optPet7.get();
        assertThat(pet7.getName(), equalTo("Filimon"));
        assertThat(pet7.getOwner().getFirstName(), equalTo("Jean"));
    }

    @Test
    void testFindAllPetTypes() {
        var petTypes = clinicService.findAllPetTypes();
        assertThat(petTypes.size(), is(6));
    }

    @Test
    void testFindVets() {
        var vets = clinicService.findAllVets();
        assertThat(vets.size(), is(6));
    }

    @Test
    void testFindVisitsByPetId() {
        var visits = clinicService.findVisitsByPetId(8);
        assertThat(visits.size(), is(2));
    }

    @Test
    void testFindAllPets() {
        var pets = clinicService.findAllPets();
        assertThat(pets.size(), greaterThan(0));
    }

    @Test
    void testFindVisitById() {
    	var visit = clinicService.findVisitById(1).orElseThrow();
    	assertThat(visit.getId(), is(1));
        assertThat(visit.getPet().getId(), is(7));
    	assertThat(visit.getPet().getName(), equalTo("Filimon"));
    }

    @Test
    void testFindAllVisits() {
        var visits = clinicService.findAllVisits();
        assertThat(visits.size(), is(4));
    }

    @Test
    void testFindVetDyId() {
    	var vet = clinicService.findVetById(1).orElseThrow();
    	assertThat(vet.getFirstName(), equalTo("James"));
    	assertThat(vet.getLastName(), equalTo("Carter"));
    }

    @Test
    void testFindAllOwners() {
        var owners = this.clinicService.findAllOwners();
        assertThat(owners.size(), is(10));
    }

    @Test
    void testFindPetTypeById() {
    	var petType = clinicService.findPetTypeById(1).orElseThrow();
    	assertThat(petType.getName(), equalTo("cat"));
    }

    @Test
    void testFindSpecialtyById() {
    	var specialty = clinicService.findSpecialtyById(1).orElseThrow();
    	assertThat(specialty.getName(), equalTo("radiology"));
    }

    @Test
    void testFindAllSpecialties() {
        var specialties = clinicService.findAllSpecialties();
        assertThat(specialties.size(), is(3));
    }

    @Test
    void testFindPetTypeByName() {
        var petType = clinicService.findPetTypeByName("cat");
        assertThat(petType.getId(), is(1));
    }
}
