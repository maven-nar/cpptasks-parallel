/*
 * 
 * Copyright 2002-2004 The Ant-Contrib project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.maven_nar.cpptasks.parser;
public class FilenameState extends AbstractParserState {
    private final StringBuffer buf = new StringBuffer();
    private final char[] terminators;
    public FilenameState(AbstractParser parser, char[] terminators) {
        super(parser);
        this.terminators = (char[]) terminators.clone();
    }
    public AbstractParserState consume(char ch) {
        for (int i = 0; i < terminators.length; i++) {
            if (ch == terminators[i]) {
                getParser().addFilename(buf.toString());
                buf.setLength(0);
                return null;
            }
        }
        if (ch == '\n') {
            buf.setLength(0);
            return getParser().getNewLineState();
        } else {
            buf.append(ch);
        }
        return this;
    }
}
