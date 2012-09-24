package org.tinwelint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class RemoveEclipseInclusionExclusion
{
    public static void main( String[] args ) throws IOException
    {
        for ( String arg : args )
            removeInclusionExclusion( new File( arg ), 0 );
    }

    private static void removeInclusionExclusion( File file, int depth ) throws IOException
    {
        if ( file.isDirectory() && depth < 2 )
            for ( File sub : file.listFiles() )
                removeInclusionExclusion( sub, depth+1 );
        else if ( file.getName().equals( ".classpath" ) )
            removeFromDotClasspathFile( file );
    }

    private static void removeFromDotClasspathFile( File file ) throws IOException
    {
        List<String> lines = new ArrayList<String>();
        boolean changed = false;
        for ( String line : loadTextFile( file ) )
            if ( purgeLine( line, lines ) )
                changed = true;
        
        if ( changed )
        {
            System.out.println( "Fixed " + file.getParentFile() );
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
    
    private static boolean purgeLine( String line, List<String> lines )
    {
        String result = purgeFrom( line, " excluding=\"" );
        result = purgeFrom( result, " including=\"" );
        lines.add( result );
        return !line.equals( result );
    }

    private static String purgeFrom( String line, String find )
    {
        int beginIndex = line.indexOf( find );
        if ( beginIndex != -1 )
        {
            int firstQuote = line.indexOf( '"', beginIndex );
            int end = line.indexOf( '"', firstQuote+1 );
            line = line.substring( 0, beginIndex ) + line.substring( end+1 );
        }
        return line;
    }
}
