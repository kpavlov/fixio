/*
 * Copyright 2013 The FIX.io Project
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

public class Party {

    String compID;
    String subID;
    String locationID;
    Role role;

    public static enum Role {

        SENDER(49, 50, 142),
        TARGET(56, 57, 143),
        ON_BEHALF_OF(115, 116, 144),
        DELIVER_TO(128, 129, 145);

        private final int compIdField;
        private final int subIdField;
        private final int locationIdField;

        private Role(int compIdField, int subIdField, int locationIdField) {
            this.compIdField = compIdField;
            this.subIdField = subIdField;
            this.locationIdField = locationIdField;
        }
    }


}
