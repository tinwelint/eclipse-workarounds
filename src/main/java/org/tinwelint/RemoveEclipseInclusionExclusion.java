package org.tinwelint;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.tinwelint.FileUtils.loadTextFile;
import static org.tinwelint.FileUtils.saveTextFile;

public class RemoveEclipseInclusionExclusion
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
        for ( String line : loadTextFile( file ) )
            if ( purgeLine( line, lines ) )
                changed = true;

        if ( changed )
        {
            System.out.println( "Fixed " + file.getAbsolutePath() );
            saveTextFile( file, lines );
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
