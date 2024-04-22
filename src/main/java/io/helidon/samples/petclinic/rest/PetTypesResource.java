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

import io.helidon.samples.petclinic.mapper.PetTypeMapper;
import io.helidon.samples.petclinic.rest.api.PettypesService;
import io.helidon.samples.petclinic.rest.dto.PetTypeDto;
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
public class PetTypesResource implements PettypesService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final PetTypeMapper petTypeMapper;

    @Inject
    public PetTypesResource(ClinicService clinicService, PetTypeMapper petTypeMapper) {
        this.clinicService = clinicService;
        this.petTypeMapper = petTypeMapper;
    }

    @Override
    public Response addPetType(PetTypeDto petTypeDto) {
        var petType = petTypeMapper.toPetType(petTypeDto);
        clinicService.savePetType(petType);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/pettypes/{id}").build(petType.getId());
        return Response.created(location).entity(petTypeMapper.toPetTypeDto(petType)).build();
    }

    @Override
    @Transactional
    public Response deletePetType(Integer petTypeId) {
        var petType = clinicService.findPetTypeById(petTypeId).orElseThrow(NotFoundException::new);
        clinicService.deletePetType(petType);
        return Response.noContent().build();
    }

    @Override
    public Response getPetType(Integer petTypeId) {
        var petType = clinicService.findPetTypeById(petTypeId).orElseThrow(NotFoundException::new);
        return Response.ok(petTypeMapper.toPetTypeDto(petType)).build();
    }

    @Override
    public Response listPetTypes() {
        var petTypes = petTypeMapper.toPetTypeDtos(clinicService.findAllPetTypes());
        if (petTypes.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(petTypes).build();
    }

    @Override
    public Response updatePetType(Integer petTypeId, PetTypeDto petTypeDto) {
        var petType = clinicService.findPetTypeById(petTypeId).orElseThrow(NotFoundException::new);
        petType.setName(petTypeDto.getName());
        clinicService.savePetType(petType);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/pettypes/{id}").build(petType.getId());
        return Response.ok(petTypeMapper.toPetTypeDto(petType)).location(location).build();
    }
}
