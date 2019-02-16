package c4l.applet.input;


import java.net.URL;
import java.nio.charset.Charset;
import java.io.*;
import org.json.*;

import c4l.applet.main.Constants;
/**
 * Fragt die Api vom c4l Server ab und sammelt die 
 */
public final class DashboardInput {
	
	
	private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String readStringFromUrl(String url) throws IOException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            // JSONObject json = new JSONObject(jsonText);
            return jsonText;
        } finally {
            is.close();
        }
    }
    
    public JSONObject getResponse() {
    	String ResponsString;
		try {
			ResponsString = readStringFromUrl(Constants.SERVER_ADDRESS+"/"+Constants.INFORMATIONPFAD);
		} catch (IOException e) {
			return null;
		}
    	return new JSONObject(ResponsString);
    }
    

}
