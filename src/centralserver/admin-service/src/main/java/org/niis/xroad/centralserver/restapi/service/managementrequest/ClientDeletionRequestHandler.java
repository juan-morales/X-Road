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
package org.niis.xroad.centralserver.restapi.service.managementrequest;

import ee.ria.xroad.common.identifier.SecurityServerId;

import lombok.RequiredArgsConstructor;
import org.niis.xroad.centralserver.restapi.dto.ClientDeletionRequestDto;
import org.niis.xroad.centralserver.restapi.entity.ClientDeletionRequest;
import org.niis.xroad.centralserver.restapi.entity.SecurityServerClient;
import org.niis.xroad.centralserver.restapi.repository.IdentifierRepository;
import org.niis.xroad.centralserver.restapi.repository.RequestRepository;
import org.niis.xroad.centralserver.restapi.repository.SecurityServerClientRepository;
import org.niis.xroad.centralserver.restapi.repository.SecurityServerRepository;
import org.niis.xroad.centralserver.restapi.service.exception.DataIntegrityException;
import org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientDeletionRequestHandler implements RequestHandler<ClientDeletionRequestDto, ClientDeletionRequest> {

    private final SecurityServerClientRepository<SecurityServerClient> clients;
    private final SecurityServerRepository servers;
    private final IdentifierRepository<SecurityServerId> serverIds;
    private final RequestRepository<ClientDeletionRequest> requests;

    @Override
    public boolean canAutoApprove(ClientDeletionRequest request) {
        return false;
    }

    @Override
    public ClientDeletionRequest add(ClientDeletionRequestDto request) {

        var server = servers.findBy(request.getServerId(), request.getClientId()).orElseThrow(() ->
                new DataIntegrityException(ErrorMessage.MANAGEMENT_REQUEST_CLIENT_REGISTRATION_NOT_FOUND));

        //todo: somewhat inefficient, could also directly delete the association entity
        var client = clients.findOneBy(request.getClientId()).orElseThrow();
        for (var it = client.getServerClients().iterator(); it.hasNext(); ) {
            var item = it.next();
            if (item.getSecurityServer() == server) {
                it.remove();
                break;
            }
        }

        /*
         * Note. The legacy implementation revokes existing pending registration requests. However, that does
         * not seem right since if a request is pending, there is no registration that could be deleted,
         * and if there is a registration, there can not be pending requests (unless there is a concurrency issue
         * that should be addressed).
         */

        return requests.save(new ClientDeletionRequest(request.getOrigin(), serverIds.merge(request.getServerId()),
                client.getIdentifier()));
    }

    @Override
    public ClientDeletionRequest approve(ClientDeletionRequest request) {
        //nothing to do.
        return request;
    }

    @Override
    public Class<ClientDeletionRequest> requestType() {
        return ClientDeletionRequest.class;
    }

    @Override
    public Class<ClientDeletionRequestDto> dtoType() {
        return ClientDeletionRequestDto.class;
    }

}