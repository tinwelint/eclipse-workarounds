package org.tinwelint;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SortTestResultsByTime
{
    public static void main( String[] args ) throws Exception
    {
        File eclipseTestResultXmlFile = new File( args[0] );
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        final Map<String,Double> tests = new HashMap<>();
        parser.parse( eclipseTestResultXmlFile, new DefaultHandler()
        {
            private final LinkedList<String> suite = new LinkedList<>();

            @Override
            public void startElement( String uri, String localName, String qName, Attributes attributes )
                    throws SAXException
            {
                if ( qName.equals( "testsuite" ) )
                {
                    suite.add( attributes.getValue( "name" ) );
                }
                else if ( qName.equals( "testcase" ) )
                {
                    if ( attributes.getValue( "time" ) != null )
                    {
                        double time = Double.parseDouble( attributes.getValue( "time" ) );
                        if ( time >= 0.3 )
                        {
                            tests.put( fullName( attributes.getValue( "name" ) ), time );
                        }
                    }
                }
            }

            private String fullName( String testName )
            {
                StringBuilder result = new StringBuilder();
                for ( String name : suite )
                {
                    result.append( result.length() > 0 ? "#" : "" ).append( name );
                }
                result.append( result.length() > 0 ? "#" : "" ).append( testName );
                return result.toString();
            }

            @Override
            public void endElement( String uri, String localName, String qName ) throws SAXException
            {
                if ( qName.equals( "testsuite" ) )
                {
                    suite.removeLast();
                }
            }
        } );

        Map.Entry[] entries = tests.entrySet().toArray( new Map.Entry[tests.size()] );
        Arrays.sort( entries, new Comparator<Map.Entry>()
        {
            @Override
            public int compare( Map.Entry o1, Map.Entry o2 )
            {
                double t1 = ((Double)o1.getValue()).longValue();
                double t2 = ((Double)o2.getValue()).longValue();
                return Double.compare( t2, t1 );
            }
        } );

        for ( Entry entry : entries )
        {
            System.out.println( entry );
        }
    }
}
