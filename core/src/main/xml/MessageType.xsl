<?xml version="1.0" encoding="UTF-8" ?>
<!--
  ~ Copyright 2014 The FIX.io Project
  ~
  ~ The FIX.io Project licenses this file to you under the Apache License,
  ~ version 2.0 (the "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at:
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  ~ WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  ~ License for the specific language governing permissions and limitations
  ~ under the License.
  -->
<stylesheet version="1.0"
            xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="text" indent="no" standalone="yes" media-type="text/java" omit-xml-declaration="yes"/>
    <strip-space elements="*"/>

    <!--XHTML document outline-->
    <template match="fix">
        /*
        * Copyright 2014 The FIX.io Project
        *
        * The FIX.io Project licenses this file to you under the Apache License,
        * version 2.0 (the "License"); you may not use this file except in compliance
        * with the License. You may obtain a copy of the License at:
        *
        * http://www.apache.org/licenses/LICENSE-2.0
        *
        * Unless required by applicable law or agreed to in writing, software
        * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
        * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
        * License for the specific language governing permissions and limitations
        * under the License.
        */
        package fixio.fixprotocol;

        import java.util.HashMap;

        public enum MessageType {
        <for-each select="messages/message">
            <call-template name="message"/>
        </for-each>
        ;

        private final String type;

        private static final HashMap&lt;String,MessageType&gt; TYPES = new HashMap&lt;&gt;();

        static {
        for (MessageType msgType : MessageType.values()) {
        TYPES.put(msgType.type, msgType);
        }
        }

        public static MessageType forType(String type) {
        return TYPES.get(type);
        }

        private MessageType(String type) {
        this.type = type;
        }
        }
    </template>

    <template name="message" match="/fix/messages/messages">
        <value-of select="@name"/>("<value-of select="@msgtype"/>"),
    </template>

</stylesheet>