/*
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

package org.niis.xroad.cs.test.glue;

import io.cucumber.java.en.Step;
import org.niis.xroad.cs.openapi.model.ClientDto;
import org.niis.xroad.cs.openapi.model.ClientIdDto;
import org.niis.xroad.cs.test.api.FeignSubsystemsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.apache.commons.lang3.StringUtils.split;
import static org.niis.xroad.cs.openapi.model.XRoadIdDto.TypeEnum.SUBSYSTEM;
import static org.springframework.http.HttpStatus.CREATED;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SubsystemsApiStepDefs extends BaseStepDefs {

    @Autowired
    private FeignSubsystemsApi subsystemsApi;

    @SuppressWarnings("checkstyle:MagicNumber")
    @Step("new subsystem {string} is added")
    public void createSubsystem(String subsystemId) {
        final String[] idParts = split(subsystemId, ':');

        final ClientIdDto clientIdDto = new ClientIdDto()
                .memberClass(idParts[1])
                .memberCode(idParts[2])
                .subsystemCode(idParts[3]);
        clientIdDto.setType(SUBSYSTEM);
        clientIdDto.setInstanceId(idParts[0]);

        final ClientDto dto = new ClientDto()
                .xroadId(clientIdDto);

        final ResponseEntity<ClientDto> response = subsystemsApi.addSubsystem(dto);

        validate(response)
                .assertion(equalsStatusCodeAssertion(CREATED))
                .execute();
    }


}