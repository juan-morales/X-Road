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
package org.niis.xroad.centralserver.restapi.dto;

import ee.ria.xroad.common.identifier.SecurityServerId;

import lombok.Getter;
import org.niis.xroad.centralserver.restapi.domain.ManagementRequestStatus;
import org.niis.xroad.centralserver.restapi.domain.ManagementRequestType;
import org.niis.xroad.centralserver.restapi.domain.Origin;

@Getter
public class AuthenticationCertificateRegistrationRequestDto extends ManagementRequestDto {
    private final byte[] authCert;
    private final String address;

    public AuthenticationCertificateRegistrationRequestDto(Integer id, Origin origin, SecurityServerId serverId,
                                                           ManagementRequestStatus status, byte[] authCert,
                                                           String address) {
        super(id, ManagementRequestType.AUTH_CERT_REGISTRATION_REQUEST, origin, serverId, status);
        this.authCert = authCert;
        this.address = address;
    }

    public AuthenticationCertificateRegistrationRequestDto(Origin origin, SecurityServerId serverId,
            byte[] authCert, String address) {
        this(null, origin, serverId, null, authCert, address);
    }
}