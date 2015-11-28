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
<stylesheet version="2.0"
            xmlns="http://www.w3.org/1999/XSL/Transform">
    <output method="text" indent="no" standalone="yes" media-type="text/java" omit-xml-declaration="yes"/>


    <strip-space elements="*"/>

    <!--XHTML document outline-->
    <template match="fix">
        /*
        * Copyright 2015 The FIX.io Project
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
        import static fixio.fixprotocol.DataType.*;

        public enum FieldType {
        UNKNOWN(-1, STRING),
        <for-each select="fields/field">
            <call-template name="field"/>
        </for-each>
        ;

        private final int tag;
        private final DataType type;
        private final String[] enumValues;

        private static final HashMap&lt;Integer,FieldType&gt; TYPES = new HashMap&lt;&gt;(FieldType.values().length);

        static {
            for (FieldType fieldType : FieldType.values()) {
                if (fieldType.tag &gt; 0) {
                    TYPES.put(fieldType.tag, fieldType);
                }
            }
        }

        public static FieldType forTag(int tag) {
            FieldType fieldType = TYPES.get(tag);
            return (fieldType != null) ? fieldType : UNKNOWN;
        }

        private FieldType(int tag, DataType type) {
            this.tag = tag;
            this.type = type;
            this.enumValues = null;
        }

        private FieldType(int tag, DataType type, String... enumValues) {
            this.tag = tag;
            this.type = type;
            this.enumValues = enumValues;
        }

        public int tag() {
        return tag;
        }

        public DataType type() {
        return type;
        }

        public String[] enumValues() {
        return enumValues;
        }
        }
    </template>

    <template name="field" match="/fix/fields/field">
        <value-of select="@name"/>(<value-of select="@number"/>,
        <value-of select="@type"/>
        <apply-templates select="value"/>
        ),
    </template>

    <template match="value">
        ,"<value-of select="@enum"/>"//<value-of select="@description"/>
    </template>

</stylesheet>