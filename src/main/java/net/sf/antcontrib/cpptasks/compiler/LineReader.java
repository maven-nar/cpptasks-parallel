/*
 * 
 * Copyright 2008 The Ant-Contrib project
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
package net.sf.antcontrib.cpptasks.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Reads an array of lines from the provided input stream.
 */
public class LineReader implements Runnable {

    private InputStream is;
    private String[] lines;

    /**
     * Create a new line reader.
     * 
     * @param is
     *            input stream to read data from
     */
    public LineReader(InputStream is) {
        this.is = is;
    }

    public String[] getLines() {
        return this.lines;
    }

    /**
     * Copies data from the input stream to the output String[].
     */
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.is));

            Vector lines = new Vector(10);
            String line = null;
            while ((line = reader.readLine()) != null) {
                lines.addElement(line);
            }

            this.lines = new String[lines.size()];
            lines.copyInto(this.lines);
        } catch (IOException e) {
        }
    }
}
