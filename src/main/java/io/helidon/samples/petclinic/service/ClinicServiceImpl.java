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

import io.helidon.samples.petclinic.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@ApplicationScoped
public class ClinicServiceImpl implements ClinicService {
	@PersistenceContext(unitName = "pu1")
	private EntityManager entityManager;

	@Override
	public List<Pet> findAllPets() {
		return entityManager.createNamedQuery("findAllPets", Pet.class).getResultList();
	}

	@Override
	@Transactional
	public void deletePet(Pet pet) {
		var owner = pet.getOwner();
		owner.deletePet(pet);
		entityManager.merge(owner);
		entityManager.flush();
	}

	@Override
	public Optional<Visit> findVisitById(int visitId) {
		return Optional.ofNullable(entityManager.find(Visit.class, visitId));
	}

	@Override
	public List<Visit> findAllVisits() {
		return entityManager.createNamedQuery("findAllVisits", Visit.class).getResultList();
	}

	@Override
	@Transactional
	public void deleteVisit(Visit visit) {
		var pet = visit.getPet();
		pet.deleteVisit(visit);
		entityManager.merge(pet);
		entityManager.flush();
	}

	@Override
	public Optional<Vet> findVetById(int vetId) {
		return Optional.ofNullable(entityManager.find(Vet.class, vetId));
	}

	@Override
	public List<Vet> findAllVets() {
		return entityManager.createNamedQuery("findAllVets", Vet.class).getResultList();
	}

	@Override
	@Transactional
	public void saveVet(Vet vet) {
		if (vet.isNew()) {
			entityManager.persist(vet);
		} else {
			entityManager.merge(vet);
		}
	}

	@Override
	@Transactional
	public void deleteVet(Vet vet) {
		entityManager.remove(vet);
	}

	@Override
	@Transactional
	public Collection<Owner> findAllOwners() {
		return entityManager.createNamedQuery("findAllOwners", Owner.class).getResultList();
	}

	@Override
	@Transactional
	public void deleteOwner(Owner owner) {
		entityManager.remove(owner);
	}

	@Override
	public Optional<PetType> findPetTypeById(int petTypeId) {
		return Optional.ofNullable(entityManager.find(PetType.class, petTypeId));
	}

	@Override
	public List<PetType> findAllPetTypes() {
		return entityManager.createNamedQuery("findAllPetTypes", PetType.class).getResultList();
	}

	@Override
	@Transactional
	public void savePetType(PetType petType) {
		if (petType.isNew()) {
			entityManager.persist(petType);
		} else {
			entityManager.merge(petType);
		}
	}

	@Override
	@Transactional
	public void deletePetType(PetType petType) {
		entityManager.remove(petType);
	}

	@Override
	public Optional<Specialty> findSpecialtyById(int specialtyId) {
		return Optional.ofNullable(entityManager.find(Specialty.class, specialtyId));
	}

	@Override
	public Collection<Specialty> findAllSpecialties() {
		return entityManager.createNamedQuery("findAllSpecialities", Specialty.class).getResultList();
	}

	@Override
	@Transactional
	public void saveSpecialty(Specialty specialty) {
		if (specialty.isNew()) {
			entityManager.persist(specialty);
		} else {
			entityManager.merge(specialty);
		}
	}

	@Override
	@Transactional
	public void deleteSpecialty(Specialty specialty) {
		entityManager.remove(specialty);
	}

	@Override
	public List<Specialty> findSpecialtiesByIdIn(Set<Integer> ids) {
		var query = entityManager.createNamedQuery("findSpecialtiesByIdsIn", Specialty.class);
		return query.setParameter("ids", ids).getResultList();
	}

	@Override
	public List<Specialty> findSpecialtiesByNameIn(Set<String> names) {
		var query = entityManager.createNamedQuery("findSpecialtiesByNameIn", Specialty.class);
		return query.setParameter("names", names).getResultList();
	}

	@Override
	public Optional<Owner> findOwnerById(int ownerId) {
		return Optional.ofNullable(entityManager.find(Owner.class, ownerId));
	}

	@Override
	public Optional<Pet> findPetById(int petId) {
		return Optional.ofNullable(entityManager.find(Pet.class, petId));
	}

	@Override
	@Transactional
	public void savePet(Pet pet) {
		if (pet.isNew()) {
			entityManager.persist(pet);
		} else {
			entityManager.merge(pet);
		}
		entityManager.flush();
	}

	@Override
	@Transactional
	public void saveVisit(Visit visit) {
		if (visit.isNew()) {
			var pet = findPetById(visit.getPet().getId()).orElseThrow();
			pet.addVisit(visit);
			entityManager.merge(pet);
		} else {
			entityManager.merge(visit);
		}
	}

	@Override
	@Transactional
	public void saveOwner(Owner owner) {
		if (owner.isNew()) {
			entityManager.persist(owner);
		} else {
			entityManager.merge(owner);
		}
	}

	@Override
	public List<Owner> findOwnerByLastName(String lastName) {
		var query = entityManager.createNamedQuery("findOwnersByLastName", Owner.class);
		return query.setParameter("lastName", lastName + "%").getResultList();
	}

	@Override
	public List<Visit> findVisitsByPetId(int petId) {
		var query = entityManager.createNamedQuery("findVisitsByPetId", Visit.class);
        return query.setParameter("petId", petId).getResultList();
	}

    @Override
    public PetType findPetTypeByName(String name) {
		var query = entityManager.createNamedQuery("getPetTypeByName", PetType.class);
		var petTypeList = query.setParameter("name", name).getResultList();
		if (petTypeList.isEmpty()) {
			throw new NotFoundException("Unable to find pet type with name " + name);
		}
		return petTypeList.get(0);
    }
}
