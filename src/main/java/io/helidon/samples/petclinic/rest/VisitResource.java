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

import io.helidon.samples.petclinic.mapper.VisitMapper;
import io.helidon.samples.petclinic.rest.api.VisitService;
import io.helidon.samples.petclinic.rest.dto.VisitDto;
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
public class VisitResource implements VisitService {
    @Context
    UriInfo uriInfo;

    private final ClinicService clinicService;
    private final VisitMapper visitMapper;

    @Inject
    public VisitResource(ClinicService clinicService, VisitMapper visitMapper) {
        this.clinicService = clinicService;
        this.visitMapper = visitMapper;
    }

    @Override
    public Response addVisit(VisitDto visitDto) {
        var pet = clinicService.findPetById(visitDto.getPetId()).orElseThrow();
        var visit = visitMapper.toVisit(visitDto);
        pet.addVisit(visit);
        clinicService.saveVisit(visit);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/visits/{id}").build(visit.getId());
        return Response.created(location).entity(visitMapper.toVisitDto(visit)).build();
    }

    @Override
    @Transactional
    public Response deleteVisit(Integer visitId) {
        var visit = clinicService.findVisitById(visitId).orElseThrow(NotFoundException::new);
        clinicService.deleteVisit(visit);
        return Response.noContent().build();
    }

    @Override
    public Response getVisit(Integer visitId) {
        var visit = clinicService.findVisitById(visitId).orElseThrow(NotFoundException::new);
        return Response.ok(visitMapper.toVisitDto(visit)).build();
    }

    @Override
    public Response listVisits() {
        var visits = visitMapper.toVisitDtos(clinicService.findAllVisits());
        if (visits.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(visits).build();
    }

    @Override
    public Response updateVisit(Integer visitId, VisitDto visitDto) {
        var visit = clinicService.findVisitById(visitId).orElseThrow(NotFoundException::new);
        visit.setDate(visitDto.getDate());
        visit.setDescription(visitDto.getDescription());
        clinicService.saveVisit(visit);

        var location = UriBuilder.fromUri(uriInfo.getBaseUri()).path("api/visits/{id}").build(visit.getId());
        return Response.ok(visitMapper.toVisitDto(visit)).location(location).build();
    }
}
