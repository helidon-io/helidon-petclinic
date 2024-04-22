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

import io.helidon.samples.petclinic.mapper.OwnerMapper;
import io.helidon.samples.petclinic.mapper.PetMapper;
import io.helidon.samples.petclinic.mapper.VisitMapper;
import io.helidon.samples.petclinic.rest.api.OwnerService;
import io.helidon.samples.petclinic.rest.dto.OwnerFieldsDto;
import io.helidon.samples.petclinic.rest.dto.PetFieldsDto;
import io.helidon.samples.petclinic.rest.dto.VisitFieldsDto;
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
public class OwnerResource implements OwnerService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final OwnerMapper ownerMapper;
    private final PetMapper petMapper;
    private final VisitMapper visitMapper;

    @Inject
    public OwnerResource(ClinicService clinicService,
                         OwnerMapper ownerMapper,
                         PetMapper petMapper,
                         VisitMapper visitMapper) {
        this.clinicService = clinicService;
        this.ownerMapper = ownerMapper;
        this.petMapper = petMapper;
        this.visitMapper = visitMapper;
    }

    @Override
    public Response addOwner(OwnerFieldsDto ownerFieldsDto) {
        var owner = ownerMapper.toOwner(ownerFieldsDto);
        clinicService.saveOwner(owner);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/owners/{id}").build(owner.getId());
        return Response.created(location).entity(ownerMapper.toOwnerDto(owner)).build();
    }

    @Override
    public Response addPetToOwner(Integer ownerId, PetFieldsDto petFieldsDto) {
        var owner = clinicService.findOwnerById(ownerId).orElseThrow();
        var pet = petMapper.toPet(petFieldsDto);
        pet.setOwner(owner);
        clinicService.savePet(pet);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/pets/{id}").build(pet.getId());
        return Response.created(location).entity(petMapper.toPetDto(pet)).build();
    }

    @Override
    public Response addVisitToOwner(Integer ownerId, Integer petId, VisitFieldsDto visitFieldsDto) {
        var visit = visitMapper.toVisit(visitFieldsDto);
        var pet = clinicService.findPetById(petId).orElseThrow();

        if (ownerId != null && !pet.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("Pet's owner doesn't correspond to ownerId parameter value.");
        }
        pet.addVisit(visit);
        this.clinicService.saveVisit(visit);
        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/visits/{id}").build(visit.getId());
        return Response.created(location).entity(visitMapper.toVisitDto(visit)).build();
    }

    @Override
    @Transactional
    public Response deleteOwner(Integer ownerId) {
        var owner = clinicService.findOwnerById(ownerId).orElseThrow(NotFoundException::new);
        clinicService.deleteOwner(owner);
        return Response.noContent().build();
    }

    @Override
    public Response getOwner(Integer ownerId) {
        var owner = clinicService.findOwnerById(ownerId).orElseThrow(NotFoundException::new);
        return Response.ok(ownerMapper.toOwnerDto(owner)).build();
    }

    @Override
    public Response getOwnersPet(Integer ownerId, Integer petId) {
        var pet = clinicService.findPetById(petId).orElseThrow(NotFoundException::new);
        if (!pet.getOwner().getId().equals(ownerId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(petMapper.toPetDto(pet)).build();
    }

    @Override
    public Response listOwners(String lastName) {
        var owners = ownerMapper.toOwnerDtos(clinicService.findAllOwners());
        if (owners.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(owners).build();
    }

    @Override
    public Response updateOwner(Integer ownerId, OwnerFieldsDto ownerFieldsDto) {
        var owner = clinicService.findOwnerById(ownerId).orElseThrow(NotFoundException::new);
        owner.setAddress(ownerFieldsDto.getAddress());
        owner.setCity(ownerFieldsDto.getCity());
        owner.setFirstName(ownerFieldsDto.getFirstName());
        owner.setLastName(ownerFieldsDto.getLastName());
        owner.setTelephone(ownerFieldsDto.getTelephone());

        this.clinicService.saveOwner(owner);
        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/owners/{id}").build(owner.getId());
        return Response.ok(ownerMapper.toOwnerDto(owner)).location(location).build();
    }

    @Override
    public Response updateOwnersPet(Integer ownerId, Integer petId, PetFieldsDto petFieldsDto) {
        var pet = clinicService.findPetById(petId).orElseThrow(NotFoundException::new);
        if (!pet.getOwner().getId().equals(ownerId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        pet.setName(petFieldsDto.getName());
        pet.setBirthDate(petFieldsDto.getBirthDate());
        if (!pet.getType().getId().equals(petFieldsDto.getType().getId())) {
            var petType = clinicService.findPetTypeById(petFieldsDto.getType().getId()).orElseThrow(NotFoundException::new);
            pet.setType(petType);
        }
        clinicService.savePet(pet);
        return Response.noContent().build();
    }
}
