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
package io.helidon.samples.petclinic.mapper;

import io.helidon.samples.petclinic.model.*;
import io.helidon.samples.petclinic.rest.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import org.mapstruct.ObjectFactory;

import java.util.ArrayList;

@ApplicationScoped
public class DtoFactory {
    @ObjectFactory
    public VetDto createVetDto(Vet vet) {
        return new VetDto(vet.getId());
    }

    @ObjectFactory
    public OwnerDto createOwnerDto(Owner owner) {
        return new OwnerDto(owner.getId(), new ArrayList<>());
    }

    @ObjectFactory
    public VisitDto createVisitDto(Visit visit) {
        return new VisitDto(visit.getId(), visit.getPet().getId());
    }

    @ObjectFactory
    public SpecialtyDto createSpecialtyDto(Specialty specialty) {
        return new SpecialtyDto(specialty.getId());
    }

    @ObjectFactory
    public PetTypeDto createPetTypeDto(PetType petType) {
        return new PetTypeDto(petType.getId());
    }

    @ObjectFactory
    public PetDto createPetDto(Pet pet) {
        return new PetDto(pet.getId(), pet.getOwner().getId(), new ArrayList<>());
    }
}