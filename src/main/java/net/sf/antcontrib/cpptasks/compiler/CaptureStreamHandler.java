/*
 * 
 * Copyright 2001-2004 The Ant-Contrib project
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

/**
 * Implements ExecuteStreamHandler to capture the output of a Execute to an
 * array of strings
 * 
 * @author Curt Arnold
 */
public class CaptureStreamHandler implements ExecuteStreamHandler {
    /**
     * Runs an executable and captures the output in a String array
     * 
     * @param cmdline
     *            command line arguments
     * @return output of process
     */
    public static String[] run(String[] cmdline) {
        CaptureStreamHandler handler = new CaptureStreamHandler();
        Execute exec = new Execute(handler);
        exec.setCommandline(cmdline);
        try {
            exec.execute();
        } catch (IOException ex) {
        }
        return handler.getOutput();
    }

    private InputStream errorStream;
    private InputStream fromProcess;
    private String[] output;
    private LineReader outputReader;
    private LineReader errorReader;
    private Thread outputReaderThread;
    private Thread errorReaderThread;

    public CaptureStreamHandler() {
    }

    public String[] getOutput() {
        if (this.output != null) {
            return this.output;
        } else {
            return new String[0];
        }
    }

    /**
     * Install a handler for the error stream of the subprocess.
     * 
     * @param is
     *            input stream to read from the error stream from the subprocess
     */
    public void setProcessErrorStream(InputStream is) throws IOException {
        errorStream = is;
    }

    /**
     * Install a handler for the input stream of the subprocess.
     * 
     * @param os
     *            output stream to write to the standard input stream of the
     *            subprocess
     */
    public void setProcessInputStream(OutputStream os) throws IOException {
        os.close();
    }

    /**
     * Install a handler for the output stream of the subprocess.
     * 
     * @param is
     *            input stream to read from the error stream from the subprocess
     */
    public void setProcessOutputStream(InputStream is) throws IOException {
        fromProcess = is;
    }

    /**
     * Start handling of the streams.
     */
    public void start() throws IOException {
        this.outputReader = new LineReader(this.fromProcess);
        this.errorReader = new LineReader(this.errorStream);
        this.outputReaderThread = new Thread(this.outputReader);
        this.errorReaderThread = new Thread(this.errorReader);

        outputReaderThread.start();
        errorReaderThread.start();
    }

    /**
     * Stop handling of the streams - will not be restarted.
     */
    public void stop() {
        try {
            if (this.outputReaderThread != null) {
                this.outputReaderThread.join();
            }
            if (this.errorReaderThread != null) {
                this.errorReaderThread.join();
            }
        } catch (InterruptedException e) {
        }

        String[] outputLines = null;
        String[] errorLines = null;

        if (this.outputReader != null) {
            outputLines = this.outputReader.getLines();
        } else {
            outputLines = new String[0];
        }

        if (this.errorReader != null) {
            errorLines = this.errorReader.getLines();
        } else {
            errorLines = new String[0];
        }

        this.output = new String[outputLines.length + errorLines.length];
        int pos = 0;
        for (int i = 0; i < errorLines.length; i++) {
            this.output[pos++] = errorLines[i];
        }
        for (int i = 0; i < outputLines.length; i++) {
            this.output[pos++] = outputLines[i];
        }
    }
}
