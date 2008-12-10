package org.apache.maven.mercury.ant.tasks;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.maven.mercury.MavenDependencyProcessor;
import org.apache.maven.mercury.builder.api.DependencyProcessor;
import org.apache.maven.mercury.repository.api.Repository;
import org.apache.maven.mercury.repository.local.m2.LocalRepositoryM2;
import org.apache.maven.mercury.repository.remote.m2.RemoteRepositoryM2;
import org.apache.maven.mercury.transport.api.Server;
import org.apache.maven.mercury.util.Util;

/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public class Config
extends AbstractDataType
{
  Collection<Repo> _repositories;
  
  public Collection<Repository> getRepositories()
  throws MalformedURLException
  {
    if( Util.isEmpty( _repositories ) )
      return null;
    
    Collection<Repository> repos = new ArrayList<Repository>( _repositories.size() );
    
    for( Repo repo : _repositories )
    {
      if( repo.isLocal() )
      {
        DependencyProcessor dp = new MavenDependencyProcessor();
        
        LocalRepositoryM2 r = new LocalRepositoryM2( repo.getId(), new File( repo._dir ), dp  );
        
        repos.add( r );
      }
      else
      {
        DependencyProcessor dp = new MavenDependencyProcessor();
        
        Server server = new Server( repo.getId(), new URL( repo._url ) );
        
        RemoteRepositoryM2 r  = new RemoteRepositoryM2( server, dp  );
        
        repos.add( r );
      }
    }
    
    return repos;
  }
  
  public Repo createRepo()
  {
    if( _repositories == null )
    _repositories = new ArrayList<Repo>(4);
    
    Repo r = new Repo();
    
    _repositories.add( r );
    
    return r;
  }
  
  public class Repo
  extends AbstractDataType
  {
    String _dir;
    String _url;
    String _type;

    public void setUrl( String url )
    {
      this._url = url;
    }

    public void setDir( String dir )
    {
      this._dir = dir;
    }

    public void setType( String type )
    {
      this._type = type;
    }
    
    boolean isLocal()
    {
      return _dir != null;
    }
  }

}
