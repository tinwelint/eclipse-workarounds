package org.tinwelint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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

    static void saveTextFile( File file, List<String> lines ) throws IOException
    {
        PrintStream out = new PrintStream( file );
        try
        {
            for ( String line : lines )
                out.println( line );
        }
        finally
        {
            out.close();
        }
    }

    static List<String> loadTextFile( File file ) throws IOException
    {
        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();
        try
        {
            reader = new BufferedReader( new FileReader( file ) );
            String line = null;
            while ( (line = reader.readLine()) != null )
                lines.add( line );
            return lines;
        }
        finally
        {
            if ( reader != null )
                reader.close();
        }
    }
}
