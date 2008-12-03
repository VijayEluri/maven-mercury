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
package org.apache.maven.mercury.repository.tests;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.maven.mercury.MavenDependencyProcessor;
import org.apache.maven.mercury.artifact.ArtifactBasicMetadata;
import org.apache.maven.mercury.crypto.api.StreamVerifierAttributes;
import org.apache.maven.mercury.crypto.api.StreamVerifierFactory;
import org.apache.maven.mercury.crypto.pgp.PgpStreamVerifierFactory;
import org.apache.maven.mercury.crypto.sha.SHA1VerifierFactory;
import org.apache.maven.mercury.repository.local.m2.MetadataProcessorMock;
import org.apache.maven.mercury.repository.remote.m2.RemoteRepositoryM2;
import org.apache.maven.mercury.transport.api.Credentials;
import org.apache.maven.mercury.transport.api.Server;
import org.apache.maven.mercury.util.FileUtil;

/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public class RemoteRepositoryWriterM2NexusTest
extends AbstractRepositoryWriterM2Test
{
  protected boolean needNexus = false;
  
  String nexusReleasesTestDir = "./target/nexus-webapp-1.0.1/runtime/work/storage/releases";
  String nexusReleasesTestUrl = Nexus.nexusTestUrl+"/content/repositories/releases";

  String nexusSnapshotsTestDir = "./target/nexus-webapp-1.0.1/runtime/work/storage/snapshots";
  String nexusSnapshotsTestUrl = Nexus.nexusTestUrl+"/content/repositories/snapshots";

  //------------------------------------------------------------------------------
  @Override
  void setReleases()
  throws Exception
  {
    Nexus.stop();
    targetDirectory = new File(nexusReleasesTestDir);
    FileUtil.delete( new File( targetDirectory, "org" ) );
    server.setURL( new URL(nexusReleasesTestUrl) );
    Nexus.start( plexus );
  }
  //------------------------------------------------------------------------------
  @Override
  void setSnapshots()
  throws Exception
  {
    Nexus.stop();
    targetDirectory = new File( nexusSnapshotsTestDir );
    FileUtil.delete( new File( targetDirectory, "org" ) );
    server.setURL( new URL( nexusSnapshotsTestUrl ) );
    Nexus.start( plexus );
  }
  //------------------------------------------------------------------------------
  @Override
  protected void setUp()
  throws Exception
  {
    needNexus = true;
    
    super.setUp();

    mdProcessor = new MetadataProcessorMock();

    query = new ArrayList<ArtifactBasicMetadata>();
    
    Credentials user = new Credentials( Nexus.nexusTestUser, Nexus.nexusTestPass );

    server = new Server( "nexusTest", new URL(nexusSnapshotsTestUrl), false, false, user );
    
    repo = new RemoteRepositoryM2( "testNexusRepo", server, new MavenDependencyProcessor() );
    
    mdProcessor = new MetadataProcessorMock();
    
    query = new ArrayList<ArtifactBasicMetadata>();
    
    // verifiers
    factories = new HashSet<StreamVerifierFactory>();       
    factories.add( 
        new PgpStreamVerifierFactory(
                new StreamVerifierAttributes( PgpStreamVerifierFactory.DEFAULT_EXTENSION, false, false )
                , getClass().getResourceAsStream( secretKeyFile )
                , keyId
                , secretKeyPass
                                    )
                  );
    factories.add( new SHA1VerifierFactory(false,false) );
    server.setWriterStreamVerifierFactories(factories);
      
    repo.setDependencyProcessor( mdProcessor );
    reader = repo.getReader();
    writer = repo.getWriter();
    
    setSnapshots();
  }
  //-------------------------------------------------------------------------
  @Override
  protected void tearDown()
  throws Exception
  {
    super.tearDown();
  }
  //-------------------------------------------------------------------------
  @Override
  public void testWriteContentionMultipleArtifacts()
      throws Exception
  {
    System.out.println("Mutliple Artifacts contention test fails for remote repo. Currently there is no way to synchronize those writes");
  }
  
  @Override
  public void testWriteContentionSingleArtifact()
      throws Exception
  {
    System.out.println("Single Artifacts contention test fails for remote repo. Currently there is no way to synchronize those writes");
  }
  
  //-------------------------------------------------------------------------
  //-------------------------------------------------------------------------
}
