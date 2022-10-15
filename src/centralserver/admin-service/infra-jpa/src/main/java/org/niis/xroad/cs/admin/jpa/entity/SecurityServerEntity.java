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

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = SecurityServerEntity.TABLE_NAME)
public class SecurityServerEntity extends AuditableEntity {

    public static final String TABLE_NAME = "security_servers";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_NAME + "_id_seq")
    @SequenceGenerator(name = TABLE_NAME + "_id_seq", sequenceName = TABLE_NAME + "_id_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    @Access(AccessType.FIELD)
    @Getter
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @Access(AccessType.FIELD)
    @Getter
    @Setter
    private XRoadMemberEntity owner;

    @Column(name = "server_code")
    @NotNull
    @Getter
    @Setter
    private String serverCode;

    @Column(name = "address")
    @NotNull
    @Getter
    @Setter
    private String address;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "securityServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Access(AccessType.FIELD)
    @Getter
    private Set<AuthCertEntity> authCerts = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "securityServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Access(AccessType.FIELD)
    @Getter
    private Set<SecurityServerSecurityCategoryEntity> securityServerSecurityCategories = new HashSet<>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "securityServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Access(AccessType.FIELD)
    @Getter
    private Set<ServerClientEntity> serverClients = new HashSet<>(0);

    protected SecurityServerEntity() {
        //JPA
    }

    public SecurityServerEntity(XRoadMemberEntity owner, String serverCode) {
        this.owner = owner;
        this.serverCode = serverCode;
    }

    public void addClient(SecurityServerClient client) {
        var serverClient = new ServerClientEntity(this, client);
        serverClients.add(serverClient);
        //todo: is two-way mapping necessary.
        client.getServerClients().add(serverClient);
    }

    @Transient
    public SecurityServerIdEntity getServerId() {
        ClientIdEntity identifier = owner.getIdentifier();
        return SecurityServerIdEntity.create(identifier, serverCode);
    }

}


