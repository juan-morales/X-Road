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
package org.niis.xroad.centralserver.restapi.dto.converter.db;

import ee.ria.xroad.common.junit.helper.WithInOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.niis.xroad.centralserver.openapi.model.AuthenticationCertificateDeletionRequestDto;
import org.niis.xroad.centralserver.openapi.model.AuthenticationCertificateRegistrationRequestDto;
import org.niis.xroad.centralserver.openapi.model.ClientDeletionRequestDto;
import org.niis.xroad.centralserver.openapi.model.ClientIdDto;
import org.niis.xroad.centralserver.openapi.model.ClientRegistrationRequestDto;
import org.niis.xroad.centralserver.openapi.model.ManagementRequestDto;
import org.niis.xroad.centralserver.openapi.model.ManagementRequestOriginDto;
import org.niis.xroad.centralserver.openapi.model.ManagementRequestStatusDto;
import org.niis.xroad.centralserver.openapi.model.ManagementRequestTypeDto;
import org.niis.xroad.centralserver.restapi.converter.db.ClientIdDtoConverter;
import org.niis.xroad.centralserver.restapi.converter.db.ManagementRequestDtoConverter;
import org.niis.xroad.centralserver.restapi.converter.model.ManagementRequestDtoTypeConverter;
import org.niis.xroad.centralserver.restapi.converter.model.ManagementRequestOriginDtoConverter;
import org.niis.xroad.centralserver.restapi.converter.model.ManagementRequestStatusConverter;
import org.niis.xroad.centralserver.restapi.domain.ManagementRequestStatus;
import org.niis.xroad.centralserver.restapi.domain.ManagementRequestType;
import org.niis.xroad.centralserver.restapi.domain.Origin;
import org.niis.xroad.centralserver.restapi.dto.converter.AbstractDtoConverterTest;
import org.niis.xroad.cs.admin.api.domain.AuthenticationCertificateDeletionRequest;
import org.niis.xroad.cs.admin.api.domain.AuthenticationCertificateRegistrationRequest;
import org.niis.xroad.cs.admin.api.domain.ClientDeletionRequest;
import org.niis.xroad.cs.admin.api.domain.ClientRegistrationRequest;
import org.niis.xroad.cs.admin.api.domain.MemberId;
import org.niis.xroad.cs.admin.api.domain.Request;
import org.niis.xroad.cs.admin.api.domain.SecurityServerId;
import org.niis.xroad.restapi.converter.SecurityServerIdConverter;
import org.niis.xroad.restapi.openapi.BadRequestException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.niis.xroad.centralserver.openapi.model.ManagementRequestTypeDto.AUTH_CERT_DELETION_REQUEST;
import static org.niis.xroad.centralserver.openapi.model.ManagementRequestTypeDto.AUTH_CERT_REGISTRATION_REQUEST;
import static org.niis.xroad.centralserver.openapi.model.ManagementRequestTypeDto.CLIENT_DELETION_REQUEST;
import static org.niis.xroad.centralserver.openapi.model.ManagementRequestTypeDto.CLIENT_REGISTRATION_REQUEST;

@ExtendWith(MockitoExtension.class)
public class ManagementRequestDtoConverterTest extends AbstractDtoConverterTest implements WithInOrder {
    private final byte[] authCertBytes = "authCertBytes".getBytes();
    private final Origin origin = Origin.SECURITY_SERVER;
    private final ManagementRequestOriginDto originDto = ManagementRequestOriginDto.SECURITY_SERVER;

    @Mock
    private MemberId clientId;
    @Mock
    private ClientIdDto clientIdDto;
    @Mock
    private SecurityServerId securityServerId;

    @Mock
    private ManagementRequestOriginDtoConverter.Service originDtoMapper;
    @Mock
    private SecurityServerIdConverter securityServerIdMapper;
    @Mock
    private ManagementRequestStatusConverter.Service statusMapper;
    @Mock
    private ClientIdDtoConverter clientIdDtoMapper;
    @Spy
    private ManagementRequestDtoTypeConverter.Service requestTypeConverter = new ManagementRequestDtoTypeConverter.Service();

