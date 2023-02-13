/**
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
package org.niis.xroad.cs.admin.api.service;

import io.vavr.control.Option;
import org.niis.xroad.cs.admin.api.domain.MemberClass;
import org.niis.xroad.cs.admin.api.exception.DataIntegrityException;
import org.niis.xroad.cs.admin.api.exception.NotFoundException;

import java.util.List;

/**
 * MemberClass Service
 */
public interface MemberClassService {

    /**
     * List all member classes
     */
    List<MemberClass> findAll();

    /**
     * Find a member class corresponding to the code
     *
     * @param code member class code
     */
    Option<MemberClass> findByCode(String code);

    /**
     * Add a new member class
     *
     * @param memberClass member class to add
     * @throws DataIntegrityException if the member class already exists
     */
    MemberClass add(MemberClass memberClass);

    /**
     * Update member class
     *
     * @throws NotFoundException if the member class does not exist
     */
    MemberClass update(MemberClass memberClass);

    /**
     * Delete member class.
     *
     * @param code member class code
     * @throws DataIntegrityException if the member class is in use
     * @throws NotFoundException      if the member class does not exist
     */
    void delete(String code);
}