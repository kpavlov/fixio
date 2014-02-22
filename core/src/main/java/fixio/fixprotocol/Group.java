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

import fixio.fixprotocol.fields.StringField;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents FIX Protocol field Group or Component - a sequence of Fields or other Groups.
 */
public class Group {

    public static final int DEFAULT_GROUP_SIZE = 8;
    private final ArrayList<FixMessageFragment> contents;

    public Group(int expectedSize) {
        this.contents = new ArrayList<>(expectedSize);
    }

    public Group() {
        this.contents = new ArrayList<>(DEFAULT_GROUP_SIZE);
    }

    public void add(FixMessageFragment element) {
        contents.add(element);
    }

    public Group add(FieldType fieldType, String value) {
        contents.add(new StringField(fieldType.tag(), value));
        return this;
    }

    public Group add(int tagNum, String value) {
        assert (tagNum > 0) : "Tag must be positive.";
        assert (value != null) : "Value  must be specified.";
        contents.add(new StringField(tagNum, value));
        return this;
    }

    public List<FixMessageFragment> getContents() {
        return contents;
    }

}
