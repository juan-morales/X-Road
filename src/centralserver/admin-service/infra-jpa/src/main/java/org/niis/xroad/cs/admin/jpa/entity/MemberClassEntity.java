/**
 * The MIT License
 *
 * Copyright (c) 2019- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.cs.admin.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.niis.xroad.cs.admin.jpa.entity.validation.EntityIdentifier;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = MemberClassEntity.TABLE_NAME)
public class MemberClassEntity extends AuditableEntity implements EntityExistsAwareEntity.ByIdInt {

    public static final String TABLE_NAME = "member_classes";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + "_id_seq")
    @SequenceGenerator(name = TABLE_NAME + "_id_seq", sequenceName = TABLE_NAME + "_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @Access(AccessType.FIELD)
    @Getter
    private int id;

    @NotNull
    @EntityIdentifier
    @Column(name = "code", updatable = false)
    @Access(AccessType.FIELD)
    @Getter
    private String code;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    protected MemberClassEntity() {
        //JPA
    }

    public MemberClassEntity(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
