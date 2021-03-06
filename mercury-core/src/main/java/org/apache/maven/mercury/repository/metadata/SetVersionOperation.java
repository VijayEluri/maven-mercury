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

import org.codehaus.plexus.lang.DefaultLanguage;
import org.codehaus.plexus.lang.Language;

/**
 * adds version to metadata
 * 
 * @author Oleg Gusakov
 * @version $Id$
 */
public class SetVersionOperation
    implements MetadataOperation
{
    private static final Language LANG = new DefaultLanguage( SetVersionOperation.class );

    private String version;

    /**
     * @throws MetadataException
     */
    public SetVersionOperation( StringOperand data )
        throws MetadataException
    {
        setOperand( data );
    }

    public void setOperand( Object data )
        throws MetadataException
    {
        if ( data == null || !( data instanceof StringOperand ) )
        {
            throw new MetadataException( LANG.getMessage( "bad.operand", "SnapshotOperand", data == null ? "null"
                            : data.getClass().getName() ) );
        }

        version = ( (StringOperand) data ).getOperand();
    }

    /**
     * add version to the in-memory metadata instance
     * 
     * @param metadata
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

        String vs = metadata.getVersion();

        if ( vs == null )
        {
            if ( version == null )
            {
                return false;
            }
        }
        else if ( vs.equals( version ) )
        {
            return false;
        }

        metadata.setVersion( version );

        return true;
    }

}
