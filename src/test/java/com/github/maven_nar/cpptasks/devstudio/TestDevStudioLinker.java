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
import org.apache.tools.ant.taskdefs.condition.Os;

import com.github.maven_nar.cpptasks.compiler.AbstractProcessor;
import com.github.maven_nar.cpptasks.compiler.TestAbstractLinker;
import com.github.maven_nar.cpptasks.devstudio.DevStudioLinker;
/**
 * Test for Microsoft Developer Studio linker
 * 
 * Override create to test concrete compiler implementions
 */
public class TestDevStudioLinker extends TestAbstractLinker {
    public TestDevStudioLinker(String name) {
        super(name);
    }
    protected AbstractProcessor create() {
        return DevStudioLinker.getInstance();
    }
    public void testGetIdentfier() {
        if (!Os.isFamily("windows")) {
            return;
        }
        AbstractProcessor compiler = create();
        String id = compiler.getIdentifier();
        boolean hasMSLinker = ((id.indexOf("Microsoft") >= 0) && (id
                .indexOf("Linker") >= 0))
                || id.indexOf("link") >= 0;
        assertTrue(hasMSLinker);
    }
}
