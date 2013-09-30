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
package com.github.maven_nar.cpptasks.gcc;
import java.io.File;
import java.util.Vector;


import org.apache.tools.ant.BuildException;

import com.github.maven_nar.cpptasks.CCTask;
import com.github.maven_nar.cpptasks.VersionInfo;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinker;
import com.github.maven_nar.cpptasks.compiler.CommandLineLinkerConfiguration;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.types.LibraryTypeEnum;
/**
 * Adapter for the "ar" tool
 * 
 * @author Adam Murdoch
 * @author Curt Arnold
 */
public abstract class AbstractArLibrarian extends CommandLineLinker {
    private/* final */
    String outputPrefix;
    protected AbstractArLibrarian(String command, String identificationArg,
            String[] inputExtensions, String[] ignoredExtensions,
            String outputPrefix, String outputExtension, boolean isLibtool,
            AbstractArLibrarian libtoolLibrarian) {
        super(command, identificationArg, inputExtensions, ignoredExtensions,
                outputExtension, isLibtool, libtoolLibrarian);
        this.outputPrefix = outputPrefix;
    }
    public void addBase(long base, Vector args) {
    }
    public void addFixed(Boolean fixed, Vector args) {
    }
    public void addImpliedArgs(boolean debug, LinkType linkType, Vector args) {
    }
    public void addIncremental(boolean incremental, Vector args) {
    }
    public void addMap(boolean map, Vector args) {
    }
    public void addStack(int stack, Vector args) {
    }
    /* (non-Javadoc)
     * @see com.github.maven_nar.cpptasks.compiler.CommandLineLinker#addEntry(int, java.util.Vector)
     */
    protected void addEntry(String entry, Vector args) {
    }
    
    public String getCommandFileSwitch(String commandFile) {
        return null;
    }
    public File[] getLibraryPath() {
        return new File[0];
    }
    public String[] getLibraryPatterns(String[] libnames, LibraryTypeEnum libType) {
    	return new String[0];
    }
    public int getMaximumCommandLength() {
        return Integer.MAX_VALUE;
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
    public String[] getOutputFileSwitch(String outputFile) {
        return GccProcessor.getOutputFileSwitch("rvs", outputFile);
    }
    public boolean isCaseSensitive() {
        return true;
    }
    public void link(CCTask task, File outputFile, String[] sourceFiles,
            CommandLineLinkerConfiguration config) throws BuildException {
        //
        //   if there is an existing library then
        //      we must delete it before executing "ar"
        if (outputFile.exists()) {
            if (!outputFile.delete()) {
                throw new BuildException("Unable to delete "
                        + outputFile.getAbsolutePath());
            }
        }
        //
        //   delegate to CommandLineLinker
        //
        super.link(task, outputFile, sourceFiles, config);
    }
}
