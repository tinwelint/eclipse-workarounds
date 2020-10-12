package org.tinwelint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WhichTestHangedInCI
{
    public static void main( String[] args ) throws IOException
    {
        File file = new File( args[0] );
        Set<String> tests = new HashSet<>();
        try ( BufferedReader reader = new BufferedReader( new FileReader( file ) ) )
        {
            String line;
            while ( (line = reader.readLine()) != null )
            {
                int index = indexAfter( line, "] Running " );
                if ( index != -1 )
                {
                    String test = line.substring( index );
                    if ( !tests.add( test ) )
                    {
                        System.out.println( "HMM, couldn't add " + test + " it was already there" );
                    }
                }
                else if ( line.contains( "] Tests run:" ) )
                {
                    index = indexAfter( line, " - in " );
                    if ( index != -1 )
                    {
                        String test = line.substring( index );
                        if ( !tests.remove( test ) )
                        {
                            System.out.println( "HMM, couldn't remove " + test + " it wasn't in the set" );
                        }
                    }
                }
            }
        }

        tests.forEach( System.out::println );
    }

    private static int indexAfter( String line, String toSearchFor )
    {
        int index = line.indexOf( toSearchFor );
        if ( index == -1 )
        {
            return index;
        }
        return index + toSearchFor.length();
    }
}
