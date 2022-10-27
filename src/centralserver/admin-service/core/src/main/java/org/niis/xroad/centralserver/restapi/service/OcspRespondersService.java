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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niis.xroad.centralserver.restapi.dto.CertificateDetails;
import org.niis.xroad.centralserver.restapi.dto.OcspResponder;
import org.niis.xroad.centralserver.restapi.dto.OcspResponderModifyRequest;
import org.niis.xroad.centralserver.restapi.dto.converter.CaInfoConverter;
import org.niis.xroad.centralserver.restapi.dto.converter.OcspResponderConverter;
import org.niis.xroad.centralserver.restapi.entity.OcspInfo;
import org.niis.xroad.centralserver.restapi.repository.OcspInfoJpaRepository;
import org.niis.xroad.centralserver.restapi.service.exception.NotFoundException;
import org.niis.xroad.restapi.config.audit.AuditDataHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ee.ria.xroad.common.util.CryptoUtils.DEFAULT_CERT_HASH_ALGORITHM_ID;
import static ee.ria.xroad.common.util.CryptoUtils.calculateCertHexHashDelimited;
import static org.niis.xroad.centralserver.restapi.service.exception.ErrorMessage.CERTIFICATION_SERVICE_NOT_FOUND;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.OCSP_CERT_HASH;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.OCSP_CERT_HASH_ALGORITHM;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.OCSP_ID;
import static org.niis.xroad.restapi.config.audit.RestApiAuditProperty.OCSP_URL;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OcspRespondersService {
    private final OcspInfoJpaRepository ocspInfoRepository;
    private final CaInfoConverter caInfoConverter;
    private final OcspResponderConverter ocspResponderConverter;

    private final AuditDataHelper auditDataHelper;

    public CertificateDetails getOcspResponderCertificateDetails(Integer id) {
        return ocspInfoRepository.findById(id)
                .map(OcspInfo::getCaInfo)
                .map(caInfoConverter::toCertificateDetails)
                .orElseThrow(() -> new NotFoundException(CERTIFICATION_SERVICE_NOT_FOUND));
    }

    private OcspInfo get(Integer id) {
        return ocspInfoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CERTIFICATION_SERVICE_NOT_FOUND));
    }

    public OcspResponder update(OcspResponderModifyRequest updateRequest) {
        final OcspInfo ocspInfo = get(updateRequest.getId());
        if (StringUtils.isNotBlank(updateRequest.getUrl())) {
            ocspInfo.setUrl(updateRequest.getUrl());
        }
        if (updateRequest.getCertificate() != null) {
            ocspInfo.setCert(updateRequest.getCertificate());
        }
        final OcspInfo savedOcspInfo = ocspInfoRepository.save(ocspInfo);

        auditDataHelper.put(OCSP_ID, savedOcspInfo.getId());
        auditDataHelper.put(OCSP_URL, savedOcspInfo.getUrl());
        auditDataHelper.put(OCSP_CERT_HASH, calculateCertHexHashDelimited(savedOcspInfo.getCert()));
        auditDataHelper.put(OCSP_CERT_HASH_ALGORITHM, DEFAULT_CERT_HASH_ALGORITHM_ID);

        return ocspResponderConverter.toModel(savedOcspInfo);
    }
}
