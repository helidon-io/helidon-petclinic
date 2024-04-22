/*
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

import io.helidon.samples.petclinic.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Mostly used as a facade so all controllers have a single point of entry
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
public interface ClinicService {

	Optional<Pet> findPetById(int id);
	List<Pet> findAllPets();
	void savePet(Pet pet);
	void deletePet(Pet pet);

	List<Visit> findVisitsByPetId(int petId);
	Optional<Visit> findVisitById(int visitId);
	List<Visit> findAllVisits();
	void saveVisit(Visit visit);
	void deleteVisit(Visit visit);
	Optional<Vet> findVetById(int id);
	Collection<Vet> findAllVets();
	void saveVet(Vet vet);
	void deleteVet(Vet vet);
	Optional<Owner> findOwnerById(int id);
	Collection<Owner> findAllOwners();
	void saveOwner(Owner owner);
	void deleteOwner(Owner owner);
	List<Owner> findOwnerByLastName(String lastName);

	Optional<PetType> findPetTypeById(int petTypeId);
	List<PetType> findAllPetTypes();
	void savePetType(PetType petType);
	void deletePetType(PetType petType);
	Optional<Specialty> findSpecialtyById(int specialtyId);
	Collection<Specialty> findAllSpecialties();
	void saveSpecialty(Specialty specialty);
	void deleteSpecialty(Specialty specialty);

	List<Specialty> findSpecialtiesByIdIn(Set<Integer> ids);

	List<Specialty> findSpecialtiesByNameIn(Set<String> names);

    PetType findPetTypeByName(String name);
}
