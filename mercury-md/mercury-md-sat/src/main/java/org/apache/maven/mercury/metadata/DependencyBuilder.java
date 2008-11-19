package org.apache.maven.mercury.metadata;

import java.util.List;

import org.apache.maven.mercury.artifact.ArtifactBasicMetadata;
import org.apache.maven.mercury.artifact.ArtifactMetadata;
import org.apache.maven.mercury.artifact.ArtifactScopeEnum;
import org.apache.maven.mercury.util.event.MercuryEventListener;

/**
 *
 *
 * @author Oleg Gusakov
 * @version $Id$
 *
 */
public interface DependencyBuilder
{
  public static final String TREE_BUILD_EVENT = "tree.build";
  public static final String TREE_NODE_BUILD_EVENT = "tree.node.build";
  //------------------------------------------------------------------------
  /**
   * build the tree, using the repositories specified in the
   * constructor
   * 
   * @param startMD - root of the tree to build
   * @param targetPlatform - limitations to use when retrieving metadata. Format is G:A=V, where V is Version Range
   * @return the root of the tree built
   * @throws MetadataTreeException
   */
  public abstract MetadataTreeNode buildTree( ArtifactBasicMetadata startMD, ArtifactScopeEnum scope )
  throws MetadataTreeException;

  /**
   * hard to believe, but this actually resolves the conflicts, removing all duplicate GAVs from the tree
   * 
   * @param root the tree to resolve conflicts on
   * @return list of resolved GAVs
   * @throws MetadataTreeException
   */
  public abstract List<ArtifactMetadata> resolveConflicts( MetadataTreeNode root )
  throws MetadataTreeException;

  /**
   *  this one resolves the conflicts, removing all duplicate GAVs from the tree and
   *  returning a copy of the resulting subtree - original tree should be intact
   * 
   * @param root the tree to resolve conflicts on
   * @return resolved subtree
   * @throws MetadataTreeException
   */
  public abstract MetadataTreeNode resolveConflictsAsTree( MetadataTreeNode root )
  throws MetadataTreeException;
  
  /**
   * register a listener for dependency events 
   * 
   * @param listener
   */
  public abstract void register( MercuryEventListener listener );
  
  /**
   * remove a listener 
   * 
   * @param listener
   */
  public abstract void unRegister( MercuryEventListener listener );
  //-----------------------------------------------------
  //-----------------------------------------------------

}
