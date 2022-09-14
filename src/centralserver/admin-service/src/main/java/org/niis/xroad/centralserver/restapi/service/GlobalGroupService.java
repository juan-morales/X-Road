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
import org.niis.xroad.centralserver.openapi.model.GlobalGroupCodeAndDescriptionDto;
import org.niis.xroad.centralserver.openapi.model.GlobalGroupResourceDto;
import org.niis.xroad.centralserver.openapi.model.GroupMembersFilterModelDto;
import org.niis.xroad.centralserver.restapi.converter.GlobalGroupConverter;
import org.niis.xroad.centralserver.restapi.converter.GroupMemberFilterModelConverter;
import org.niis.xroad.centralserver.restapi.dto.GlobalGroupUpdateDto;
import org.niis.xroad.centralserver.restapi.entity.GlobalGroup;
import org.niis.xroad.centralserver.restapi.entity.GlobalGroupMember;
import org.niis.xroad.centralserver.restapi.entity.SystemParameter;
import org.niis.xroad.centralserver.restapi.repository.GlobalGroupMemberRepository;
import org.niis.xroad.centralserver.restapi.repository.GlobalGroupRepository;
import org.niis.xroad.centralserver.restapi.repository.SystemParameterRepository;
import org.niis.xroad.centralserver.restapi.service.exception.DataIntegrityException;
import org.niis.xroad.centralserver.restapi.service.exception.NotFoundException;
import org.niis.xroad.centralserver.restapi.service.exception.ValidationFailureException;
import org.niis.xroad.restapi.config.audit.AuditDataHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.niis.xroad.centralserver.restapi.service.SystemParameterService.SECURITY_SERVER_OWNERS_GROUP;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.GLOBAL_GROUP_EXISTS;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.GLOBAL_GROUP_NOT_FOUND;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.OWNERS_GLOBAL_GROUP_CANNOT_BE_DELETED;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.CODE;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.DESCRIPTION;

@SuppressWarnings("checkstyle:RegexpSingleline")
@Service
@Transactional
@RequiredArgsConstructor
public class GlobalGroupService {
    private final AuditDataHelper auditDataHelper;
    private final StableSortHelper stableSortHelper;
    private final GlobalGroupRepository globalGroupRepository;
    private final GlobalGroupConverter globalGroupConverter;
    private final GroupMemberFilterModelConverter groupMemberFilterModelConverter;
    private final SystemParameterRepository systemParameterRepository;
    private final GlobalGroupMemberRepository globalGroupMemberRepository;

    public Set<GlobalGroupResourceDto> findGlobalGroups() {
        return globalGroupRepository.findAll().stream()
                .map(globalGroupConverter::convert)
                .collect(Collectors.toSet());
    }

    public GlobalGroupResourceDto addGlobalGroup(GlobalGroupCodeAndDescriptionDto codeAndDescription) {
        assertGlobalGroupExists(codeAndDescription.getCode());
        var globalGroupEntity = globalGroupConverter.toEntity(codeAndDescription);
        var persistedGlobalGroup = globalGroupRepository.save(globalGroupEntity);
        addAuditData(persistedGlobalGroup);
        return globalGroupConverter.convert(persistedGlobalGroup);
    }

    public GlobalGroupResourceDto getGlobalGroup(Integer groupId) {
        GlobalGroup globalGroup = findGlobalGroupOrThrowException(groupId);
        return globalGroupConverter.convert(globalGroup);
    }

    public void deleteGlobalGroup(Integer groupId) {
        handleInternalDelete(findGlobalGroupOrThrowException(groupId));
    }

    public GlobalGroupResourceDto updateGlobalGroupDescription(GlobalGroupUpdateDto updateDto) {
        GlobalGroup globalGroup = findGlobalGroupOrThrowException(updateDto.getGroupId());
        GlobalGroup updatedGlobalGroup = handleInternalUpdate(globalGroup, updateDto);
        return globalGroupConverter.convert(updatedGlobalGroup);
    }

    public GroupMembersFilterModelDto getGroupMembersFilterModel(Integer groupId) {
        return groupMemberFilterModelConverter.convert(globalGroupMemberRepository.findByGlobalGroupId(groupId));
    }

    public Page<GlobalGroupMember> findGroupMembers(GlobalGroupMemberRepository.Criteria criteria, Pageable pageable) {
        return globalGroupMemberRepository.findAll(GlobalGroupMemberRepository.findSpecification(criteria),
                stableSortHelper.addSecondaryIdSort(pageable));
    }

    private GlobalGroup findGlobalGroupOrThrowException(Integer groupId) {
        return globalGroupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(GLOBAL_GROUP_NOT_FOUND));
    }

    private void assertGlobalGroupExists(String code) {
        globalGroupRepository.getByGroupCode(code)
                .ifPresent(globalGroup -> {
                    throw new DataIntegrityException(GLOBAL_GROUP_EXISTS);
                });
    }

    private void handleInternalDelete(GlobalGroup entity) {
        List<SystemParameter> ownersGroupCode = systemParameterRepository.findByKey(SECURITY_SERVER_OWNERS_GROUP);
        if (isOwnersGroup(ownersGroupCode, entity.getGroupCode())) {
            throw new ValidationFailureException(OWNERS_GLOBAL_GROUP_CANNOT_BE_DELETED);
        }
        addAuditData(entity);
        globalGroupRepository.deleteById(entity.getId());
    }

    private GlobalGroup handleInternalUpdate(GlobalGroup entity, GlobalGroupUpdateDto updateDto) {
        entity.setDescription(updateDto.getDescription());
        addAuditData(entity);
        return globalGroupRepository.save(entity);
    }

    private boolean isOwnersGroup(List<SystemParameter> ownersGroupCode, String groupCode) {
        return ownersGroupCode.stream()
                .map(SystemParameter::getValue)
                .anyMatch(code -> code.equalsIgnoreCase(groupCode));
    }

    private void addAuditData(GlobalGroup entity) {
        auditDataHelper.put(CODE, entity.getGroupCode());
        auditDataHelper.put(DESCRIPTION, entity.getDescription());
    }
}
