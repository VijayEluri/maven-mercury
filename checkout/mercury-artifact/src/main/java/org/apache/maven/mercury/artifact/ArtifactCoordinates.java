/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.maven.mercury.artifact;


/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public class ArtifactCoordinates
{
  /** 
   * standard glorified artifact coordinates
   */
  protected String groupId;

  protected String artifactId;

  protected String version;
  
  /**
   * @param groupId
   * @param artifactId
   * @param version
   */
  public ArtifactCoordinates(
                        String groupId,
                        String artifactId,
                        String version
                                )
  {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  public String getGroupId()
  {
    return groupId;
  }

  public void setGroupId( String groupId )
  {
    this.groupId = groupId;
  }

  public String getArtifactId()
  {
    return artifactId;
  }

  public void setArtifactId( String artifactId )
  {
    this.artifactId = artifactId;
  }

  public String getVersion()
  {
    return version;
  }

  public void setVersion( String version )
  {
    this.version = version;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return groupId+":"+artifactId+":"+version;
  }
}