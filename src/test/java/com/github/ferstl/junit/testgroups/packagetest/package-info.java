/*
 * Copyright (c) 2013 Stefan Ferstl <st.ferstl@gmail.com>
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
/**
 * Test package to make integration tests for package annotations.
 * See {@link com.github.ferstl.junit.testgroups.IntegrationTest}.
 */
@TestGroup(key = TEST_GROUP_KEY, value = TEST_GROUP_NAME)
package com.github.ferstl.junit.testgroups.packagetest;
import com.github.ferstl.junit.testgroups.TestGroup;

import static com.github.ferstl.junit.testgroups.packagetest.PackageTest.TEST_GROUP_KEY;
import static com.github.ferstl.junit.testgroups.packagetest.PackageTest.TEST_GROUP_NAME;

