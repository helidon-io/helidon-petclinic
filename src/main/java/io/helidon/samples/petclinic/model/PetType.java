/*
 * Copyright 2002-2013 the original author or authors.
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
package io.helidon.samples.petclinic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * @author Juergen Hoeller
 *         Can be Cat, Dog, Hamster...
 */
@Entity
@Table(name = "types")
@NamedQueries({
        @NamedQuery(name = "findAllPetTypes",
                query = "SELECT pt FROM PetType pt"),
        @NamedQuery(name = "getPetTypeByName",
                query = "SELECT pt FROM PetType pt WHERE pt.name = :name")
})
public class PetType extends NamedEntity {

}
