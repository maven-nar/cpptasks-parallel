<!--
 Licensed to the Ant-Contrib Project under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The Ant-Contrib Project licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

-->
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsl:version="1.0">

   <xsl:output method="xml" indent="yes"/>

   <xsl:apply-templates select="/"/>

   <xsl:template match="/">
  <xsl:comment>

 Licensed to the Ant-Contrib Project under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The Ant-Contrib Project licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.


  </xsl:comment>
  <document>
  <properties>
    <title>cpptasks: Compile tasks for Apache Ant</title>
  </properties>
  <body>
  
    <release version="1.0-beta5" date="2007-12-31" description="">
       <!-- xsl:apply-templates select='/rss/channel/item'>
           <xsl:sort select="substring-after(key, '-')" data-type="number"/>
       </xsl:apply-templates -->
     </release>

    <release version="1.0-beta4" date="2006-05-19" description="">
     </release>

    <release version="1.0-beta3" date="2004-04-28" description="">
     </release>

    <release version="1.0-beta2" date="2004-02-27" description="">
     </release>

    <release version="1.0-beta1" date="2002-08-17" description="">
     </release>

    <release version="1.0-alpha1" date="2002-01-08" description="">
     </release>
  </body>
</document>
</xsl:template>

<xsl:template match="item">
      <action issue="{key}"><xsl:value-of select="summary"/></action>
</xsl:template>

</xsl:transform>
