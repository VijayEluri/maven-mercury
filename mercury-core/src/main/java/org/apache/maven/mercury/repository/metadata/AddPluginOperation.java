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
package org.apache.maven.mercury.repository.metadata;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.plexus.lang.DefaultLanguage;
import org.codehaus.plexus.lang.Language;

/**
 * adds new plugin to metadata
 *
 * @author Oleg Gusakov
 * @version $Id$
 */
public class AddPluginOperation
    implements MetadataOperation
{
    private static final Language LANG = new DefaultLanguage( AddPluginOperation.class );

    private Plugin plugin;
    
    private static PluginComparator pluginComparator;
    
    {
        pluginComparator = new PluginComparator();
    }

    /**
     * @throws MetadataException
     */
    public AddPluginOperation( PluginOperand data )
        throws MetadataException
    {
        setOperand( data );
    }

    public void setOperand( Object data )
        throws MetadataException
    {
        if ( data == null || !( data instanceof PluginOperand ) )
        {
            throw new MetadataException( LANG.getMessage( "bad.operand", "PluginOperand", data == null ? "null"
                            : data.getClass().getName() ) );
        }

        plugin = ( (PluginOperand) data ).getOperand();
    }

    /**
     * add plugin to the in-memory metadata instance
     *
     * @param metadata
     * @param version
     * @return
     * @throws MetadataException
     */
    public boolean perform( Metadata metadata )
        throws MetadataException
    {
        if ( metadata == null )
        {
            return false;
        }

        List<Plugin> plugins = metadata.getPlugins();

        for ( Plugin p : plugins )
        {
            if ( p.getArtifactId().equals( plugin.getArtifactId() ) )
            {
                // plugin already enlisted
                return false;
            }
        }

        // not found, add it
        plugins.add( plugin );

        Collections.sort( plugins, pluginComparator );
        
        return true;
    }
    
    class PluginComparator
        implements Comparator<Plugin>
    {
        public int compare( Plugin p1, Plugin p2 )
        {
            if ( p1 == null || p2 == null )
            {
                throw new IllegalArgumentException( LANG.getMessage( "null.plugin.to.compare" ) );
            }

            if ( p1.getArtifactId() == null || p2.getArtifactId() == null )
            {
                throw new IllegalArgumentException( LANG.getMessage( "null.plugin.artifactId.to.compare", p1
                    .getArtifactId(), p2.getArtifactId() ) );
            }

            return p1.getArtifactId().compareTo( p2.getArtifactId() );
        }
    }

}