    @InjectMocks
    private ManagementRequestDtoConverter converter;

    @BeforeEach
    private void setZoneOffset() {
        ReflectionTestUtils.setField(converter, "dtoZoneOffset", dtoZoneOffset);
    }

    @Nested
    @DisplayName("toDto(Request request)")
    public class ToDtoMethod {

        private static final int ID = 1;
        private final ManagementRequestStatus managementRequestStatus = ManagementRequestStatus.APPROVED;
        private final ManagementRequestStatusDto managementRequestStatusDto = ManagementRequestStatusDto.APPROVED;

        @Test
        @DisplayName("should successfully perform  AuthenticationCertificateRegistrationRequest conversion")
        public void testAuthenticationCertificateRegistrationRequestConversion() {
            AuthenticationCertificateRegistrationRequest request = mock(AuthenticationCertificateRegistrationRequest.class);
            doReturn(SERVER_ADDRESS).when(request).getAddress();
            doReturn(authCertBytes).when(request).getAuthCert();
            prepareCommonStubs(request, ManagementRequestType.AUTH_CERT_REGISTRATION_REQUEST);

            ManagementRequestDto converted = converter.toDto(request);

            assertCommon(converted, AUTH_CERT_REGISTRATION_REQUEST);
            AuthenticationCertificateRegistrationRequestDto casted =
                    assertInstanceOf(AuthenticationCertificateRegistrationRequestDto.class, converted);
            assertEquals(authCertBytes, casted.getAuthenticationCertificate());
            assertEquals(SERVER_ADDRESS, casted.getServerAddress());
            inOrder(request).verify(inOrder -> {
                inOrder.verify(request).getAddress();
                inOrder.verify(request).getAuthCert();
                verifyCommon(inOrder, request);
            });
        }

        @Test
        @DisplayName("should successfully perform  AuthenticationCertificateDeletionRequest conversion")
        public void testAuthenticationCertificateDeletionRequestConversion() {
            AuthenticationCertificateDeletionRequest request = mock(AuthenticationCertificateDeletionRequest.class);
            doReturn(authCertBytes).when(request).getAuthCert();
            prepareCommonStubs(request, ManagementRequestType.AUTH_CERT_DELETION_REQUEST);

            ManagementRequestDto converted = converter.toDto(request);

            assertCommon(converted, AUTH_CERT_DELETION_REQUEST);
            AuthenticationCertificateDeletionRequestDto casted =
                    assertInstanceOf(AuthenticationCertificateDeletionRequestDto.class, converted);
            assertEquals(authCertBytes, casted.getAuthenticationCertificate());
            inOrder(request).verify(inOrder -> {
                inOrder.verify(request).getAuthCert();
                verifyCommon(inOrder, request);
            });
        }

        @Test
        @DisplayName("should successfully perform  ClientRegistrationRequest conversion")
        public void testClientRegistrationRequestConversion() {
            ClientRegistrationRequest request = mock(ClientRegistrationRequest.class);
            doReturn(clientId).when(request).getClientId();
            doReturn(clientIdDto).when(clientIdDtoMapper).toDto(clientId);
            prepareCommonStubs(request, ManagementRequestType.CLIENT_REGISTRATION_REQUEST);

            ManagementRequestDto converted = converter.toDto(request);

            assertCommon(converted, CLIENT_REGISTRATION_REQUEST);
            ClientRegistrationRequestDto casted =
                    assertInstanceOf(ClientRegistrationRequestDto.class, converted);
            assertEquals(clientIdDto, casted.getClientId());
            inOrder(request).verify(inOrder -> {
                inOrder.verify(request).getClientId();
                inOrder.verify(clientIdDtoMapper).toDto(clientId);
                verifyCommon(inOrder, request);
            });
        }

