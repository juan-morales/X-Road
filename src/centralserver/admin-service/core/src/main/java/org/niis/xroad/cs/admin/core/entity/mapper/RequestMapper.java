/**
 * The MIT License
 * <p>
 * Copyright (c) 2019- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.niis.xroad.cs.admin.core.entity.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.niis.xroad.centralserver.restapi.dto.converter.GenericDtoMapper;
import org.niis.xroad.cs.admin.api.domain.AuthenticationCertificateDeletionRequest;
import org.niis.xroad.cs.admin.api.domain.AuthenticationCertificateRegistrationRequest;
import org.niis.xroad.cs.admin.api.domain.ClientDeletionRequest;
import org.niis.xroad.cs.admin.api.domain.ClientRegistrationRequest;
import org.niis.xroad.cs.admin.api.domain.OwnerChangeRequest;
import org.niis.xroad.cs.admin.api.domain.Request;
import org.niis.xroad.cs.admin.core.entity.AuthenticationCertificateDeletionRequestEntity;
import org.niis.xroad.cs.admin.core.entity.AuthenticationCertificateRegistrationRequestEntity;
import org.niis.xroad.cs.admin.core.entity.ClientDeletionRequestEntity;
import org.niis.xroad.cs.admin.core.entity.ClientRegistrationRequestEntity;
import org.niis.xroad.cs.admin.core.entity.OwnerChangeRequestEntity;
import org.niis.xroad.cs.admin.core.entity.RequestEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {RequestProcessingMapper.class, ClientIdMapper.class, SecurityServerIdMapper.class})
public interface RequestMapper extends GenericDtoMapper<RequestEntity, Request> {

    @Override
    default Request toDto(RequestEntity source) {
        if (source == null) {
            return null;
        }
        if (source instanceof AuthenticationCertificateDeletionRequestEntity) {
            return toDto((AuthenticationCertificateDeletionRequestEntity) source);
        }
        if (source instanceof AuthenticationCertificateRegistrationRequestEntity) {
            return toDto((AuthenticationCertificateRegistrationRequestEntity) source);
        }
        if (source instanceof ClientDeletionRequestEntity) {
            return toDto((ClientDeletionRequestEntity) source);
        }
        if (source instanceof ClientRegistrationRequestEntity) {
            return toDto((ClientRegistrationRequestEntity) source);
        }
        if (source instanceof OwnerChangeRequestEntity) {
            return toDto((OwnerChangeRequestEntity) source);
        }

        throw new IllegalArgumentException("Cannot map " + source.getClass());
    }

    @Override
    default RequestEntity fromDto(Request source) {
        if (source == null) {
            return null;
        }
        if (source instanceof AuthenticationCertificateDeletionRequest) {
            return fromDto((AuthenticationCertificateDeletionRequest) source);
        }
        if (source instanceof AuthenticationCertificateRegistrationRequest) {
            return fromDto((AuthenticationCertificateRegistrationRequest) source);
        }
        if (source instanceof ClientDeletionRequest) {
            return fromDto((ClientDeletionRequest) source);
        }
        if (source instanceof ClientRegistrationRequest) {
            return fromDto((ClientRegistrationRequest) source);
        }
        if (source instanceof OwnerChangeRequest) {
            return fromDto((OwnerChangeRequest) source);
        }

        throw new IllegalArgumentException("Cannot map " + source.getClass());
    }

    AuthenticationCertificateDeletionRequestEntity fromDto(AuthenticationCertificateDeletionRequest source);

    AuthenticationCertificateRegistrationRequestEntity fromDto(AuthenticationCertificateRegistrationRequest source);

    ClientDeletionRequestEntity fromDto(ClientDeletionRequest source);

    ClientRegistrationRequestEntity fromDto(ClientRegistrationRequest source);

    OwnerChangeRequestEntity fromDto(OwnerChangeRequest source);

    AuthenticationCertificateDeletionRequest toDto(AuthenticationCertificateDeletionRequestEntity source);

    AuthenticationCertificateRegistrationRequest toDto(AuthenticationCertificateRegistrationRequestEntity source);

    ClientDeletionRequest toDto(ClientDeletionRequestEntity source);

    ClientRegistrationRequest toDto(ClientRegistrationRequestEntity source);

    OwnerChangeRequest toDto(OwnerChangeRequestEntity source);
}