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

import java.util.Calendar;

/**
 * Some often used FIX fields.
 */
public enum FieldType {

    //header fields
    MsgType(35, String.class),
    SenderCompID(49, String.class),
    TargetCompID(56, String.class),
    MsgSeqNum(34, Integer.class),
    // body fields
    TestReqID(112, String.class),

    SendingTime(52, Calendar.class),

    // trailer fields
    Signature(89, String.class),
    SignatureLength(93, Integer.class),
    CheckSum(10, Integer.class),

    // body fields
    /**
     * The tag number of the FIX field being referenced.
     */
    RefTagID(371, Integer.class),

    /**
     * The MsgType of the FIX message being referenced.
     */
    RefMsgType(372, String.class),
    /**
     * MsgSeqNum of rejected message
     */
    RefSeqNum(45, Integer.class),
    /**
     * Some descriptive text
     */
    Text(58, String.class),
    HeartBtInt(108, Integer.class),
    Username(553, String.class),
    EncryptMethod(98, Integer.class),
    Password(554, String.class),
    NewPassword(925, String.class);

    public int tag() {
        return tag;
    }

    public Class type() {
        return type;
    }

    private final int tag;
    private final Class type;

    private FieldType(int tag, Class cl) {
        this.tag = tag;
        this.type = cl;
    }
}
