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
package org.apache.maven.mercury.plexus;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.maven.mercury.artifact.Artifact;
import org.apache.maven.mercury.artifact.ArtifactBasicMetadata;
import org.apache.maven.mercury.artifact.ArtifactExclusionList;
import org.apache.maven.mercury.artifact.ArtifactMetadata;
import org.apache.maven.mercury.artifact.ArtifactQueryList;
import org.apache.maven.mercury.artifact.ArtifactScopeEnum;
import org.apache.maven.mercury.artifact.DefaultArtifact;
import org.apache.maven.mercury.crypto.api.StreamVerifierFactory;
import org.apache.maven.mercury.crypto.pgp.PgpStreamVerifierFactory;
import org.apache.maven.mercury.crypto.sha.SHA1VerifierFactory;
import org.apache.maven.mercury.repository.api.Repository;
import org.apache.maven.mercury.repository.api.RepositoryException;
import org.apache.maven.mercury.repository.local.m2.LocalRepositoryM2;
import org.apache.maven.mercury.repository.remote.m2.RemoteRepositoryM2;
import org.apache.maven.mercury.repository.virtual.VirtualRepositoryReader;
import org.apache.maven.mercury.transport.api.Server;
import org.apache.maven.mercury.util.FileUtil;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusTestCase;

/**
 * 
 * 
 * @author Oleg Gusakov
 * @version $Id: DefaultPlexusMercuryTest.java 723125 2008-12-03 23:19:50Z ogusakov $
 */
