package com.sewn.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class Robot {
	
     private HashSet<String> urls;
     private String rootDirectory;

     private static String _UA = "User-agent: ";
     private static String _DISALLOWED = "Disallow: ";

     public Robot(String file)
     {
         urls = new HashSet<String>();
         rootDirectory = file.substring(0, file.lastIndexOf('/'));
         process(file);
     }

     private void process(String file)
     {
      	 URL url;
      	 HttpURLConnection webClient;
      	 try {
			 url = new URL(file);
			 webClient = (HttpURLConnection) url.openConnection();
			 InputStream data = webClient.getInputStream();
	    	 InputStreamReader read = new InputStreamReader(data);
	    	 StringBuilder sb=new StringBuilder();
	         BufferedReader br = new BufferedReader(read);
	         String rd = br.readLine();
	         while(rd != null) {
	             sb.append(rd+"\n");
	             rd =br.readLine();
	         }
	         String content = sb.toString();
	         String[] lines = content.split("\n");
	         for (String line : lines)
	             if (!line.startsWith(_UA) && line.startsWith(_DISALLOWED))               
	                 urls.add(rootDirectory + line.substring(_DISALLOWED.length(), line.length()));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }


     public Boolean visit(String link)
     {
         for (String url : urls)
             if (link.contains(url))
                 return false;

         return true;
     }


}
