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
package org.niis.xroad.cs.admin.application.openapi;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.niis.xroad.cs.admin.application.util.TestUtils;
import org.niis.xroad.cs.openapi.model.ClientDto;
import org.niis.xroad.cs.openapi.model.ClientIdDto;
import org.niis.xroad.cs.openapi.model.MemberGlobalGroupDto;
import org.niis.xroad.cs.openapi.model.MemberNameDto;
import org.niis.xroad.cs.openapi.model.SecurityServerDto;
import org.niis.xroad.cs.openapi.model.XRoadIdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class MembersApiTest extends AbstractApiControllerTestContext {

    private static final String PATH = "/api/v1/members";
    public static final String MEMBER_M1_CLIENT_ID = "TEST:GOV:M1";
    public static final String MEMBER_M1_NAME = "Member1";

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void addApiKeyAuthorizationHeader() {
        TestUtils.addApiKeyAuthorizationHeader(restTemplate);
    }

    @Nested
    @DisplayName("POST " + PATH)
    class AddMember {

        @Test
        @WithMockUser(authorities = {"ADD_NEW_MEMBER"})
        @DisplayName("should add member")
        public void addMember(TestInfo testInfo) {
            String memberCode = composeMemberName(testInfo);
            ClientIdDto memberIdDto =  (ClientIdDto) new ClientIdDto()
                    .memberClass("GOV")
                    .memberCode(memberCode)
                    .instanceId("TEST")
                    .type(XRoadIdDto.TypeEnum.MEMBER);
            ClientDto memberDto = new ClientDto()
                    .memberName("memberName")
                    .xroadId(memberIdDto);

            ResponseEntity<ClientDto> response = restTemplate.postForEntity(PATH, memberDto, ClientDto.class);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("memberName", response.getBody().getMemberName());
            assertEquals(memberIdDto, response.getBody().getXroadId());
        }
    }

    @Nested
    @DisplayName("GET " + PATH + "/{id}")
    @WithMockUser(authorities = {"VIEW_MEMBER_DETAILS"})
    class GetMember {
        private final ClientIdDto expectedMemberId = (ClientIdDto) new ClientIdDto()
                .memberClass("GOV")
                .memberCode("M1")
                .instanceId("TEST")
                .type(XRoadIdDto.TypeEnum.MEMBER);

        @Test
        void getMember() {
            var uriVariables = Map.of("clientId", MEMBER_M1_CLIENT_ID);

            var response = restTemplate.getForEntity(
                    PATH + "/{clientId}",
                    ClientDto.class,
                    uriVariables);

            assertNotNull(response);
            assertEquals(200, response.getStatusCodeValue());
            assertNotNull(response.getBody());
            assertEquals(MEMBER_M1_NAME, response.getBody().getMemberName());
            assertEquals(expectedMemberId, response.getBody().getXroadId());
        }
    }

    @Nested
    @DisplayName("DELETE " + PATH + "/{id}")
    @WithMockUser(authorities = {"DELETE_MEMBER"})
    class DeleteMember {

        @Test
        void deleteMember() {
            ClientIdDto memberIdDto = (ClientIdDto) new ClientIdDto()
                    .memberClass("GOV")
                    .memberCode("to-be-deleted")
                    .instanceId("TEST")
                    .type(XRoadIdDto.TypeEnum.MEMBER);
            ClientDto memberDto = new ClientDto()
                    .memberName("To Be Deleted")
                    .xroadId(memberIdDto);
            restTemplate.postForEntity(PATH, memberDto, ClientDto.class);
            var uriVariables = Map.of("clientId", "TEST:GOV:to-be-deleted");

            var resp = restTemplate.exchange(
                    PATH + "/{clientId}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, uriVariables
            );
            assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());

            ResponseEntity<ClientDto> deletedClientResponse =
                    restTemplate.getForEntity("/api/v1/clients/{clientId}", ClientDto.class, uriVariables);

            assertNotNull(deletedClientResponse.getBody());
            assertEquals(NOT_FOUND, deletedClientResponse.getStatusCode());
        }
    }

    @Nested
    @DisplayName("PATCH" + PATH + "/{id}")
    @WithMockUser(authorities = {"EDIT_MEMBER_NAME_AND_ADMIN_CONTACT"})
    class UpdateMemberName {

        @Test
        void updateMemberName() {
            var timeBeforeUpdate = OffsetDateTime.now();

            ClientDto response = doUpdateName("new name");

            assertNotNull(response);
            assertEquals("new name", response.getMemberName());
            assertTrue(response.getUpdatedAt().isAfter(timeBeforeUpdate), "Expecting updatedAt to be updated");
        }

        private ClientDto doUpdateName(String newName) {
            var uriVariables = Map.of("clientId", MEMBER_M1_CLIENT_ID);

            return restTemplate.patchForObject(
                    PATH + "/{clientId}",
                    new MemberNameDto().memberName(newName),
                    ClientDto.class,
                    uriVariables);
        }

        @AfterEach
        void revertNameUpdate() {
            doUpdateName(MEMBER_M1_NAME);
        }
    }

    @Nested
    @DisplayName("GET" + PATH + "/{id}/global-groups")
    @WithMockUser(authorities = {"VIEW_MEMBER_DETAILS"})
    class GetMemberGlobalGroups {

        @Test
        void shouldReturnMemberGlobalGroups() {
            var response = restTemplate.getForEntity(
                    PATH + "/{clientId}/global-groups",
                    MemberGlobalGroupDto[].class,
                    MEMBER_M1_CLIENT_ID);

            final MemberGlobalGroupDto[] result = response.getBody();
            assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            assertEquals(2, ArrayUtils.getLength(result));

            final Map<String, MemberGlobalGroupDto> resultMap = Arrays.stream(result)
                    .collect(Collectors.toMap(MemberGlobalGroupDto::getGroupCode, Function.identity()));

            assertEquals("SS1", resultMap.get("CODE_1").getSubsystem());
            assertTrue(resultMap.containsKey("CODE_2"));
        }

        @Test
        void shouldReturnNoResultsForNotExistingMember() {
            var response = restTemplate.getForEntity(
                    PATH + "/{clientId}/global-groups",
                    MemberGlobalGroupDto[].class,
                    "not:existing:member");
            final MemberGlobalGroupDto[] result = response.getBody();
            assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            assertTrue(ArrayUtils.isEmpty(result));
        }
    }

    @Nested
    @DisplayName("GET" + PATH + "/{id}/owned-servers")
    @WithMockUser(authorities = {"VIEW_MEMBER_DETAILS"})
    class GetMemberOwnedServers {

        @Test
        void shouldReturnMemberOwnedServers() {
            var response = restTemplate.getForEntity(
                    PATH + "/{clientId}/owned-servers",
                    SecurityServerDto[].class,
                    MEMBER_M1_CLIENT_ID);

            final SecurityServerDto[] result = response.getBody();
            assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            assertEquals(1, ArrayUtils.getLength(result));
            assertEquals("server1", result[0].getXroadId().getServerCode());
        }

        @Test
        void shouldReturnNoResultsForNotExistingMember() {
            var response = restTemplate.getForEntity(
                    PATH + "/{clientId}/owned-servers",
                    MemberGlobalGroupDto[].class,
                    "not:existing:member");
            final MemberGlobalGroupDto[] result = response.getBody();
            assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
            assertTrue(ArrayUtils.isEmpty(result));
        }
    }

    private static String composeMemberName(TestInfo testInfo) {
        String testClassSimpleName = StringUtils.substringAfterLast(testInfo.getTestClass().get().getName(), ".");
        String testMethodName = testInfo.getTestMethod().get().getName();
        return testClassSimpleName + "." + testMethodName;
    }

}
