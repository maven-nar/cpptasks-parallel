/*
 *
 * Copyright 2004 The Ant-Contrib project
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
package com.github.maven_nar.cpptasks;

import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * Distributer prococol names (experimental).
 *
 * @author Curt Arnold
 *
 */
public final class DistributerProtocolEnum
    extends EnumeratedAttribute {
  /**
   * Constructor.
   *
   * Set by default to "distcc"
   *
   * @see java.lang.Object#Object()
   */
  public DistributerProtocolEnum() {
    setValue("distcc");
  }

  /**
   * Gets list of acceptable values.
   *
   * @see org.apache.tools.ant.types.EnumeratedAttribute#getValues()
   */
  public String[] getValues() {
    return new String[] {
        "distcc",
        "ssh"};
  }
}
