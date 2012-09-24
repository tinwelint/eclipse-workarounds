package org.tinwelint;

import static org.tinwelint.RemoveEclipseInclusionExclusion.loadTextFile;
import static org.tinwelint.RemoveEclipseInclusionExclusion.saveTextFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoveMavenBuilders
{
    public static void main( String[] args ) throws IOException
    {
        for ( String arg : args )
            removeMavenBuilders( new File( arg ), 0 );
    }

    private static void removeMavenBuilders( File file, int depth ) throws IOException
    {
        if ( file.isDirectory() && depth < 2 )
            for ( File sub : file.listFiles() )
                removeMavenBuilders( sub, depth+1 );
        else if ( file.getName().equals( ".project" ) )
            removeFromDotProjectFile( file );
    }

    private static void removeFromDotProjectFile( File file ) throws IOException
    {
        String[] lines = loadTextFile( file ).toArray( new String[0] );
        List<String> pruned = new ArrayList<String>();
        boolean changed = false;
        
        int start = -1;
        boolean prune = false;
        for ( int i = 0; i < lines.length; i++ )
        {
            String line = lines[i];
            if ( line.trim().equals( "<buildCommand>" ) ) 
                start = i;
            else if ( line.contains( "org.eclipse.m2e.core.maven2Builder" ) )
            {
                prune = true;
                changed = true;
            }
            else if ( line.trim().equals( "</buildCommand>" ) )
            {
                if ( !prune )
                    for ( int l = start; l < i; l++ )
                        pruned.add( lines[l] );
                start = -1;
            }
            
            if ( start == -1 )
            {
                if ( prune )
                    prune = false;
                else
                    pruned.add( line );
            }
        }
        
        if ( changed )
        {
            System.out.println( "Fixed " + file );
            saveTextFile( file, pruned );
        }
        
        File externalBuildersDir = new File( file.getParentFile(), ".externalToolBuilders" );
        if ( externalBuildersDir.exists() )
            for ( File builder : externalBuildersDir.listFiles() )
            {
                if ( builder.getName().startsWith( "org.eclipse.m2e.core.maven2Builder" ) )
                {
                    builder.delete();
                    System.out.println( "Deleted " + builder );
                }
            }
    }
}
