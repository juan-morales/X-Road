/*
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
package ee.ria.xroad.signer.protocol.dto;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tiny container class to help handle the key list
 */
@RequiredArgsConstructor
public final class KeyInfo implements Serializable {

    private final KeyInfoProto message;

    public boolean isAvailable() {
        return message.getAvailable();
    }

    public KeyUsageInfo getUsage() {
        return message.getUsage();
    }

    public String getFriendlyName() {
        return message.getFriendlyName();
    }

    public String getId() {
        return message.getId();
    }

    public String getLabel() {
        return message.getLabel();
    }

    public String getPublicKey() {
        return message.getPublicKey();
    }

    public List<CertificateInfo> getCerts() {
        return message.getCertsList().stream()
                .map(CertificateInfo::new)
                .collect(Collectors.toList());
    }

    public List<CertRequestInfo> getCertRequests() {
        return message.getCertRequestsList().stream()
                .map(CertRequestInfo::new)
                .collect(Collectors.toList());
    }

    public String getSignMechanismName() {
        return message.getSignMechanismName();
    }

    public boolean isForSigning() {
        return getUsage() == KeyUsageInfo.SIGNING;
    }

    /**
     * Logic to determine if a token is saved to configuration.
     * True if there are some cert requests, or there is at least one certificate which is saved to configuration.
     * (logic originally from token_renderer.rb#key_saved_to_configuration)
     */
    public boolean isSavedToConfiguration() {
        if (!getCertRequests().isEmpty()) {
            return true;
        }
        return getCerts().stream()
                .anyMatch(CertificateInfo::isSavedToConfiguration);

    }

    public KeyInfoProto asMessage() {
        return message;
    }
}
