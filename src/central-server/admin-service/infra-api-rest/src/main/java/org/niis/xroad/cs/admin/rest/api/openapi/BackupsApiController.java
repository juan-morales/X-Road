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
package org.niis.xroad.cs.admin.rest.api.openapi;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.niis.xroad.cs.admin.api.service.TokensService;
import org.niis.xroad.cs.admin.rest.api.converter.BackupDtoConverter;
import org.niis.xroad.cs.admin.rest.api.exception.InternalServerErrorException;
import org.niis.xroad.cs.openapi.BackupsApi;
import org.niis.xroad.cs.openapi.model.BackupDto;
import org.niis.xroad.cs.openapi.model.TokensLoggedOutDto;
import org.niis.xroad.restapi.common.backup.dto.BackupFile;
import org.niis.xroad.restapi.common.backup.exception.BackupFileNotFoundException;
import org.niis.xroad.restapi.common.backup.exception.BackupInvalidFileException;
import org.niis.xroad.restapi.common.backup.exception.InvalidFilenameException;
import org.niis.xroad.restapi.common.backup.exception.RestoreProcessFailedException;
import org.niis.xroad.restapi.common.backup.service.BackupService;
import org.niis.xroad.restapi.common.backup.service.BaseConfigurationBackupGenerator;
import org.niis.xroad.restapi.common.backup.service.RestoreService;
import org.niis.xroad.restapi.config.audit.AuditEventMethod;
import org.niis.xroad.restapi.config.audit.RestApiAuditEvent;
import org.niis.xroad.restapi.exceptions.ErrorDeviation;
import org.niis.xroad.restapi.openapi.BadRequestException;
import org.niis.xroad.restapi.openapi.ControllerUtil;
import org.niis.xroad.restapi.openapi.ResourceNotFoundException;
import org.niis.xroad.restapi.service.UnhandledWarningsException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.niis.xroad.restapi.exceptions.DeviationCodes.ERROR_BACKUP_RESTORE_INTERRUPTED;
import static org.niis.xroad.restapi.exceptions.DeviationCodes.ERROR_GENERATE_BACKUP_INTERRUPTED;
import static org.springframework.http.HttpStatus.CREATED;


@Controller
@PreAuthorize("denyAll")
@RequiredArgsConstructor
@RequestMapping(ControllerUtil.API_V1_PREFIX)
public class BackupsApiController implements BackupsApi {
    private final BackupService backupService;
    private final RestoreService restoreService;
    private final TokensService tokensService;
    private final BackupDtoConverter backupDtoConverter;
    private final BaseConfigurationBackupGenerator centralServerConfigurationBackupGenerator;

    @Override
    @PreAuthorize("hasAuthority('BACKUP_CONFIGURATION')")
    public ResponseEntity<BackupDto> addBackup() {
        try {
            BackupFile backupFile = centralServerConfigurationBackupGenerator.generateBackup();
            return ResponseEntity
                    .status(CREATED)
                    .body(backupDtoConverter.toTarget(backupFile));
        } catch (InterruptedException e) {
            throw new InternalServerErrorException(new ErrorDeviation(ERROR_GENERATE_BACKUP_INTERRUPTED));
        } catch (BackupFileNotFoundException e) {
            throw new InternalServerErrorException(e.getErrorDeviation());
        }
    }

    @Override
    @PreAuthorize("hasAuthority('BACKUP_CONFIGURATION')")
    public ResponseEntity<Void> deleteBackup(String filename) {
        throw new NotImplementedException("deleteBackup not implemented yet");
    }

    @Override
    @PreAuthorize("hasAuthority('BACKUP_CONFIGURATION')")
    public ResponseEntity<Resource> downloadBackup(String filename) {
        byte[] backupFile;
        try {
            backupFile = backupService.readBackupFile(filename);
        } catch (BackupFileNotFoundException e) {
            throw new ResourceNotFoundException(e);
        }
        return ControllerUtil.createAttachmentResourceResponse(backupFile, filename);
    }

    @Override
    @PreAuthorize("hasAuthority('BACKUP_CONFIGURATION')")
    public ResponseEntity<List<BackupDto>> getBackups() {
        return ResponseEntity.ok(backupService.getBackupFiles().stream()
                .map(backupDtoConverter::toTarget)
                .collect(toList()));
    }

    @Override
    @PreAuthorize("hasAuthority('RESTORE_CONFIGURATION')")
    @AuditEventMethod(event = RestApiAuditEvent.RESTORE_BACKUP)
    public ResponseEntity<TokensLoggedOutDto> restoreBackup(String filename) {
        boolean hasHardwareTokens = tokensService.hasHardwareTokens();
        TokensLoggedOutDto tokensLoggedOut = new TokensLoggedOutDto().hsmTokensLoggedOut(hasHardwareTokens);
        try {
            restoreService.restoreFromBackup(filename);
        } catch (BackupFileNotFoundException e) {
            throw new BadRequestException(e);
        } catch (InterruptedException e) {
            throw new InternalServerErrorException(new ErrorDeviation(ERROR_BACKUP_RESTORE_INTERRUPTED));
        } catch (RestoreProcessFailedException e) {
            throw new InternalServerErrorException(e);
        }
        return new ResponseEntity<>(tokensLoggedOut, HttpStatus.OK);
    }

    @Override
    @PreAuthorize("hasAuthority('BACKUP_CONFIGURATION')")
    @AuditEventMethod(event = RestApiAuditEvent.UPLOAD_BACKUP)
    public ResponseEntity<BackupDto> uploadBackup(Boolean ignoreWarnings, MultipartFile file) {
        try {
            final BackupFile backupFile = backupService.uploadBackup(ignoreWarnings,
                    file.getOriginalFilename(), file.getBytes());
            return ResponseEntity.status(CREATED).body(backupDtoConverter.toTarget(backupFile));
        } catch (InvalidFilenameException | UnhandledWarningsException
                 | BackupInvalidFileException e) {
            throw new BadRequestException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
