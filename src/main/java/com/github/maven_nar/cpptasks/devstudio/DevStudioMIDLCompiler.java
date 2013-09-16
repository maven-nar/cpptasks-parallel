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
package com.github.maven_nar.cpptasks.devstudio;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.types.Environment;

import com.github.maven_nar.cpptasks.CUtil;
import com.github.maven_nar.cpptasks.OptimizationEnum;
import com.github.maven_nar.cpptasks.compiler.CommandLineCompiler;
import com.github.maven_nar.cpptasks.compiler.LinkType;
import com.github.maven_nar.cpptasks.compiler.Linker;
import com.github.maven_nar.cpptasks.compiler.Processor;
import com.github.maven_nar.cpptasks.parser.CParser;
import com.github.maven_nar.cpptasks.parser.Parser;

/**
 * Adapter for the Microsoft (r) MIDL Compiler
 * 
 * @author Curt Arnold
 */
public final class DevStudioMIDLCompiler extends CommandLineCompiler {
    private static final DevStudioMIDLCompiler instance = new DevStudioMIDLCompiler(
            false, null);
    public static DevStudioMIDLCompiler getInstance() {
        return instance;
    }
    private DevStudioMIDLCompiler(boolean newEnvironment, Environment env) {
        super("midl", null, new String[]{".idl", ".odl"}, new String[]{},
                ".tlb", false, null, newEnvironment, env);
    }
    protected void addImpliedArgs(final Vector args, 
    		final boolean debug,
            final boolean multithreaded, 
			final boolean exceptions, 
			final LinkType linkType,
			final Boolean rtti,
			final OptimizationEnum optimization) {
    }
    protected void addWarningSwitch(Vector args, int level) {
        DevStudioProcessor.addWarningSwitch(args, level);
    }
    public Processor changeEnvironment(boolean newEnvironment, Environment env) {
        if (newEnvironment || env != null) {
            return new DevStudioMIDLCompiler(newEnvironment, env);
        }
        return this;
    }
    /**
     * The include parser for C will work just fine, but we didn't want to
     * inherit from CommandLineCCompiler
     */
    protected Parser createParser(File source) {
        return new CParser();
    }
    protected int getArgumentCountPerInputFile() {
        return 3;
    }
    protected void getDefineSwitch(StringBuffer buffer, String define,
            String value) {
        DevStudioProcessor.getDefineSwitch(buffer, define, value);
    }
    protected File[] getEnvironmentIncludePath() {
        return CUtil.getPathFromEnvironment("INCLUDE", ";");
    }
    protected String getIncludeDirSwitch(String includeDir) {
        return DevStudioProcessor.getIncludeDirSwitch(includeDir);
    }
    protected String getInputFileArgument(File outputDir, String filename,
            int index) {
        switch (index) {
            case 0 :
                return "/tlb";
            case 1 :
                return new File(outputDir, getOutputFileNames(filename, null)[0])
                        .getAbsolutePath();
        }
        return filename;
    }
    public Linker getLinker(LinkType type) {
        return DevStudioLinker.getInstance().getLinker(type);
    }
    public int getMaximumCommandLength() {
// FREEHEP stay on the safe side
        return 32000; // 32767;
    }
    protected int getMaximumInputFilesPerCommand() {
        return 1;
    }
    protected int getTotalArgumentLengthForInputFile(File outputDir,
            String inputFile) {
        String arg1 = getInputFileArgument(outputDir, inputFile, 0);
        String arg2 = getInputFileArgument(outputDir, inputFile, 1);
        String arg3 = getInputFileArgument(outputDir, inputFile, 2);
        return arg1.length() + arg2.length() + arg3.length() + 3;
    }
    protected void getUndefineSwitch(StringBuffer buffer, String define) {
        DevStudioProcessor.getUndefineSwitch(buffer, define);
    }
}
