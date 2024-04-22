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
package io.helidon.samples.petclinic.service;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import io.helidon.samples.petclinic.model.*;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clinic service tests which perform add/update/delete operations. Transaction is initiated before
 * calling each method and rolls back after calling it.
 */
@HelidonTest
public class ClinicServiceTransactionalTest {
    @Inject
    ClinicService clinicService;

    @Inject
    UserTransaction transaction;

    @BeforeEach
    public void beginTransaction() throws NotSupportedException, SystemException {
        transaction.begin();
    }

    @AfterEach
    public void rollbackTransaction() {
        try {
            transaction.rollback();
        } catch (SystemException e) {
            throw new RuntimeException("Error rolling back transaction.", e);
        }
    }

    @Test
    void testInsertOwner() {
        var owners = clinicService.findOwnerByLastName("Schultz");
        int found = owners.size();

        var owner = new Owner();
        owner.setFirstName("Sam");
        owner.setLastName("Schultz");
        owner.setAddress("4, Evans Street");
        owner.setCity("Wollongong");
        owner.setTelephone("4444444444");
        clinicService.saveOwner(owner);

        assertThat(owner.getId().longValue(), not(0));
        assertNull(owner.getPet("null value"));

        owners = clinicService.findOwnerByLastName("Schultz");
        assertThat(owners.size(), is(found + 1));
    }

    @Test
    void testUpdateOwner() {
        var optOwner = clinicService.findOwnerById(1);
        assertTrue(optOwner.isPresent());

        var owner = optOwner.get();
        var oldLastName = owner.getLastName();
        var newLastName = oldLastName + "X";

        owner.setLastName(newLastName);
        clinicService.saveOwner(owner);

        // retrieving new name from database
        optOwner = this.clinicService.findOwnerById(1);
        assertTrue(optOwner.isPresent());
        assertThat(optOwner.get().getLastName(), equalTo(newLastName));
    }

    @Test
    void testInsertPetIntoDatabaseAndGenerateId() {
        var optOwner6 = clinicService.findOwnerById(6);
        assertTrue(optOwner6.isPresent());

        var owner6 = optOwner6.get();
        int found = owner6.getPets().size();

        var pet = new Pet();
        pet.setName("bowser");
        pet.setType(clinicService.findPetTypeById(2).orElseThrow());
        pet.setBirthDate(LocalDate.now());

        owner6.addPet(pet);
        assertThat(owner6.getPets().size(), is(found + 1));

        this.clinicService.savePet(pet);
        this.clinicService.saveOwner(owner6);

        owner6 = clinicService.findOwnerById(6).orElseThrow();
        assertThat(owner6.getPets().size(), is(found + 1));
        assertNotNull(pet.getId());
    }

    @Test
    void testUpdatePetName() {
        var pet7 = clinicService.findPetById(7).orElseThrow();
        var oldName = pet7.getName();

        var newName = oldName + "X";
        pet7.setName(newName);
        clinicService.savePet(pet7);

        pet7 = clinicService.findPetById(7).orElseThrow();
        assertThat(pet7.getName(), equalTo(newName));
    }

    @Test
    void testAddNewVisitForPet() {
        var pet7 = clinicService.findPetById(7).orElseThrow();
        int found = pet7.getVisits().size();

        var visit = new Visit();
        visit.setDescription("test");
        pet7.addVisit(visit);

        clinicService.saveVisit(visit);
        clinicService.savePet(pet7);

        pet7 = clinicService.findPetById(7).orElseThrow();
        assertThat(pet7.getVisits().size(), is(found + 1));
        assertThat(visit.getId(), notNullValue());
    }

    @Test
    void testDeletePet() {
        var pet = clinicService.findPetById(1).orElseThrow();
        clinicService.deletePet(pet);
        assertTrue(clinicService.findPetById(1).isEmpty());
    }

    @Test
    void testInsertVisit() {
        var visits = clinicService.findAllVisits();
        int found = visits.size();

        var pet = this.clinicService.findPetById(1).orElseThrow();

        var visit = new Visit();
        visit.setPet(pet);
        visit.setDate(LocalDate.now());
        visit.setDescription("new visit");

        clinicService.saveVisit(visit);
        assertThat(visit.getId().longValue(), not(0));

        visits = clinicService.findAllVisits();
        assertThat(visits.size(), is(found + 1));
    }

    @Test
    void testUpdateVisit() {
        var visit = clinicService.findVisitById(1).orElseThrow();
        String oldDesc = visit.getDescription();
        String newDesc = oldDesc + "X";
        visit.setDescription(newDesc);
        clinicService.saveVisit(visit);

        visit = clinicService.findVisitById(1).orElseThrow();
        assertThat(visit.getDescription(), equalTo(newDesc));
    }

