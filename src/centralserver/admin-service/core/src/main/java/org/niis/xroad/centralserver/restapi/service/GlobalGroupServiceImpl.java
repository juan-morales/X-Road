/**
 * The MIT License
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
package org.niis.xroad.centralserver.restapi.service;

import lombok.RequiredArgsConstructor;
import org.niis.xroad.centralserver.restapi.service.exception.DataIntegrityException;
import org.niis.xroad.centralserver.restapi.service.exception.NotFoundException;
import org.niis.xroad.centralserver.restapi.service.exception.ValidationFailureException;
import org.niis.xroad.cs.admin.api.domain.GlobalGroup;
import org.niis.xroad.cs.admin.api.domain.GlobalGroupMember;
import org.niis.xroad.cs.admin.api.dto.GlobalGroupUpdateDto;
import org.niis.xroad.cs.admin.api.service.GlobalGroupService;
import org.niis.xroad.cs.admin.core.entity.SystemParameterEntity;
import org.niis.xroad.cs.admin.core.entity.mapper.GlobalGroupMapper;
import org.niis.xroad.cs.admin.core.entity.mapper.GlobalGroupMemberMapper;
import org.niis.xroad.cs.admin.core.repository.GlobalGroupMemberRepository;
import org.niis.xroad.cs.admin.core.repository.GlobalGroupRepository;
import org.niis.xroad.cs.admin.core.repository.SystemParameterRepository;
import org.niis.xroad.restapi.config.audit.AuditDataHelper;
import org.niis.xroad.restapi.config.audit.RestApiAuditProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.niis.xroad.centralserver.restapi.service.SystemParameterServiceImpl.SECURITY_SERVER_OWNERS_GROUP;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.GLOBAL_GROUP_EXISTS;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.GLOBAL_GROUP_NOT_FOUND;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.OWNERS_GLOBAL_GROUP_CANNOT_BE_DELETED;

@SuppressWarnings("checkstyle:RegexpSingleline")
@Service
@Transactional
@RequiredArgsConstructor
public class GlobalGroupServiceImpl implements GlobalGroupService {
    private final AuditDataHelper auditDataHelper;
    private final StableSortHelperImpl stableSortHelper;
    private final GlobalGroupRepository globalGroupRepository;
    private final SystemParameterRepository systemParameterRepository;
    private final GlobalGroupMemberRepository globalGroupMemberRepository;
    private final GlobalGroupMapper globalGroupMapper;
    private final GlobalGroupMemberMapper globalGroupMemberMapper;

    public List<GlobalGroup> findGlobalGroups() {
        return globalGroupRepository.findAll().stream()
                .map(globalGroupMapper::toDto)
                .collect(Collectors.toList());
    }

    public GlobalGroup addGlobalGroup(GlobalGroup globalGroup) {
        assertGlobalGroupExists(globalGroup.getGroupCode());

        var globalGroupEntity = globalGroupMapper.fromDto(globalGroup);
        globalGroupEntity = globalGroupRepository.save(globalGroupEntity);
        addAuditData(globalGroup);

        return globalGroupMapper.toDto(globalGroupEntity);
    }

    public GlobalGroup getGlobalGroup(Integer groupId) {
        return findGlobalGroupOrThrowException(groupId);
    }

    public void deleteGlobalGroup(Integer groupId) {
        handleInternalDelete(findGlobalGroupOrThrowException(groupId));
    }

    public GlobalGroup updateGlobalGroupDescription(GlobalGroupUpdateDto updateDto) {
        GlobalGroup globalGroup = findGlobalGroupOrThrowException(updateDto.getGroupId());
        return handleInternalUpdate(globalGroup, updateDto);
    }

    public List<GlobalGroupMember> getGroupMembersFilterModel(Integer groupId) {
        return globalGroupMemberRepository.findByGlobalGroupId(groupId).stream()
                .map(globalGroupMemberMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<GlobalGroupMember> findGroupMembers(GlobalGroupService.Criteria criteria, Pageable pageable) {
        return globalGroupMemberRepository.findAll(criteria, stableSortHelper.addSecondaryIdSort(pageable))
                .map(globalGroupMemberMapper::toDto);
    }

    private GlobalGroup findGlobalGroupOrThrowException(Integer groupId) {
        return globalGroupRepository.findById(groupId)
                .map(globalGroupMapper::toDto)
                .orElseThrow(() -> new NotFoundException(GLOBAL_GROUP_NOT_FOUND));
    }

    private void assertGlobalGroupExists(String code) {
        globalGroupRepository.getByGroupCode(code)
                .ifPresent(globalGroup -> {
                    throw new DataIntegrityException(GLOBAL_GROUP_EXISTS);
                });
    }

    private void handleInternalDelete(GlobalGroup entity) {
        List<SystemParameterEntity> ownersGroupCode = systemParameterRepository.findByKey(SECURITY_SERVER_OWNERS_GROUP);
        if (isOwnersGroup(ownersGroupCode, entity.getGroupCode())) {
            throw new ValidationFailureException(OWNERS_GLOBAL_GROUP_CANNOT_BE_DELETED);
        }
        addAuditData(entity);
        globalGroupRepository.deleteById(entity.getId());
    }

    private GlobalGroup handleInternalUpdate(GlobalGroup globalGroup, GlobalGroupUpdateDto updateDto) {
        globalGroup.setDescription(updateDto.getDescription());
        addAuditData(globalGroup);

        var globalGroupEntity = globalGroupMapper.fromDto(globalGroup);
        globalGroupEntity = globalGroupRepository.save(globalGroupEntity);
        return globalGroupMapper.toDto(globalGroupEntity);
    }

    private boolean isOwnersGroup(List<SystemParameterEntity> ownersGroupCode, String groupCode) {
        return ownersGroupCode.stream()
                .map(SystemParameterEntity::getValue)
                .anyMatch(code -> code.equalsIgnoreCase(groupCode));
    }

    private void addAuditData(GlobalGroup entity) {
        auditDataHelper.put(RestApiAuditProperty.CODE, entity.getGroupCode());
        auditDataHelper.put(RestApiAuditProperty.DESCRIPTION, entity.getDescription());
    }
}