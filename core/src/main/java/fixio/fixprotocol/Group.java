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

import java.util.Collection;
import java.util.LinkedHashMap;

public class Group extends FixMessageFragment {

    private final LinkedHashMap<Integer, FixMessageFragment> contents = new LinkedHashMap<>();

    public Group(int tagNum) {
        super(tagNum);
    }

    public Group(FieldType fieldType) {
        super(fieldType.tag());
    }

    public String getValue() {
        return String.valueOf(contents.size());
    }

    public void add(FixMessageFragment element) {
        contents.put(element.getTagNum(), element);
    }

    public Group add(FieldType fieldType, String value) {
        return add(fieldType.tag(), value);
    }

    public Group add(int tagNum, String value) {
        assert (tagNum > 0) : "Tag must be positive.";
        assert (value != null) : "Value  must be specified.";

        contents.put(tagNum, new Field(tagNum, value));

        return this;
    }

    public Collection<FixMessageFragment> getContents() {
        return contents.values();
    }

}
