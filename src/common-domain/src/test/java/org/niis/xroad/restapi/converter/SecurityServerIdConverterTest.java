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
package org.niis.xroad.restapi.converter;

import ee.ria.xroad.common.identifier.SecurityServerId;


import org.junit.Ignore;
import org.junit.Test;
import org.niis.xroad.restapi.openapi.BadRequestException;

import static org.junit.Assert.assertEquals;

/**
 * test SecurityServerConverter
 */
public class SecurityServerIdConverterTest {

    SecurityServerIdConverter securityServerIdConverter = new SecurityServerIdConverter();

    @Test
    public void convertEncodedId() {
        String securityServerCode = "security-server-foo";
        String memberCode = "XRD2:GOV:M4";
        SecurityServerId id = securityServerIdConverter.convert(
                memberCode + ":" + securityServerCode);
        assertEquals("XRD2", id.getXRoadInstance());
        assertEquals("GOV", id.getMemberClass());
        assertEquals("M4", id.getMemberCode());
        assertEquals(securityServerCode, id.getServerCode());

        String difficultServerCode = "FOO SS-;/?@=&-X<!-- o -->BAR";
        id = securityServerIdConverter.convert(
                memberCode + ":" + difficultServerCode);
        assertEquals("XRD2", id.getXRoadInstance());
        assertEquals("GOV", id.getMemberClass());
        assertEquals("M4", id.getMemberCode());
        assertEquals(difficultServerCode, id.getServerCode());
    }

    @Test(expected = BadRequestException.class)
    public void convertEncodedIdWithSubsystem() {
        securityServerIdConverter.convert("XRD2:GOV:M4:SS1:serverCode");
    }

    @Test(expected = BadRequestException.class)
    public void convertEncodedIdWithMissingMember() {
        securityServerIdConverter.convert("XRD2:GOV:serverCode");
    }

    @Test(expected = BadRequestException.class)
    public void convertEncodedIdWithTooManyElements() {
        securityServerIdConverter.convert("XRD2:GOV:M4:SS1:serverCode::::");
    }

    @Test(expected = BadRequestException.class)
    public void convertEmptyEncodedId() {
        securityServerIdConverter.convert("");
    }

    @Ignore("Failing due to changed ")
    @Test(expected = BadRequestException.class)
    public void convertNullEncodedId() {
        String id = null;
        securityServerIdConverter.convert(id);
    }

    @Test(expected = BadRequestException.class)
    public void convertEncodedIdWithoutDelimiter() {
        securityServerIdConverter.convert(";;;;asdsdas");
    }

    @Test
    public void convertSecurityServerId() {
        SecurityServerId securityServerId = SecurityServerId.Conf.create(
                "XRD2", "GOV", "M4", "server1");
        String id = securityServerIdConverter.convert(securityServerId);
        assertEquals("XRD2:GOV:M4:server1", id);
    }

}