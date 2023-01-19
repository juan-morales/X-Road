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
package org.niis.xroad.cs.admin.core.service;

import ee.ria.xroad.common.identifier.ClientId;
import ee.ria.xroad.common.identifier.SecurityServerId;

import lombok.RequiredArgsConstructor;
import org.niis.xroad.cs.admin.api.domain.Subsystem;
import org.niis.xroad.cs.admin.api.dto.SubsystemCreationRequest;
import org.niis.xroad.cs.admin.api.exception.EntityExistsException;
import org.niis.xroad.cs.admin.api.exception.ErrorMessage;
import org.niis.xroad.cs.admin.api.exception.NotFoundException;
import org.niis.xroad.cs.admin.api.exception.ValidationFailureException;
import org.niis.xroad.cs.admin.api.service.SubsystemService;
import org.niis.xroad.cs.admin.core.entity.SecurityServerClientNameEntity;
import org.niis.xroad.cs.admin.core.entity.ServerClientEntity;
import org.niis.xroad.cs.admin.core.entity.SubsystemEntity;
import org.niis.xroad.cs.admin.core.entity.mapper.SecurityServerClientMapper;
import org.niis.xroad.cs.admin.core.repository.SecurityServerClientNameRepository;
import org.niis.xroad.cs.admin.core.repository.SubsystemRepository;
import org.niis.xroad.cs.admin.core.repository.XRoadMemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.niis.xroad.cs.admin.api.exception.ErrorMessage.MEMBER_NOT_FOUND;
import static org.niis.xroad.cs.admin.api.exception.ErrorMessage.SUBSYSTEM_EXISTS;

@Service
@Transactional
@RequiredArgsConstructor
public class SubsystemServiceImpl implements SubsystemService {

    private final SubsystemRepository subsystemRepository;
    private final XRoadMemberRepository xRoadMemberRepository;

    private final SecurityServerClientNameRepository securityServerClientNameRepository;
    private final SecurityServerClientMapper subsystemConverter;

    @Override
    public Subsystem add(SubsystemCreationRequest request) {
        final boolean exists = subsystemRepository.findOneBy(request.getSubsystemId()).isDefined();
        if (exists) {
            throw new EntityExistsException(SUBSYSTEM_EXISTS, request.getSubsystemId().toShortString());
        }

        var persistedEntity = saveSubsystem(request);
        saveSecurityServerClientName(persistedEntity);
        return subsystemConverter.toDto(persistedEntity);
    }

    private SubsystemEntity saveSubsystem(SubsystemCreationRequest request) {
        var memberEntity = xRoadMemberRepository.findMember(request.getMemberId())
                .getOrElseThrow(() -> new NotFoundException(
                        MEMBER_NOT_FOUND,
                        "code",
                        request.getMemberId().getMemberCode()
                ));
        var subsystemEntity = new SubsystemEntity(memberEntity, request.getSubsystemId());
        return subsystemRepository.save(subsystemEntity);
    }

    private void saveSecurityServerClientName(SubsystemEntity subsystem) {
        var ssClientName = new SecurityServerClientNameEntity(subsystem.getXroadMember(), subsystem.getIdentifier());
        securityServerClientNameRepository.save(ssClientName);
    }

    @Override
    public Set<Subsystem> findByMemberIdentifier(ClientId id) {
        return xRoadMemberRepository.findMember(id)
                .getOrElseThrow(() -> new NotFoundException(ErrorMessage.MEMBER_NOT_FOUND))
                .getSubsystems().stream().map(subsystemConverter::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Subsystem> findByIdentifier(ClientId id) {
        return subsystemRepository.findByIdentifier(id).map(subsystemConverter::toDto);
    }

    @Override
    public void unregisterSubsystem(ClientId subsystemId, SecurityServerId securityServerId) {
        SubsystemEntity subsystem = subsystemRepository.findOneBy(subsystemId)
                .getOrElseThrow(() -> new NotFoundException(ErrorMessage.SUBSYSTEM_NOT_FOUND));
        ServerClientEntity serverClient = subsystem.getServerClients().stream()
                .filter(sc -> securityServerId.equals(sc.getSecurityServer().getServerId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorMessage.SUBSYSTEM_NOT_REGISTERED_TO_SECURITY_SERVER));
        subsystem.getServerClients().remove(serverClient);
    }

    @Override
    public void deleteSubsystem(ClientId subsystemClientId) {
        var subsystem = subsystemRepository.findOneBy(subsystemClientId)
                .getOrElseThrow(() -> new NotFoundException(ErrorMessage.SUBSYSTEM_NOT_FOUND));

        if (isRegistered(subsystem)) {
            throw new ValidationFailureException(ErrorMessage.SUBSYSTEM_REGISTERED_AND_CANNOT_BE_DELETED);
        }

        subsystemRepository.deleteById(subsystem.getId());
    }

    private boolean isRegistered(SubsystemEntity subsystem) {
        return !CollectionUtils.isEmpty(subsystem.getServerClients());
    }
}