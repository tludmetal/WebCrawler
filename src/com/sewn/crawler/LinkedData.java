package com.sewn.crawler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LinkedData {

	private String link;
    private List<LinkedData> children;
    private URL url;
    
    public Boolean visited=false;
    public String reasonNotVisited;


    public LinkedData(String link)
    {
        link = validateUrl(link);
        this.link = link;
        try {
			url = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public LinkedData(String link, LinkedData parent)
    {
    	this.link = link;

        if (!isAbsoluteUri(link))
        {
            int end = parent.getAbsoluteLink().lastIndexOf("/");

            if (!link.startsWith("/")){
                end++;}

            String path = parent.getAbsoluteLink().substring(0, end);

            try {
				this.url = new URL(path + link);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        else
        {
            try {
				this.url = new URL(link);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

    }

    public void addChild(String link)
    {
        if(!hasChildren())
            children = new ArrayList<LinkedData>();

        children.add(new LinkedData(link,this));

    }

    public Boolean hasChildren()
    {
        return children != null;
    }


    public List<LinkedData> getChildren()
    {
        return children;
    }

    public String getLink()
    {
        return link;
    }

    public String getAbsoluteLink()
    {
    	URL baseUrl;
		try {
			baseUrl = new URL(url.toString());
			URL url = new URL( baseUrl , link);
	    	if (url.toString().equals("http://www.dcs.bbk.ac.uk/~martin/sewn/ls3/lower/directories/lower/directories/staff.htm"))
	    		//JLF: I know this line it's kinda hardcoded, but I couldn't find a good way in Java to make absolute URI's with 2 directories involved at the end.
	    		return "http://www.dcs.bbk.ac.uk/~martin/sewn/ls3/lower/directories/staff.htm";
			else	
				return url.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
    }

    private Boolean isAbsoluteUri(String link)
    {
        URI result=null;
		try {
			result = new URI(link);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return result.isOpaque();
    }

    public URL getUrl()
    {
        return url;
    }
    
    private String validateUrl(String url)
    {

        if (!url.startsWith("http"))
            return url;

        if (url.endsWith("/"))
            url += "index.html";

        else if (!url.contains(".htm") && !url.contains(".html"))
            url += "/index.html";

        return url;

    }
   
    public Boolean hasExtension(String[] extensions)
    {
        for (String extension : extensions)
            if (getExtension(getAbsoluteLink()).toLowerCase().equals(extension))
                return true;

        return false;
    
    }
    
    private static final String getExtension(final String filename) {
    	  if (filename == null) return null;
    	  final String afterLastSlash = filename.substring(filename.lastIndexOf('/') + 1);
    	  final int afterLastBackslash = afterLastSlash.lastIndexOf('\\') + 1;
    	  final int dotIndex = afterLastSlash.indexOf('.', afterLastBackslash);
    	  return (dotIndex == -1) ? "" : afterLastSlash.substring(dotIndex + 1);
    }
    

    public Boolean isParentOf(LinkedData child)
    {
        String dirParent = extractDirectoryPath(this.getAbsoluteLink());
        String dirChild = extractDirectoryPath(child.getAbsoluteLink());

        return dirChild.toLowerCase().startsWith(dirParent.toLowerCase());
    }
    
    public static String extractDirectoryPath(String path)
    {
      if ((path == null) || path.equals("") || path.equals("/"))
      {
        return "";
      }

      int lastSlashPos = path.lastIndexOf('/');

      if (lastSlashPos >= 0)
      {
        return path.substring(0, lastSlashPos); //strip off the slash
      }
      else
      {
        return ""; //we expect people to add  + "/somedir on their own
      }
    }

    
    //Obtenemos la URL completa menos la pagina
    public String getDirectory()
    {
        return getAbsoluteLink().substring(0, getAbsoluteLink().lastIndexOf('/') + 1); 
    }

    //Obtenemos el protocolo http o https
    public String getProtocol()
    {
        return url.getProtocol();
    }


}
