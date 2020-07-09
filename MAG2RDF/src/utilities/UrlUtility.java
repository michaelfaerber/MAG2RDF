package utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class UrlUtility {

	public UrlUtility() {
		// TODO Auto-generated constructor stub
	}
	
	public  Boolean isValid(String url_path) throws IOException {
		
		//System.out.println(url_path);
		return true;
//		try {
//			
//	        URL url = new URL(url_path);
//	        URLConnection connection = url.openConnection();
//	        connection.setConnectTimeout(5000);
//	        connection.setReadTimeout(5000);
//	        InputStream i = null;
//
//	        try {
//	        	i = connection.getInputStream();
//	        } catch (Exception ex) {
//	        	System.out.println(ex.toString());
//	            return(false);
//	        }
//
//	        if (i != null) {
//	            return(true);
//	        }
//
//	    } catch (MalformedURLException e) {
//	        return(false);
//	    }
//		return(true);
	}
}


