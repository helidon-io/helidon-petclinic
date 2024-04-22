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

import io.helidon.samples.petclinic.model.Vet;
import io.helidon.samples.petclinic.rest.dto.VetDto;
import io.helidon.samples.petclinic.rest.dto.VetFieldsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Collection;
import java.util.List;

/**
 * Map Vet & VetoDto using mapstruct
 */
@Mapper(uses = {DtoFactory.class, SpecialtyMapper.class})
public interface VetMapper {
    VetMapper INSTANCE = Mappers.getMapper(VetMapper.class);

    Vet toVet(VetDto vetDto);

    @Mapping(target = "id", ignore = true)
    Vet toVet(VetFieldsDto vetFieldsDto);

    VetDto toVetDto(Vet vet);

    List<VetDto> toVetDtos(Collection<Vet> vets);
}
