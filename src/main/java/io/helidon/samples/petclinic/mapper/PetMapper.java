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

import io.helidon.samples.petclinic.model.Pet;
import io.helidon.samples.petclinic.rest.dto.PetDto;
import io.helidon.samples.petclinic.rest.dto.PetFieldsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Map Pet & PetDto using mapstruct
 */
@Mapper(uses = DtoFactory.class)
public interface PetMapper {
    PetDto toPetDto(Pet pet);

    List<PetDto> toPetDtos(List<Pet> pets);

    List<Pet> toPets(List<PetDto> pets);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Pet toPet(PetDto petDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "visits", ignore = true)
    Pet toPet(PetFieldsDto petFieldsDto);
}
