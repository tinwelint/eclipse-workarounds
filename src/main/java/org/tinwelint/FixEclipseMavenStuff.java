package org.tinwelint;

import java.io.IOException;

public class FixEclipseMavenStuff
{
    public static void main( String[] args ) throws IOException
    {
        RemoveEclipseInclusionExclusion.main( args );
        RemoveMavenBuilders.main( args );
    }
}
