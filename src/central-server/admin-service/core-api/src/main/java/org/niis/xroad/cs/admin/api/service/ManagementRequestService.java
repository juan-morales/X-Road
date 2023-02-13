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
package org.niis.xroad.cs.admin.api.service;

import ee.ria.xroad.common.identifier.SecurityServerId;

import lombok.Builder;
import lombok.Getter;
import org.niis.xroad.common.managementrequest.model.ManagementRequestType;
import org.niis.xroad.cs.admin.api.domain.ManagementRequestStatus;
import org.niis.xroad.cs.admin.api.domain.Origin;
import org.niis.xroad.cs.admin.api.domain.Request;
import org.niis.xroad.cs.admin.api.dto.ManagementRequestInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ManagementRequestService {

    @Builder
    @Getter
    class Criteria {
        private final String query;
        private final Origin origin;
        private final List<ManagementRequestType> types;
        private final ManagementRequestStatus status;
        private final SecurityServerId serverId;
    }

    /**
     * Get a management request
     *
     * @param id request id
     */
    Optional<Request> getRequest(int id);

    ManagementRequestType getRequestType(int id);

    /**
     * Find management requests matching criteria.
     */
    Page<ManagementRequestInfoDto> findRequests(
            ManagementRequestService.Criteria filter,
            Pageable page);

    /**
     * Add new management request
     */
    <T extends Request> T add(T request);

    /**
     * Approve pending management request
     *
     * @param requestId request id to approve
     */
    <T extends Request> T approve(int requestId);

    /**
     * Revoke (decline) pending management request
     *
     * @param requestId request id to revoke
     */
    void revoke(Integer requestId);
}