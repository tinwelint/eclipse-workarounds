package org.tinwelint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{
    public static Iterable<File> listFilesRecursively( File inDirectory, int maxDepth, FileFilter filter )
    {
        List<File> result = new ArrayList<File>();
        gatherFiles( inDirectory, maxDepth, 0, filter, result );
        return result;
    }

    private static void gatherFiles( File file, int maxDepth, int depth, FileFilter filter, List<File> result )
    {
        if ( file.isDirectory() && depth < maxDepth )
            for ( File sub : file.listFiles() )
                gatherFiles( sub, maxDepth, depth+1, filter, result );
        else if ( filter.accept( file ) )
            result.add( file );
    }

    public static FileFilter fileFilter( final String exactFileName )
    {
        return new FileFilter()
        {
            @Override
            public boolean accept( File path )
            {
                return path.isFile() && path.getName().equals( exactFileName );
            }
        };
    }

    public static void saveTextFile( File file, List<String> lines ) throws IOException
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

    public static List<String> loadTextFile( File file ) throws IOException
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

    private FileUtils()
    {
    }
}