public class DefaultPlexusMercuryTest
extends PlexusTestCase
{
  PlexusMercury pm;

  RemoteRepositoryM2 remoteRepo;
  LocalRepositoryM2  localRepo;
  
  List<Repository>   repos;
  
  Artifact a;
  
  protected static final String keyId   = "0EDB5D91141BC4F2";

  protected static final String secretKeyFile = "/pgp/secring.gpg";
  protected static final String publicKeyFile = "/pgp/pubring.gpg";
  protected static final String secretKeyPass = "testKey82";
  
//  public static final String SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_URL = "plexus.mercury.test.url";
//  private String remoteServerUrl = System.getProperty( SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_URL, null );
  static String remoteServerUrl = "http://people.apache.org/~ogusakov/repos/test";
  String artifactCoord = "org.apache.maven.mercury:mercury-repo-virtual:1.0.0-alpha-2-SNAPSHOT";

  private File localRepoDir;
  
  public static final String SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_USER = "plexus.mercury.test.user";
  static String remoteServerUser = System.getProperty( SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_USER, "admin" );

  public static final String SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_PASS = "plexus.mercury.test.pass";
  static String remoteServerPass = System.getProperty( SYSTEM_PARAMETER_PLEXUS_MERCURY_TEST_PASS, "admin123" );
  
  PgpStreamVerifierFactory pgpRF;
  PgpStreamVerifierFactory pgpWF;
  
  SHA1VerifierFactory      sha1F;
  HashSet<StreamVerifierFactory> vFacSha1;
  
  VirtualRepositoryReader vrr;
  
  PlexusContainer plexus;
  
  //-------------------------------------------------------------------------------------
//  @Override
  protected void setUp()
  throws Exception
  {
    super.setUp();
    
    // prep. Artifact
    File artifactBinary = File.createTempFile( "test-repo-writer", "bin" );
    FileUtil.writeRawData( getClass().getResourceAsStream( "/maven-core-2.0.9.jar" ), artifactBinary );
    
    a = new DefaultArtifact( new ArtifactBasicMetadata("org.apache.maven.mercury:mercury-core:2.0.9") );
    
    a.setPomBlob( FileUtil.readRawData( getClass().getResourceAsStream( "/maven-core-2.0.9.pom" ) ) );
    a.setFile( artifactBinary );
    
    // prep Repository
    pm = getContainer().lookup( PlexusMercury.class );
    
    pgpRF = pm.createPgpReaderFactory( true, true, getClass().getResourceAsStream( publicKeyFile ) );
    pgpWF = pm.createPgpWriterFactory( true, true, getClass().getResourceAsStream( secretKeyFile ), keyId, secretKeyPass );
    
    sha1F = new SHA1VerifierFactory( true, false );
    
    remoteRepo = pm.constructRemoteRepositoryM2( "testRepo"
                        , new URL(remoteServerUrl), remoteServerUser, remoteServerPass
                        , null, null, null
                        , null, FileUtil.vSet( pgpRF, sha1F )
                        , null, FileUtil.vSet( pgpWF, sha1F )
                                        );
    
//    localRepoDir = File.createTempFile( "local-", "-repo" );
    localRepoDir = new File( "./target/local" );
    localRepoDir.delete();
    localRepoDir.mkdir();
    
    localRepo = new LocalRepositoryM2( "testLocalRepo", localRepoDir, pm.findDependencyProcessor() );
    
    repos = new ArrayList<Repository>();
    repos.add( localRepo );
    repos.add( remoteRepo );
    
    vrr = new VirtualRepositoryReader(repos);
    
  }
  //-------------------------------------------------------------------------------------
  @Override
  protected void tearDown()
  throws Exception
  {
    if( remoteServerUrl == null )
      return;
    
    super.tearDown();
  }
  //----------------------------------------------------------------------------------------------
  private static boolean assertHasArtifact( List<ArtifactMetadata> res, String gav )
  {
    ArtifactMetadata gavMd = new ArtifactMetadata(gav);
    
    for( ArtifactBasicMetadata md : res )
      if( md.sameGAV( gavMd ) )
        return true;
    
    return false;
  }
  //-------------------------------------------------------------------------------------
  public void testDummy()
  {
    System.out.println("Have to disable plexus tests - need to fix maven-mercury first");
  }
  //-------------------------------------------------------------------------------------
  public void testWrite()
  throws RepositoryException
  {
    pm.write( localRepo, a );
    
    File af = new File( localRepoDir, "org/apache/maven/mercury/mercury-core/2.0.9/mercury-core-2.0.9.jar" );
    
    assertTrue( af.exists() );
  }
  //-------------------------------------------------------------------------------------
  public void testRead()
  throws RepositoryException
  {
    ArtifactMetadata bmd = new ArtifactMetadata(artifactCoord);
    
    Collection<Artifact> res = pm.read( repos, bmd );
    
    assertNotNull( res );
    
    assertFalse( res.isEmpty() );
    
    Artifact a = res.toArray( new Artifact[1] )[0];
    
    assertNotNull( a );
    
    File fBin = a.getFile();
    
    assertNotNull( fBin );

    assertTrue( fBin.exists() );
    
    byte [] pomBytes = a.getPomBlob();
    
    assertNotNull( pomBytes );
    
    assertTrue( pomBytes.length > 10 );
  }
  //-------------------------------------------------------------------------------------
  public void testReadNonExistent()
  {
    ArtifactMetadata bmd = new ArtifactMetadata( "does.not:exist:1.0" );
    
    Collection<Artifact> res = null;
    try
    {
        res = pm.read( repos, bmd );
    }
    catch ( RepositoryException e )
    {
        fail( "reading non-existent artifact should not raise an exception, got "+e.getMessage() );
    }
    
    assertNull( res );
  }
  //-------------------------------------------------------------------------------------
  public void testResolve()
  throws Exception
  {
    Server central = new Server( "central", new URL("http://repo1.maven.org/maven2") );
//    Server central = new Server( "central", new URL("http://repository.sonatype.org/content/groups/public") );
    
    repos.add( new RemoteRepositoryM2(central, pm.findDependencyProcessor()) );

    String artifactId = "asm:asm-xml:3.0";

    List<ArtifactMetadata> res = pm.resolve( repos, ArtifactScopeEnum.compile, new ArtifactQueryList(artifactId), null, null );
    
    System.out.println("Resolved as "+res);

    assertEquals( 4, res.size() );
    
    assertTrue( assertHasArtifact( res, "asm:asm-xml:3.0" ) );
    assertTrue( assertHasArtifact( res, "asm:asm-util:3.0" ) );
    assertTrue( assertHasArtifact( res, "asm:asm-tree:3.0" ) );
    assertTrue( assertHasArtifact( res, "asm:asm:3.0" ) );
  }
  //-------------------------------------------------------------------------------------
  @SuppressWarnings("unchecked")
  public void ntestResolveWithExclusion()
  throws Exception
  {
    Server central = new Server( "central", new URL("http://repo1.maven.org/maven2") );
//    Server central = new Server( "central", new URL("http://repository.sonatype.org/content/groups/public") );
    
    repos.add( new RemoteRepositoryM2(central, pm.findDependencyProcessor()) );

    String artifactId = "asm:asm-xml:3.0";

    List<ArtifactMetadata> res = pm.resolve( repos
                                            , ArtifactScopeEnum.compile
                                            , new ArtifactQueryList(artifactId)
                                            , null
                                            , new ArtifactExclusionList("asm:asm:3.0")
                                           );
    
    System.out.println("Resolved as "+res);

    assertEquals( 3, res.size() );
    
    assertTrue( assertHasArtifact( res, "asm:asm-xml:3.0" ) );
    assertTrue( assertHasArtifact( res, "asm:asm-util:3.0" ) );
    assertTrue( assertHasArtifact( res, "asm:asm-tree:3.0" ) );
    assertFalse( assertHasArtifact( res, "asm:asm:3.0" ) );
  }
  //-------------------------------------------------------------------------------------
  //-------------------------------------------------------------------------------------
}
