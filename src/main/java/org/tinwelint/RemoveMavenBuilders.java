package org.tinwelint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.tinwelint.FileUtils.fileFilter;
import static org.tinwelint.FileUtils.listFilesRecursively;
import static org.tinwelint.RemoveEclipseInclusionExclusion.loadTextFile;
import static org.tinwelint.RemoveEclipseInclusionExclusion.saveTextFile;

public class RemoveMavenBuilders
{
    public static void main( String[] args ) throws IOException
    {
        for ( String arg : args )
            for ( File projectFile : listFilesRecursively( new File( arg ), 3, fileFilter( ".project" ) ) )
                removeFromDotProjectFile( projectFile );
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
            System.out.println( "Fixed " + file.getAbsolutePath() );
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