        @Test
        @DisplayName("should successfully perform ClientDeletionRequest conversion")
        public void testClientDeletionRequestConversion() {
            ClientDeletionRequest request = mock(ClientDeletionRequest.class);
            doReturn(clientId).when(request).getClientId();
            doReturn(clientIdDto).when(clientIdDtoMapper).toDto(clientId);
            prepareCommonStubs(request, ManagementRequestType.CLIENT_DELETION_REQUEST);

            ManagementRequestDto converted = converter.toDto(request);

            assertCommon(converted, CLIENT_DELETION_REQUEST);
            ClientDeletionRequestDto casted =
                    assertInstanceOf(ClientDeletionRequestDto.class, converted);
            assertEquals(clientIdDto, casted.getClientId());
            inOrder(request).verify(inOrder -> {
                inOrder.verify(request).getClientId();
                inOrder.verify(clientIdDtoMapper).toDto(clientId);
                verifyCommon(inOrder, request);
            });
        }

        @Test
        @DisplayName("should fail for unknown request type")
        public void shouldFailForUnknownRequest() {
            Request illegalRequest = mock(Request.class);

            Executable testable = () -> converter.toDto(illegalRequest);

            BadRequestException actualThrown = assertThrows(BadRequestException.class, testable);
            assertEquals("Unknown request type", actualThrown.getMessage());
            inOrder(illegalRequest).verifyNoMoreInteractions();
        }

        private void prepareCommonStubs(Request request, ManagementRequestType type) {
            doReturn(ID).when(request).getId();
            doReturn(origin).when(request).getOrigin();
            doReturn(type).when(request).getManagementRequestType();
            doReturn(originDto).when(originDtoMapper).toDto(origin);
            doReturn(securityServerId).when(request).getSecurityServerId();
            doReturn("SECURITY_SERVER_ID").when(securityServerIdMapper).convertId(securityServerId);
            doReturn(managementRequestStatus).when(request).getProcessingStatus();
            doReturn(managementRequestStatusDto).when(statusMapper).toDto(managementRequestStatus);
            doReturn(createdAtInstance).when(request).getCreatedAt();
            doReturn(updatedAtInstance).when(request).getUpdatedAt();
        }

        private void assertCommon(ManagementRequestDto requestDto, ManagementRequestTypeDto type) {
            assertNotNull(requestDto);
            assertEquals(ID, requestDto.getId());
            assertEquals(originDto, requestDto.getOrigin());
            assertEquals("SECURITY_SERVER_ID", requestDto.getSecurityServerId());
            assertEquals(type, requestDto.getType());
            assertEquals(managementRequestStatusDto, requestDto.getStatus());
            assertEquals(createdAtOffsetDateTime, requestDto.getCreatedAt());
            assertEquals(updatedAtOffsetDateTime, requestDto.getUpdatedAt());
        }

        private void verifyCommon(org.mockito.InOrder inOrder, Request request) {
            inOrder.verify(request).getId();
            inOrder.verify(request).getManagementRequestType();
            inOrder.verify(request).getOrigin();
            inOrder.verify(originDtoMapper).toDto(origin);
            inOrder.verify(request).getSecurityServerId();
            inOrder.verify(securityServerIdMapper).convertId(securityServerId);
            inOrder.verify(request).getProcessingStatus();
            inOrder.verify(statusMapper).toDto(managementRequestStatus);
            inOrder.verify(request).getCreatedAt();
            inOrder.verify(request).getUpdatedAt();
        }
    }

    @Nested
    @DisplayName("fromDto(Request request)")
    public class FromDtoMethod implements WithInOrder {

        @Test
        @DisplayName("should successfully perform AuthenticationCertificateRegistrationRequestDto conversion")
        public void shouldSuccessfullyPerformAuthenticationCertificateRegistrationRequestDtoConversion() {
            AuthenticationCertificateRegistrationRequestDto requestDto = mock(AuthenticationCertificateRegistrationRequestDto.class);
            prepareCommonStubs(requestDto);
            doReturn(authCertBytes).when(requestDto).getAuthenticationCertificate();
            doReturn(SERVER_ADDRESS).when(requestDto).getServerAddress();

            Request converted = converter.fromDto(requestDto);

            assertCommon(converted);
            AuthenticationCertificateRegistrationRequest request =
                    assertInstanceOf(AuthenticationCertificateRegistrationRequest.class, converted);
            assertEquals(authCertBytes, request.getAuthCert());
            assertEquals(SERVER_ADDRESS, request.getAddress());
            inOrder(requestDto).verify(inOrder -> {
                verifyCommon(inOrder, requestDto);
                inOrder.verify(requestDto).getAuthenticationCertificate();
                inOrder.verify(requestDto).getServerAddress();
            });
        }

