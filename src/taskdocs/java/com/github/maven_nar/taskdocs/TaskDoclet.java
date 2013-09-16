/*
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

*/
package com.github.maven_nar.taskdocs;

import com.sun.javadoc.*;
import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * This document writes an XML representation of the
 *   Ant related Javadoc through an XSLT transform that creates xdoc files.
 *
 */
public final class TaskDoclet {
    /**
     * Process Javadoc content.
     * @param root root of javadoc content.
     * @return true if successful
     * @throws Exception IO exceptions and the like.
     */
    public static boolean start(RootDoc root) throws Exception {
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        Source typeStyle = new StreamSource(new File("src/taskdocs/resources/com/github/maven_nar/taskdocs/element.xslt"));
        //
        //  replace with tf.newTransformerHandler() if you want to see raw generated XML.
        TransformerHandler typeHandler = tf.newTransformerHandler(typeStyle);

        Map referencedTypes = new HashMap();
        Map documentedTypes = new HashMap();
        ClassDoc[] classes = root.classes();
        for (int i = 0; i < classes.length; ++i) {
            ClassDoc clazz = classes[i];
            if (clazz.isPublic() && !clazz.isAbstract()) {
                if (isTask(clazz) || isType(clazz)) {
                    writeClass(typeHandler, clazz, referencedTypes);
                    documentedTypes.put(clazz.qualifiedTypeName(), clazz);
                }
            }
        }

        Map additionalTypes = new HashMap();
        for (Iterator iter = referencedTypes.keySet().iterator(); iter.hasNext();) {
            String referencedName = (String) iter.next();
            if (documentedTypes.get(referencedName) == null) {
                ClassDoc referencedClass = root.classNamed(referencedName);
                if (referencedClass != null) {
                    if (!referencedClass.qualifiedTypeName().startsWith("org.apache.tools.ant")) {
                        writeClass(typeHandler, referencedClass, additionalTypes);
                        documentedTypes.put(referencedClass.qualifiedTypeName(), referencedClass);
                    }
                }
            }
        }


        return true;
    }


    /**
     * Determine if class is an Ant task.
     * @param clazz class to test.
     * @return true if class is an Ant task.
     */
    private static boolean isTask(final ClassDoc clazz) {
        if (clazz == null) return false;
        if ("org.apache.tools.ant.Task".equals(clazz.qualifiedTypeName())) {
            System.out.print("true");
            return true;
        }
        return isTask(clazz.superclass());

    }

    /**
     * Determine if class is an Ant type.
     * @param clazz class to test.
     * @return true if class is an Ant type.
     */
    private static boolean isType(final ClassDoc clazz) {
        if (clazz == null) return false;
        if ("org.apache.tools.ant.types.DataType".equals(clazz.qualifiedTypeName())) {
            return true;
        }
        return isType(clazz.superclass());

    }

    /**
     * Namespace URI for class description elements.
     */
    private static final String NS_URI = "http://ant-contrib.sf.net/taskdocs";
    /**
     * Namespace URI for XHTML elements.
     */
    private static final String XHTML_URI = "http://www.w3.org/1999/xhtml";

