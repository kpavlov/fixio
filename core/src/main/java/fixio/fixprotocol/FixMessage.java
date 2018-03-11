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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents incoming FIX Protocol message.
 */
public interface FixMessage {

    String FIX_4_0 = "FIX.4.0";
    String FIX_4_1 = "FIX.4.1";
    String FIX_4_2 = "FIX.4.2";
    String FIX_4_3 = "FIX.4.3";
    String FIX_4_4 = "FIX.4.4";
    String FIX_5_0 = "FIXT.1.1";

    FixMessageHeader getHeader();

    List<FixMessageFragment> getBody();

    Integer getInt(int tagNum);

    Integer getInt(FieldType field);

    String getString(int tagNum);

    @SuppressWarnings("unchecked")
    <T> T getValue(int tagNum);

    String getString(FieldType field);

    <T> T getValue(FieldType field);

    Character getChar(int tagNum);

    Character getChar(FieldType fieldType);

    String getMessageType();

    default FixMessageFragment getFirst(int tagNum) {
        final List<FixMessageFragment> body = getBody();
        for (int i = 0; i < body.size(); i++) {
            FixMessageFragment item = body.get(i);
            if (item.getTagNum() == tagNum) {
                return item;
            }
        }
        return null;
    }

    default List<FixMessageFragment> getAll(int tagNum) {
        final List<FixMessageFragment> body = getBody();
        List<FixMessageFragment> result = new ArrayList<>(8);
        for (int i = 0; i < body.size(); i++) {
            FixMessageFragment item = body.get(i);
            if (item.getTagNum() == tagNum) {
                result.add(item);
            }
        }
        return result;
    }

    default FixMessageFragment getLast(int tagNum) {
        final List<FixMessageFragment> body = getBody();
        for (int i = body.size() - 1; i >= 0; i--) {
            FixMessageFragment item = body.get(i);
            if (item.getTagNum() == tagNum) {
                return item;
            }
        }
        return null;
    }

}
