/**
 * The MIT License
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
package org.niis.xroad.centralserver.restapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.niis.xroad.centralserver.restapi.service.exception.DataIntegrityException;
import org.niis.xroad.centralserver.restapi.service.exception.EntityExistsException;
import org.niis.xroad.centralserver.restapi.service.exception.NotFoundException;
import org.niis.xroad.centralserver.restapi.service.exception.ValidationFailureException;
import org.niis.xroad.restapi.config.audit.AuditEventLoggingFacade;
import org.niis.xroad.restapi.exceptions.ExceptionTranslator;
import org.niis.xroad.restapi.exceptions.SpringInternalExceptionHandler;
import org.niis.xroad.restapi.openapi.model.ErrorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Application exception handler.
 * Some exception occurrences do not get processed by this class, but by
 * {@link SpringInternalExceptionHandler instead}
 */
@Slf4j
@ControllerAdvice
public class CentralServerAdminExceptionHandler {

    public static final String EXCEPTION_CAUGHT = "exception caught";

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private AuditEventLoggingFacade auditEventLoggingFacade;

    @ExceptionHandler(DataIntegrityException.class)
    public ResponseEntity<ErrorInfo> exception(DataIntegrityException exception) {
        auditEventLoggingFacade.auditLogFail(exception);
        log.error(EXCEPTION_CAUGHT, exception);
        return exceptionTranslator.toResponseEntity(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorInfo> exception(EntityExistsException exception) {
        auditEventLoggingFacade.auditLogFail(exception);
        log.error(EXCEPTION_CAUGHT, exception);
        return exceptionTranslator.toResponseEntity(exception, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> exception(NotFoundException exception) {
        auditEventLoggingFacade.auditLogFail(exception);
        log.error(EXCEPTION_CAUGHT, exception);
        return exceptionTranslator.toResponseEntity(exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<ErrorInfo> exception(ValidationFailureException exception) {
        auditEventLoggingFacade.auditLogFail(exception);
        log.error(EXCEPTION_CAUGHT, exception);
        return exceptionTranslator.toResponseEntity(exception, HttpStatus.BAD_REQUEST);
    }
}