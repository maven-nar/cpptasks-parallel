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
package com.github.maven_nar.cpptasks.os400;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

import com.github.maven_nar.cpptasks.CCTask;
import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.VersionInfo;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinker;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinkerConfiguration;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.compiler.Linker;
import com.github.maven_nar.cpptasks.types.LibrarySet;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;
/**
 * Adapter for the IBM (R) OS/390 (tm) Linker
 * 
 * @author Hiram Chirino (cojonudo14@hotmail.com)
 */
public final class IccLinker extends CommandLineLinker {
    private static final IccLinker datasetLinker = new IccLinker();
    private static final IccLinker dllLinker = new IccLinker("", ".dll");
    private static final IccLinker instance = new IccLinker("", "");
    public static IccLinker getDataSetInstance() {
        return datasetLinker;
    }
    public static IccLinker getInstance() {
        return instance;
    }
    private boolean isADatasetLinker;
    File outputFile;
    private String outputPrefix;
    CCTask task;
    private IccLinker() {
        super("icc", "/bogus", new String[]{".o", ".a", ".lib", ".xds"},
                new String[]{".dll", ".x"}, ".xds", false, null);
        this.outputPrefix = "";
        this.isADatasetLinker = true;
    }
    private IccLinker(String outputPrefix, String outputSuffix) {
        super("icc", "/bogus", new String[]{".o", ".a", ".lib", ".x"},
                new String[]{".dll"}, outputSuffix, false, null);
        this.outputPrefix = outputPrefix;
        this.isADatasetLinker = false;
    }
    protected void addBase(long base, Vector args) {
    }
    protected void addFixed(Boolean fixed, Vector args) {
    }
    protected void addImpliedArgs(boolean debug, LinkType linkType, Vector args) {
        if (linkType.isSharedLibrary()) {
            args.addElement("-W");
            args.addElement("l,DLL");
        }
    }
    protected void addIncremental(boolean incremental, Vector args) {
    }
    /*
     * @see CommandLineLinker#addLibrarySets(LibrarySet[], Vector, Vector,
     *      Vector)
     */
    protected String[] addLibrarySets(CCTask task, LibrarySet[] libsets,
            Vector preargs, Vector midargs, Vector endargs) {
        // If yo want to link against a library sitting in a dataset and
        // not in the HFS, you can just use the //'dataset' notation
        // to specify it. e.g:
        // <libset dir="." libs="//'MQM.V5R2M0.SCSQLOAD'"/>
        //
        // We have to have special handling here because the file is not
        // on the normal filesystem so the task will not noramly include it
        // as part of the link command.
        if (libsets != null) {
            for (int i = 0; i < libsets.length; i++) {
                String libs[] = libsets[i].getLibs();
                for (int j = 0; j < libs.length; j++) {
                    if (libs[j].startsWith("//")) {
                        endargs.addElement("-l");
                        endargs.addElement(libs[j]);
                    } else if (libsets[i].getDataset() != null) {
                        String ds = libsets[i].getDataset();
                        endargs.addElement("//'" + ds + "(" + libs[j] + ")'");
                    }
                }
            }
        }
        return super.addLibrarySets(task, libsets, preargs, midargs, endargs);
    }
    protected void addMap(boolean map, Vector args) {
    }
    protected void addStack(int stack, Vector args) {
    }
    /* (non-Javadoc)
     * @see com.github.maven_nar.cpptasks.compiler.CommandLineLinker#addEntry(int, java.util.Vector)
     */
    protected void addEntry(String entry, Vector args) {
    }
    
    public String getCommandFileSwitch(String commandFile) {
        return "@" + commandFile;
    }
    public File[] getLibraryPath() {
        return CUtil.getPathFromEnvironment("LIB", ";");
    }
    public String[] getLibraryPatterns(String[] libnames, LibraryTypeEnum libType) {
        StringBuffer buf = new StringBuffer();
        String[] patterns = new String[libnames.length * 3];
        int offset = addLibraryPatterns(libnames, buf, "lib", ".a", patterns, 0);
        offset = addLibraryPatterns(libnames, buf, "", ".x", patterns, offset);
        offset = addLibraryPatterns(libnames, buf, "", ".o", patterns, offset);
        return patterns;
    }
    
    private static int addLibraryPatterns(String[] libnames, StringBuffer buf,
            String prefix, String extension, String[] patterns, int offset) {
        for (int i = 0; i < libnames.length; i++) {
            buf.setLength(0);
            buf.append(prefix);
            buf.append(libnames[i]);
            buf.append(extension);
            patterns[offset + i] = buf.toString();
        }
        return offset + libnames.length;
    }
    
    
    public Linker getLinker(LinkType linkType) {
        if (this == datasetLinker)
            return datasetLinker;
        if (linkType.isSharedLibrary())
            return dllLinker;
        return instance;
    }
    public int getMaximumCommandLength() {
        return Integer.MAX_VALUE;
    }
    protected String[] getOutputFileSwitch(CCTask task, String outputFile) {
        if (isADatasetLinker && task.getDataset() != null) {
            String ds = task.getDataset();
            outputFile = "//'" + ds + "(" + outputFile + ")'";
        }
        return getOutputFileSwitch(outputFile);
    }
    public String[] getOutputFileSwitch(String outputFile) {
        return new String[]{"-o", outputFile};
    }
    public boolean isCaseSensitive() {
        return IccProcessor.isCaseSensitive();
    }
    /*
     * @see CommandLineLinker#link(Task, File, String[],
     *      CommandLineLinkerConfiguration)
     */
    public void link(CCTask task, File outputFile, String[] sourceFiles,
            CommandLineLinkerConfiguration config) throws BuildException {
        this.task = task;
        this.outputFile = outputFile;
        if (isADatasetLinker) {
            int p = outputFile.getName().indexOf(".");
            if (p >= 0) {
                String newname = outputFile.getName().substring(0, p);
                outputFile = new File(outputFile.getParent(), newname);
            }
        }
        super.link(task, outputFile, sourceFiles, config);
    }
    /*
     * @see CommandLineLinker#runCommand(Task, File, String[])
     */
    protected int runCommand(CCTask task, File workingDir, String[] cmdline)
            throws BuildException {
        int rc = super.runCommand(task, workingDir, cmdline);
        // create the .xds file if everything was ok.
        if (rc == 0) {
            try {
                outputFile.delete();
                new FileOutputStream(outputFile).close();
            } catch (IOException e) {
                throw new BuildException(e.getMessage());
            }
        }
        return rc;
    }
    public String[] getOutputFileNames(String baseName, VersionInfo versionInfo) {
    	String[] baseNames = super.getOutputFileNames(baseName, versionInfo);
    	if (outputPrefix.length() > 0) {
    		for(int i = 0; i < baseNames.length; i++) {
    			baseNames[i] = outputPrefix + baseNames[i];
    		}
    	}
        return baseNames;
    }
}
