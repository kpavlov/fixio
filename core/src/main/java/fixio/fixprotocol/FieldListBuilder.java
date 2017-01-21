/*
 * Copyright 2014 The FIX.io Project
 *
 * The FIX.io Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package fixio.fixprotocol;

import fixio.fixprotocol.fields.FixedPointNumber;

/**
 * Represent a ordered list of fields.
 * <p>
 * Provides methods to add new fields.
 * </p>
 */
interface FieldListBuilder<T> {

    T add(FieldType field, String value);

    T add(FieldType field, char value);

    T add(int tagNum, String value);

    T add(DataType type, int tagNum, String value);

    T add(FieldType field, int value);

    T add(int tagNum, int value);

    T add(DataType type, int tagNum, int value);

    T add(FieldType field, long value);

    T add(int tagNum, long value);

    T add(DataType type, int tagNum, long value);

    T add(FieldType field, FixedPointNumber value);

    T add(int tagNum, FixedPointNumber value);

    T add(DataType type, int tagNum, FixedPointNumber value);

    Group newGroup(int tagNum);

    Group newGroup(int tagNum, int expectedGroupSize);

    Group newGroup(FieldType fieldType);

    Group newGroup(FieldType fieldType, int expectedGroupSize);
}
