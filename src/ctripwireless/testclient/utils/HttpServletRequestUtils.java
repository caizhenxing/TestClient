package ctripwireless.testclient.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestUtils {
	public static String getHttpServletRequestBody(HttpServletRequest request) throws IOException {
		 String submitMehtod = request.getMethod();  
		 String queryString = null;  
		 String charEncoding = request.getCharacterEncoding();// charset  
		 if (submitMehtod.equals("GET")) {// GET  
			 queryString = request.getQueryString();  
			 if (charEncoding == null) {  
				 charEncoding = "UTF-8";  
			 }  
			 return new String(queryString.getBytes(charEncoding));
		 } else {// POST  
			 return new String(getRequestPostBytes(request),charEncoding);  
		 }  
	}
	
	/*** 
     * Get request query string, form method : post 
     *  
     * @param request 
     * @return byte[] 
     * @throws IOException 
     */  
    private static byte[] getRequestPostBytes(HttpServletRequest request)  throws IOException {  
        int contentLength = request.getContentLength();  
        if(contentLength<0){  
            return null;  
        }  
        byte buffer[] = new byte[contentLength];  
        for (int i = 0; i < contentLength;) {  
  
            int readlen = request.getInputStream().read(buffer, i,  
                    contentLength - i);  
            if (readlen == -1) {  
                break;  
            }  
            i += readlen;  
        }  
        return buffer;  
    }   
}