    @Test
    void testDeleteVisit() {
    	var visit = clinicService.findVisitById(1).orElseThrow();
        clinicService.deleteVisit(visit);
        assertTrue(clinicService.findVisitById(1).isEmpty());
    }

    @Test
    void testInsertVet() {
        var vets = clinicService.findAllVets();
        int found = vets.size();

        var vet = new Vet();
        vet.setFirstName("John");
        vet.setLastName("Dow");

        clinicService.saveVet(vet);
        assertThat(vet.getId().longValue(), not(0));

        assertThat(clinicService.findAllVets().size(), is(found + 1));
    }

    @Test
    void testUpdateVet() {
    	var vet = this.clinicService.findVetById(1).orElseThrow();
    	String oldLastName = vet.getLastName();
        String newLastName = oldLastName + "X";
        vet.setLastName(newLastName);
        clinicService.saveVet(vet);

        vet = clinicService.findVetById(1).orElseThrow();
        assertThat(vet.getLastName(), equalTo(newLastName));
    }

    @Test
    void testDeleteVet() {
    	var vet = clinicService.findVetById(1).orElseThrow();
        clinicService.deleteVet(vet);

        assertTrue(clinicService.findVetById(1).isEmpty());
    }

    @Test
    void testDeleteOwner() {
    	var owner = clinicService.findOwnerById(1).orElseThrow();
        clinicService.deleteOwner(owner);
        assertTrue(clinicService.findOwnerById(1).isEmpty());
    }

    @Test
    void testInsertPetType() {
        var petTypes = clinicService.findAllPetTypes();
        int found = petTypes.size();

        var petType = new PetType();
        petType.setName("tiger");

        clinicService.savePetType(petType);
        assertThat(petType.getId().longValue(), not(0));

        assertThat(clinicService.findAllPetTypes().size(), is(found + 1));
    }

    @Test
    void testUpdatePetType() {
    	var petType = clinicService.findPetTypeById(1).orElseThrow();
    	String oldLastName = petType.getName();
        String newLastName = oldLastName + "X";
        petType.setName(newLastName);
        clinicService.savePetType(petType);

        petType = this.clinicService.findPetTypeById(1).orElseThrow();
        assertThat(petType.getName(), equalTo(newLastName));
    }

    @Test
    void testDeletePetType() {
    	var petType = clinicService.findPetTypeById(1).orElseThrow();
        clinicService.deletePetType(petType);

        assertTrue(clinicService.findPetTypeById(1).isEmpty());
    }

    @Test
    void testInsertSpecialty() {
        var specialties = clinicService.findAllSpecialties();
        int found = specialties.size();

        var specialty = new Specialty();
        specialty.setName("dermatologist");

        clinicService.saveSpecialty(specialty);
        assertThat(specialty.getId().longValue(), not(0));
        assertThat(clinicService.findAllSpecialties().size(), is(found + 1));
    }

    @Test
    void testUpdateSpecialty() {
    	var specialty = clinicService.findSpecialtyById(1).orElseThrow();
    	String oldLastName = specialty.getName();
        String newLastName = oldLastName + "X";
        specialty.setName(newLastName);
        clinicService.saveSpecialty(specialty);

        specialty = clinicService.findSpecialtyById(1).orElseThrow();
        assertThat(specialty.getName(), equalTo(newLastName));
    }

    @Test
    void testDeleteSpecialty() {
        var specialty = clinicService.findSpecialtyById(1).orElseThrow();
        clinicService.deleteSpecialty(specialty);

        assertTrue(clinicService.findSpecialtyById(1).isEmpty());
    }

    @Test
    void testFindSpecialtiesByNameIn() {
        var specialty1 = new Specialty();
        specialty1.setName("radiology");
        specialty1.setId(1);
        var specialty2 = new Specialty();
        specialty2.setName("surgery");
        specialty2.setId(2);
        var specialty3 = new Specialty();
        specialty3.setName("dentistry");
        specialty3.setId(3);

        var expectedSpecialties = List.of(specialty1, specialty2, specialty3);
        var specialtyNames = expectedSpecialties.stream()
            .map(Specialty::getName)
            .collect(Collectors.toSet());

        var actualSpecialties = clinicService.findSpecialtiesByNameIn(specialtyNames);
        assertNotNull(actualSpecialties);
        assertThat(actualSpecialties.size(), is(expectedSpecialties.size()));
        for (var expected : expectedSpecialties) {
            assertThat(actualSpecialties.stream()
                .anyMatch(
                    actual -> actual.getName().equals(expected.getName())
                    && actual.getId().equals(expected.getId())), is(true));
        }
    }
}