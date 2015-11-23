package io.subutai.plugin.keshig.impl;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DeployOperationHandlerTest
{
    public static void main( String[] args )
    {
        String s = "management\\d=.*";

        String match = "management1=172.16.131.78 \n management2=172.16.131.79";


        Pattern pattern = Pattern.compile( s );

        Matcher matcher = pattern.matcher( match );

        boolean found = false;
        while ( matcher.find() )
        {
            System.out.println( String.format( "I found the text" +
                    " \"%s\" starting at " +
                    "index %d and ending at index %d.%n", matcher.group(), matcher.start(), matcher.end() ) );
            found = true;
        }
        if ( !found )
        {
            System.out.println( "No match found.%n" );
        }
    }
}
