/*
File created by com.ibm.cognos.internal.scg.toolbox.JarJad on Wed Nov 09 11:03:29 EST 2011
Decompiled from C:\Projects\Regression\BurpRegressionTesting.jar
*/

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: fullnames 

package burp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Request
{

    public Request(byte request[])
    {	
        headers = new LinkedHashMap<String, String>();
        messageString = new StringBuffer();
        read(request);
        
        String s = (String) headers.get("Host");
        int i = s.indexOf(':');
        if(i != -1)
        {
            host = s.substring(0, i - 1);
            port = Integer.valueOf(s.substring(i + 1)).intValue();
        } else
        {
            host = s;
            port = 80;
        }
    }

    public Request(String host, int port, byte request[])
    {
    	
        headers = new LinkedHashMap<String, String>();
        messageString = new StringBuffer();
        read(request);
        setHost(host, port);
    }

    public String getHost()
    {
        return host.trim();
    }

    public void setHost(String h, int p)
    {
        int j = -1;
        if(host == null)
            j = query.indexOf(h);
        else
            j = query.indexOf(host);
        if(j != -1)
        {
            StringBuffer stringbuffer = new StringBuffer();
            stringbuffer.append(query.substring(0, j));
            stringbuffer.append(h);
            if(p != 80)
                stringbuffer.append((new StringBuilder()).append(":").append(p).toString());
            stringbuffer.append(query.substring(j + host.length()));
            query = stringbuffer.toString();
        }
        headers.put("Host", (new StringBuilder()).append(h).append(":").append(p).toString());
        host = h;
        port = p;
    }

    public int getPort()
    {
        return port;
    }

    public void setCookies(String cookies)
    {
        headers.put("Cookie", cookies);
    }

    public String getQuery()
    {
        return query;
    }

    public String getQueryPath()
    {
        int i = 0;
        if(query.indexOf(host) != -1)
            i = query.indexOf(host) + host.length();
        String s = query.substring(i, query.lastIndexOf('/'));
        return s;
    }

    public void setHostAndPath(String h, int p, String path)
    {
        java.lang.StringBuffer stringbuffer = new StringBuffer();
        setHost(h, p);
        int j = query.indexOf(host);
        if(j != -1)
        {
            j = query.indexOf('/', j);
            stringbuffer.append(query.substring(0, j + 1));
        }
        stringbuffer.append(path);
        stringbuffer.append(query.substring(query.lastIndexOf('/')));
        query = stringbuffer.toString();
    }

    public void read(byte request[])
    {
        read(((InputStream) (new ByteArrayInputStream(request))));
    }

    public void read(InputStream request)
    {
        try
        {
            int i = request.read();
            do
            {
            	// end when we encounter a fully blank line
                if(i == 10 || i == -1)
                    break;
                
                for(; i != 10 && i != -1; i = request.read())
                    messageString.append((char)i);

                messageString.append(LINEFEED);
                i = request.read();
                
                // eat the carriage return
                if(i == 13)
                    i = request.read();
                
            } while(true);
            messageString.append(LINEFEED);
            
            parseHeaders();
            readContent(request);
        }
        catch(IOException ioexception)
        {
            return;
        }
    }

    public void parseHeaders()
        throws IOException
    {
        String lines[] = messageString.toString().split(LINEFEED);
        String queryString = lines[0];
        String queryParts[] = queryString.split(" ");
        
        if(queryParts.length < 3)
            throw new IOException("unable to parse the header properly");
        
        method = queryParts[0];
        query = queryParts[1];
        httpType = queryParts[2];
        
        for(int i = 1; i < lines.length; i++)
        {
           
            String header = lines[i].substring(0, lines[i].indexOf(':'));
            
            StringBuffer value = new StringBuffer();
            value.append(lines[i].substring(lines[i].indexOf(':') + 1));
            
            // deal with continuation headers
            if(i < lines.length - 1 && lines[i + 1].startsWith(" "))
            {
                value.append((new StringBuilder()).append(value).append(LINEFEED).append(lines[i + 1]).toString());
                i++;
            }
            
            headers.put(header, value.toString());
        }

    }

    private void readContent(InputStream c)
    {
        int i = 0;

        content = new StringBuffer();
        byte[] buf = new byte[50];
        
        do {
            try
            {
                i = c.read(buf, 0, 50);
                if ( i > 0 ) {
                	content.append(new String(Arrays.copyOfRange(buf, 0, i)));
                }
            }
            catch(IOException ioexception)
            {
                return;
            }
            catch(IndexOutOfBoundsException indexoutofboundsexception)
            {
                return;
            }
        } while (i > 0);
            

    }

    public byte[] getRequest()
    {
        StringBuffer buffer = new StringBuffer();
        
        byte request[] = null;
        // build the query string
        buffer.append((new StringBuilder()).append(method).append(" ").append(query).append(" ").append(httpType).toString());
        buffer.append(LINEFEED);
        if(content == null || content.length() == 0)
        {
            // no content length to worry about in this case
            buffer.append(printHeaders());
            buffer.append(LINEFEED);
            request = buffer.toString().getBytes();
        } else {
            // update the content length first
        	headers.put("Content-Length", String.valueOf(content.toString().getBytes().length));
        	buffer.append(printHeaders());
            buffer.append(LINEFEED);
            
        	int i = 0;
            request = new byte[buffer.toString().getBytes().length + content.toString().getBytes().length];
            for(int j = 0; j < buffer.toString().getBytes().length; j++) {
                request[i++] = buffer.toString().getBytes()[j];
            }

            for(int j = 0; j < content.toString().getBytes().length; j++)
            {
                
                request[i++] = content.toString().getBytes()[j];
            }

        }
        return request;
    }

    private java.lang.String printHeaders()
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(Iterator<Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext(); stringbuffer.append(LINEFEED))
        {
            Entry<String, String> entry = iterator.next();
            stringbuffer.append(entry.getKey());
            stringbuffer.append(':');
            stringbuffer.append(entry.getValue());
        }

        return stringbuffer.toString();
    }

    

	private java.lang.String host;
    private int port;
    private transient String method;
    private transient String query;
    private transient String httpType;
    private transient Map<String, String> headers;
    private final StringBuffer messageString;
    private transient StringBuffer content;
    public static final String LINEFEED = "\n";
}
