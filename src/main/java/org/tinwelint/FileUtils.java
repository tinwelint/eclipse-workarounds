package org.tinwelint;

import java.io.File;
import java.io.FileFilter;
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

    private FileUtils()
    {
    }
}
