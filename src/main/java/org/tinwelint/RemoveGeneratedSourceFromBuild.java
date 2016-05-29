package org.tinwelint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.tinwelint.FileUtils.loadTextFile;
import static org.tinwelint.FileUtils.saveTextFile;

public class RemoveGeneratedSourceFromBuild
{
    public static void main( String[] args ) throws IOException
    {
        for ( String arg : args )
            for ( File classpathFile : FileUtils.listFilesRecursively( new File( arg ), 3, FileUtils.fileFilter( ".classpath" ) ) )
                removeFromDotClasspathFile( classpathFile );
    }

    private static void removeFromDotClasspathFile( File file ) throws IOException
    {
        List<String> lines = new ArrayList<String>();
        boolean changed = false;
        boolean purgeMode = false;
        for ( String line : loadTextFile( file ) )
        {
            if ( line.trim().startsWith( "<classpathentry" ) &&
                    line.contains( "path=\"target/generated-sources/version\"" ) )
            {
                purgeMode = true;
                changed = true;
            }

            if ( !purgeMode )
            {
                lines.add( line );
            }

            if ( purgeMode && line.trim().startsWith( "</classpathentry>" ) )
            {
                purgeMode = false;
            }
        }

        if ( changed )
        {
            System.out.println( "Fixed " + file.getAbsolutePath() );
            saveTextFile( file, lines );
        }
    }
}
