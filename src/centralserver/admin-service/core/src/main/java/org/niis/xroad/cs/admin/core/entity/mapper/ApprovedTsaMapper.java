/*
 * The MIT License
 *
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

package org.niis.xroad.cs.admin.core.entity.mapper;

import ee.ria.xroad.common.util.CertUtils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.niis.xroad.centralserver.restapi.dto.converter.CertificateConverter;
import org.niis.xroad.centralserver.restapi.dto.converter.GenericUniDirectionalMapper;
import org.niis.xroad.centralserver.restapi.service.exception.ValidationFailureException;
import org.niis.xroad.cs.admin.api.domain.ApprovedTsa;
import org.niis.xroad.cs.admin.core.entity.ApprovedTsaEntity;

import java.security.cert.X509Certificate;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.INVALID_CERTIFICATE;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE,
        uses = {CertificateConverter.class})
public interface ApprovedTsaMapper extends GenericUniDirectionalMapper<ApprovedTsaEntity, ApprovedTsa> {

    @Override
    @Mapping(target = "certificate", source = "cert")
    @Mapping(target = "timestampingInterval", constant = "60") // TODO stub value. Will be implemented in separate story
    @Mapping(target = "cost", constant = "FREE") // TODO stub value. Will be implemented in separate story
    ApprovedTsa toTarget(ApprovedTsaEntity approvedTsaEntity);

    default ApprovedTsaEntity toEntity(String url, byte[] certificate) {
        try {
            final X509Certificate cert = CertUtils.readCertificateChain(certificate)[0];
            final var entity = new ApprovedTsaEntity();
            entity.setUrl(url);
            entity.setCert(certificate);
            entity.setValidFrom(cert.getNotBefore().toInstant());
            entity.setValidTo(cert.getNotAfter().toInstant());
            entity.setName(CertUtils.getSubjectCommonName(cert));
            return entity;
        } catch (Exception e) {
            throw new ValidationFailureException(INVALID_CERTIFICATE);
        }
    }
}