        @Test
        @DisplayName("should successfully perform AuthenticationCertificateDeletionRequestDto conversion")
        public void shouldSuccessfullyPerformAuthenticationCertificateDeletionRequestDtoConversion() {
            AuthenticationCertificateDeletionRequestDto requestDto = mock(AuthenticationCertificateDeletionRequestDto.class);
            prepareCommonStubs(requestDto);
            doReturn(authCertBytes).when(requestDto).getAuthenticationCertificate();

            Request converted = converter.fromDto(requestDto);

            assertCommon(converted);
            AuthenticationCertificateDeletionRequest request =
                    assertInstanceOf(AuthenticationCertificateDeletionRequest.class, converted);
            assertArrayEquals(authCertBytes, request.getAuthCert());
            inOrder(requestDto).verify(inOrder -> {
                verifyCommon(inOrder, requestDto);
                inOrder.verify(requestDto).getAuthenticationCertificate();
            });
        }

        @Test
        @DisplayName("should successfully perform ClientRegistrationRequestDto conversion")
        public void shouldSuccessfullyPerformClientRegistrationRequestDtoConversion() {
            ClientRegistrationRequestDto requestDto = mock(ClientRegistrationRequestDto.class);
            prepareCommonStubs(requestDto);
            doReturn(clientIdDto).when(requestDto).getClientId();
            doReturn(clientId).when(clientIdDtoMapper).fromDto(clientIdDto);

            Request converted = converter.fromDto(requestDto);

            assertCommon(converted);
            ClientRegistrationRequest request =
                    assertInstanceOf(ClientRegistrationRequest.class, converted);
            assertEquals(clientId, request.getClientId());
            inOrder(requestDto).verify(inOrder -> {
                verifyCommon(inOrder, requestDto);
                inOrder.verify(requestDto).getClientId();
                inOrder.verify(clientIdDtoMapper).fromDto(clientIdDto);
            });
        }

        @Test
        @DisplayName("should successfully perform ClientDeletionRequestDto conversion")
        public void shouldSuccessfullyPerformClientDeletionRequestDtoConversion() {
            ClientDeletionRequestDto requestDto = mock(ClientDeletionRequestDto.class);
            prepareCommonStubs(requestDto);
            doReturn(clientIdDto).when(requestDto).getClientId();
            doReturn(clientId).when(clientIdDtoMapper).fromDto(clientIdDto);

            Request converted = converter.fromDto(requestDto);

            assertCommon(converted);
            ClientDeletionRequest request =
                    assertInstanceOf(ClientDeletionRequest.class, converted);
            assertEquals(clientId, request.getClientId());
            inOrder(requestDto).verify(inOrder -> {
                verifyCommon(inOrder, requestDto);
                inOrder.verify(requestDto).getClientId();
                inOrder.verify(clientIdDtoMapper).fromDto(clientIdDto);
            });
        }

        private void prepareCommonStubs(ManagementRequestDto requestDto) {
            doReturn(originDto).when(requestDto).getOrigin();
            doReturn(origin).when(originDtoMapper).fromDto(originDto);
            doReturn("SECURITY_SERVER_ID").when(requestDto).getSecurityServerId();
            doReturn(securityServerId).when(securityServerIdMapper).convertId("SECURITY_SERVER_ID");
        }

        private void assertCommon(Request request) {
            assertNotNull(request);
            assertEquals(securityServerId, request.getSecurityServerId());
            assertEquals(origin, request.getOrigin());
        }

        private void verifyCommon(org.mockito.InOrder inOrder, ManagementRequestDto requestDto) {
            inOrder.verify(requestDto).getOrigin();
            inOrder.verify(originDtoMapper).fromDto(originDto);
            inOrder.verify(requestDto).getSecurityServerId();
            inOrder.verify(securityServerIdMapper).convertId("SECURITY_SERVER_ID");
        }
    }

}
