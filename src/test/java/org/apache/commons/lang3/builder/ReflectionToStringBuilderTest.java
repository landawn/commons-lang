/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.builder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;

import org.apache.commons.lang3.AbstractLangTest;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ReflectionToStringBuilder}.
 */
class ReflectionToStringBuilderTest extends AbstractLangTest {

    @Test
    void testFieldNameGettersWithoutLists() {
        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(new Object());
        try {
            assertNull(builder.getIncludeFieldNames());
            assertNull(builder.getExcludeFieldNames());
            builder.setIncludeFieldNames("included").setExcludeFieldNames("excluded");
            builder.getIncludeFieldNames()[0] = "changed";
            builder.getExcludeFieldNames()[0] = "changed";
            assertArrayEquals(new String[] { "included" }, builder.getIncludeFieldNames());
            assertArrayEquals(new String[] { "excluded" }, builder.getExcludeFieldNames());
            builder.setIncludeFieldNames((String[]) null).setExcludeFieldNames((String[]) null);
            assertNull(builder.getIncludeFieldNames());
            assertNull(builder.getExcludeFieldNames());
        } finally {
            builder.toString();
        }
    }

    @Test
    void testRegistryReleasedAfterFieldAccessFailure() {
        final Object value = new Object() {
            private final int field = 1;
        };
        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(value) {
            @Override
            protected Object getValue(final Field field) {
                throw new IllegalStateException("test failure");
            }
        };
        try {
            assertThrows(IllegalStateException.class, builder::toString);
            assertFalse(ToStringStyle.isRegistered(value));
        } finally {
            ToStringStyle.unregister(value);
        }
    }

    @Test
    void testConstructorWithNullObject() {
        assertEquals("<null>", new ReflectionToStringBuilder(null, ToStringStyle.DEFAULT_STYLE, new StringBuffer()).toString());
    }

}
