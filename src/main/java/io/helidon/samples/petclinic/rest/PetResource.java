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

import io.helidon.samples.petclinic.mapper.PetMapper;
import io.helidon.samples.petclinic.rest.api.PetService;
import io.helidon.samples.petclinic.rest.dto.PetDto;
import io.helidon.samples.petclinic.service.ClinicService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

@RequestScoped
public class PetResource implements PetService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final PetMapper petMapper;

    @Inject
    public PetResource(ClinicService clinicService, PetMapper petMapper) {
        this.clinicService = clinicService;
        this.petMapper = petMapper;
    }

    @Override
    public Response addPet(PetDto petDto) {
        var owner = clinicService.findOwnerById(petDto.getOwnerId()).orElseThrow();
        var pet = petMapper.toPet(petDto);
        pet.setOwner(owner);
        clinicService.savePet(pet);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/pets/{id}").build(pet.getId());
        return Response.created(location).entity(petMapper.toPetDto(pet)).build();
    }

    @Override
    @Transactional
    public Response deletePet(Integer petId) {
        var pet = clinicService.findPetById(petId).orElseThrow(NotFoundException::new);
        clinicService.deletePet(pet);
        return Response.noContent().build();
    }

    @Override
    public Response getPet(Integer petId) {
        var pet = clinicService.findPetById(petId).orElseThrow(NotFoundException::new);
        return Response.ok(petMapper.toPetDto(pet)).build();
    }

    @Override
    public Response listPets() {
        var pets = petMapper.toPetDtos(clinicService.findAllPets());
        if (pets.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(pets).build();
    }

    @Override
    public Response updatePet(Integer petId, PetDto petDto) {
        var pet = clinicService.findPetById(petId).orElseThrow(NotFoundException::new);
        pet.setName(petDto.getName());
        pet.setBirthDate(petDto.getBirthDate());
        if (!pet.getType().getId().equals(petDto.getType().getId())) {
            var petType = clinicService.findPetTypeById(petDto.getType().getId()).orElseThrow(NotFoundException::new);
            pet.setType(petType);
        }
        if (!pet.getOwner().getId().equals(petDto.getOwnerId())) {
            var owner = clinicService.findOwnerById(petDto.getOwnerId()).orElseThrow(NotFoundException::new);
            pet.setOwner(owner);
        }
        clinicService.savePet(pet);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/pets/{id}").build(pet.getId());
        return Response.ok(petMapper.toPetDto(pet)).location(location).build();
    }
}