    /**
     * Write a Java type.
     * @param tf content handler.
     * @param type documented type.
     * @throws Exception if IO or other exception.
     */
    private static void writeType(final TransformerHandler tf, final Type type) throws Exception {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "name", "name", "CDATA", type.simpleTypeName());
        attributes.addAttribute(null, "qualifiedTypeName", "qualifiedTypeName", "CDATA", type.qualifiedTypeName());
        tf.startElement(NS_URI, "type", "type", attributes);
        ClassDoc typeDoc = type.asClassDoc();
        if (typeDoc != null && typeDoc.commentText() != null && typeDoc.commentText().length() > 0) {
            writeDescription(tf, typeDoc.commentText());
        } else {
            tf.characters(type.typeName().toCharArray(), 0, type.typeName().length());
        }
        tf.endElement(NS_URI, "type", "type");

    }

    /**
     * Write an Ant task or type attribute (aka property).
     * @param tf content handler.
     * @param method set method for property.
     * @throws Exception if IO or other exception.
     */
    private static void writeAttribute(final TransformerHandler tf, final MethodDoc method) throws Exception {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "name", "name", "CDATA", method.name().substring(3).toLowerCase(Locale.US));
        tf.startElement(NS_URI, "attribute", "attribute", attributes);
        writeType(tf, method.parameters()[0].type());
        attributes.clear();
        tf.startElement(NS_URI, "comment", "comment", attributes);
        writeDescription(tf, method.commentText());
        tf.endElement(NS_URI, "comment", "comment");
        tf.endElement(NS_URI, "attribute", "attribute");
    }


    /**
     * Write an Ant nested element.
     * @param tf content handler.
     * @param method method to add element to task or type.
     * @param name name of nested element.
     * @param type type of nested element.
     * @param referencedTypes map of types referenced in documentation.
     * @throws Exception if IO or other exception.
     */
    private static void writeChild(final TransformerHandler tf,
                                   final MethodDoc method,
                                   final String name,
                                   final Type type,
                                   final Map referencedTypes) throws Exception {
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "name", "name", "CDATA", name.toLowerCase(Locale.US));
        tf.startElement(NS_URI, "child", "child", attributes);
        attributes.clear();
        tf.startElement(NS_URI, "comment", "comment", attributes);
        writeDescription(tf, method.commentText());
        tf.endElement(NS_URI, "comment", "comment");
        writeType(tf, type);
        tf.endElement(NS_URI, "child", "child");
        referencedTypes.put(type.qualifiedTypeName(), type);
    }


    /**
     * Redirects parsed XHTML comment into output stream.
     * Drops start and end document and body element.
     */
    private static class RedirectHandler extends DefaultHandler {
        /**
         * output handler.
         */
        private final ContentHandler tf;

        /**
         * Create new instance.
         * @param tf output handler, may not be null.
         */
        public RedirectHandler(final TransformerHandler tf) {
            if (tf == null) { throw new IllegalArgumentException("tf"); }
            this.tf = tf;
        }

        /** {@inheritDoc} */
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            tf.characters(ch, start, length);
        }

        /** {@inheritDoc} */
        public void endDocument() {
        }

        /** {@inheritDoc} */
        public void endElement(final String namespaceURI,
                               final String localName,
                               final String qName) throws SAXException {
            if (!"body".equals(localName)) {
                tf.endElement(namespaceURI, localName, qName);
            }
        }

        /** {@inheritDoc} */
        public void endPrefixMapping(final String prefix) throws SAXException {
            tf.endPrefixMapping(prefix);
        }

        /** {@inheritDoc} */
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            tf.ignorableWhitespace(ch, start, length);
        }

        /** {@inheritDoc} */
        public void processingInstruction(final String target, final String data) throws SAXException {
            tf.processingInstruction(target, data);
        }

        /** {@inheritDoc} */
        public void setDocumentLocator(final Locator locator) {
            tf.setDocumentLocator(locator);
        }

        /** {@inheritDoc} */
        public void skippedEntity(String name) throws SAXException {
            tf.skippedEntity(name);
        }

        /** {@inheritDoc} */
        public void startDocument() {
        }

        /** {@inheritDoc} */
        public void startElement(final String namespaceURI,
                                 final String localName,
                                 final String qName,
                                 final Attributes atts) throws SAXException {
            if (!"body".equals(localName)) {
                tf.startElement(namespaceURI, localName, qName, atts);
            }
        }

        /** {@inheritDoc} */
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            tf.startPrefixMapping(prefix, uri);
        }
    }

    /**
     * Writes description.
     * @param tf destination.
     * @param description description, may contain XHTML elements.
     * @throws SAXException if IO or other exception.
     */
    private static void writeDescription(final TransformerHandler tf,
                                         final String description) throws SAXException {
        if (description.indexOf('<') == -1) {
            tf.characters(description.toCharArray(), 0, description.length());
        } else {
            //
            //   attempt to fabricate an XHTML fragment
            //
            StringBuffer buf = new StringBuffer(description);
            buf.insert(0, "<body xmlns='" + XHTML_URI + "'>");
            buf.append("</body>");
            try {
                SAXParserFactory sf = SAXParserFactory.newInstance();
                sf.setNamespaceAware(true);
                SAXParser parser = sf.newSAXParser();
                parser.parse(new InputSource(new StringReader(buf.toString())), new RedirectHandler(tf));
            } catch (Exception ex) {
                tf.characters(ex.toString().toCharArray(), 0, ex.toString().length());
            }
        }
    }

    /**
     * Write all Ant attributes in this class and superclasses.
     * @param tf destination.
     * @param clazz class documentation.
     * @param processed map of processed methods.
     * @param referencedTypes map of referenced types.
     * @throws Exception if IO or other exception.
     */
    private static void writeAttributes(final TransformerHandler tf,
                                     final ClassDoc clazz,
                                     final Map processed,
                                     final Map referencedTypes) throws Exception {
        MethodDoc[] methods = clazz.methods();
        for (int i = 0; i < methods.length; i++) {
            MethodDoc method = methods[i];
            if (processed.get(method.name()) == null) {
                if (method.name().startsWith("set") && method.isPublic() && method.parameters().length == 1) {
                    writeAttribute(tf, method);
                    referencedTypes.put(method.parameters()[0].typeName(), method.parameters()[0].type());
                }
                processed.put(method.name(), method);
            }
        }
        if (clazz.superclass() != null) {
            writeAttributes(tf, clazz.superclass(), processed, referencedTypes);
        }

    }

    /**
     * Write all Ant nested elements in this class and superclasses.
     * @param tf destination.
     * @param clazz class documentation.
     * @param processed map of processed methods.
     * @param referencedTypes map of referenced types.
     * @throws Exception if IO or other exception.
     */
    private static final void writeChildren(final TransformerHandler tf,
                                            final ClassDoc clazz,
                                            final Map processed,
                                            final Map referencedTypes) throws Exception {
        MethodDoc[] methods = clazz.methods();
        for (int i = 0; i < methods.length; i++) {
            MethodDoc method = methods[i];
            if (processed.get(method.name()) == null) {
                if (method.name().startsWith("addConfigured") && method.isPublic() && method.parameters().length == 1) {
                    writeChild(tf, method, method.name().substring(13), method.parameters()[0].type(), referencedTypes);
                } else if (method.name().startsWith("add") && method.isPublic() && method.parameters().length == 1) {
                    writeChild(tf, method, method.name().substring(3), method.parameters()[0].type(), referencedTypes);
                } else if (method.isPublic() && method.parameters().length == 0 && method.name().startsWith("create")) {
                    writeChild(tf, method, method.name().substring(6), method.returnType(), referencedTypes);
                }
                processed.put(method.name(), method);
            }
        }
        if (clazz.superclass() != null) {
            writeChildren(tf, clazz.superclass(), processed, referencedTypes);
        }
    }


    /**
     * Write Ant documentation for this class.
     * @param tf destination.
     * @param clazz class documentation.
     * @param referencedTypes map of referenced types.
     * @throws Exception if IO or other exception.
     */
    private static void writeClass(final TransformerHandler tf,
                                   final ClassDoc clazz,
                                   final Map referencedTypes) throws Exception {
        StreamResult result = new StreamResult(new File("src/site/xdoc/antdocs/" + clazz.name() + ".xml"));
        tf.setResult(result);
        AttributesImpl attributes = new AttributesImpl();
        attributes.addAttribute(null, "name", "name", "CDATA", clazz.name());
        StringBuffer firstSentence = new StringBuffer();
        Tag[] tags = clazz.firstSentenceTags();
        for (int i = 0; i < tags.length; i++) {
            firstSentence.append(tags[i].text());
        }
        if (firstSentence.length() > 0) {
            attributes.addAttribute(null, "firstSentence", "firstSentence", "CDATA", firstSentence.toString());
        }
        tf.startDocument();
        tf.startElement(NS_URI, "class", "class", attributes);
        attributes.clear();
        tf.startElement(NS_URI, "comment", "comment", attributes);
        writeDescription(tf, clazz.commentText());
        tf.endElement(NS_URI, "comment", "comment");

        tf.startElement(NS_URI, "attributes", "attributes", attributes);
        Map methods = new HashMap();
        methods.put("setProject", "setProject");
        methods.put("setRuntimeConfigurableWrapper", "setRuntimeConfigurableWrapper");
        writeAttributes(tf, clazz, methods, referencedTypes);
        tf.endElement(NS_URI, "attributes", "attributes");

        tf.startElement(NS_URI, "children", "children", attributes);
        Map children = new HashMap();
        writeChildren(tf, clazz, children, referencedTypes);
        tf.endElement(NS_URI, "children", "children");

        tf.endElement(NS_URI, "class", "class");
        tf.endDocument();
    }

}
