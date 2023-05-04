package com.bk.bkskup3.db;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;
import java.lang.StringBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 21.06.11
 * Time: 17:17
 */


public class ScriptTokenizer
{
    private char delimiter = ';';
    private char escapeDelimiter = '\\';
    private InputStream stream;


    public ScriptTokenizer(InputStream stream) {
        this.stream = stream;
    }

    /**
      * Parse string
      */
    public List<String> parse() throws IOException {
        List<String> tokens = new LinkedList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuffer item = new StringBuffer();

        int c = reader.read();
        while(c != -1)
        {
            if (isEscapeDelimiter((char)c))
            {
                int nextc = reader.read();
                if(nextc == -1)
                {
                    throw new IOException("unexpected end of stream, expecting a special character after " + escapeDelimiter);
                }

                if(isDelimiter((char)nextc))
                {
                    item.append((char)nextc);
                }
                else
                {
                    item.append((char)c);
                    item.append((char)nextc);
                }

            } else if( isDelimiter((char)c)  )
            {
                tokens.add( item.toString() );
                item = new StringBuffer();
            }
            else
            {
                // Just add to item
                item.append( (char)c );
            }
            c = reader.read();
        }

        String lastStatement = item.toString();
        if(item.length() > 0 && TextUtils.isGraphic(lastStatement)) {
            tokens.add(lastStatement);
        }

        return tokens;
    }


    private boolean isEscapeDelimiter(char c) {

         return  (c == escapeDelimiter);
    }

    private boolean isDelimiter(char c)
    {
        return (c == delimiter);
    }


}

