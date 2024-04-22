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

import io.helidon.samples.petclinic.mapper.SpecialtyMapper;
import io.helidon.samples.petclinic.rest.api.SpecialtyService;
import io.helidon.samples.petclinic.rest.dto.SpecialtyDto;
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
public class SpecialtyResource implements SpecialtyService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final SpecialtyMapper specialtyMapper;

    @Inject
    public SpecialtyResource(ClinicService clinicService, SpecialtyMapper specialtyMapper) {
        this.clinicService = clinicService;
        this.specialtyMapper = specialtyMapper;
    }

    @Override
    public Response addSpecialty(SpecialtyDto specialtyDto) {
        var specialty = specialtyMapper.toSpecialty(specialtyDto);
        clinicService.saveSpecialty(specialty);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/specialities/{id}").build(specialty.getId());
        return Response.created(location).entity(specialtyMapper.toSpecialtyDto(specialty)).build();
    }

    @Override
    @Transactional
    public Response deleteSpecialty(Integer specialtyId) {
        var specialty = clinicService.findSpecialtyById(specialtyId).orElseThrow(NotFoundException::new);
        clinicService.deleteSpecialty(specialty);
        return Response.noContent().build();
    }

    @Override
    public Response getSpecialty(Integer specialtyId) {
        var specialty = clinicService.findSpecialtyById(specialtyId).orElseThrow(NotFoundException::new);
        return Response.ok(specialtyMapper.toSpecialtyDto(specialty)).build();
    }

    @Override
    public Response listSpecialties() {
        var specialties = specialtyMapper.toSpecialtyDtos(clinicService.findAllSpecialties());
        if (specialties.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(specialties).build();
    }

    @Override
    public Response updateSpecialty(Integer specialtyId, SpecialtyDto specialtyDto) {
        var specialty = clinicService.findSpecialtyById(specialtyId).orElseThrow(NotFoundException::new);
        specialty.setName(specialtyDto.getName());
        clinicService.saveSpecialty(specialty);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/specialities/{id}").build(specialty.getId());
        return Response.ok(specialtyMapper.toSpecialtyDto(specialty)).location(location).build();
    }
}
