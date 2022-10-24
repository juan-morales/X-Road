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
package org.niis.xroad.cs.test.glue;

import com.nortal.test.asserts.Assertion;
import com.nortal.test.asserts.Validation;
import feign.FeignException;
import io.cucumber.java.en.Then;
import org.niis.xroad.cs.test.api.FeignSecurityServersApi;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityServerApiStepDefs extends BaseStepDefs {
    @Autowired
    private FeignSecurityServersApi securityServersApi;

    @Then("Security server auth certs for {string} is requested")
    public void systemStatusIsRequested(String id) {
        try {
            var response = securityServersApi.getSecurityServerAuthCerts(id);
            scenarioContext.putStepData("responseCode", response.getStatusCodeValue());
        } catch (FeignException feignException) {

            scenarioContext.putStepData("responseCode", feignException.status());
        }
    }

    @Then("Response is of status code {int}")
    public void systemStatusIsValidated(int statusCode) {
        int responseCode = scenarioContext.getStepData("responseCode");

        final Validation.Builder validationBuilder = new Validation.Builder()
                .context(responseCode)
                .title("Validate response")
                .assertion(
                        new Assertion.Builder()
                                .message("Verify status code")
                                .expression("=")
                                .actualValue(responseCode)
//                                .expressionType(ExpressionType.ABSOLUTE)
                                .expectedValue(statusCode)
                                .build());


        validationService.validate(validationBuilder.build());
    }
}