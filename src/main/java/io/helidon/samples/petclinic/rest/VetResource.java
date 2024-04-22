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

import io.helidon.samples.petclinic.mapper.VetMapper;
import io.helidon.samples.petclinic.model.Specialty;
import io.helidon.samples.petclinic.rest.api.VetService;
import io.helidon.samples.petclinic.rest.dto.VetDto;
import io.helidon.samples.petclinic.service.ClinicService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequestScoped
public class VetResource implements VetService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final VetMapper vetMapper;

    @Inject
    public VetResource(ClinicService clinicService, VetMapper vetMapper) {
        this.clinicService = clinicService;
        this.vetMapper = vetMapper;
    }

    @Override
    public Response addVet(VetDto vetDto) {
        var vet = vetMapper.toVet(vetDto);
        if (vet.getNrOfSpecialties() > 0) {
            var vetSpecialities = clinicService.findSpecialtiesByNameIn(
                    vet.getSpecialties().stream().map(Specialty::getName)
                            .collect(Collectors.toSet()));
            vet.setSpecialties(vetSpecialities);
        }
        clinicService.saveVet(vet);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/vets/{id}").build(vet.getId());
        return Response.created(location).entity(vetMapper.toVetDto(vet)).build();
    }

    @Override
    @Transactional
    public Response deleteVet(Integer vetId) {
        var vet = clinicService.findVetById(vetId).orElseThrow(NotFoundException::new);
        clinicService.deleteVet(vet);
        return Response.noContent().build();
    }

    @Override
    public Response getVet(Integer vetId) {
        var vet = clinicService.findVetById(vetId).orElseThrow(NotFoundException::new);
        return Response.ok(vetMapper.toVetDto(vet)).build();
    }

    @Override
    public Response listVets() {
        var vets = vetMapper.toVetDtos(clinicService.findAllVets());
        if (vets.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(vets).build();
    }

    @Override
    public Response updateVet(Integer vetId, VetDto vetDto) {
        var vet = clinicService.findVetById(vetId).orElseThrow();
        vet.setFirstName(vetDto.getFirstName());
        vet.setLastName(vetDto.getLastName());
        vet.clearSpecialties();

        Set<Integer> specialtyIds = new HashSet<>();
        for (var specDto : vetDto.getSpecialties()) {
            specialtyIds.add(specDto.getId());
        }

        if (!specialtyIds.isEmpty()) {
            var vetSpecialities = clinicService.findSpecialtiesByIdIn(specialtyIds);
            vet.setSpecialties(vetSpecialities);
        }

        clinicService.saveVet(vet);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/vets/{id}").build(vet.getId());
        return Response.ok(vetMapper.toVetDto(vet)).location(location).build();
    }
}
