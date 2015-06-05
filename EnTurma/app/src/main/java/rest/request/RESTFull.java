package rest.request;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

/**
 * Created by gabriel on 6/5/15.
 */
public class RESTFull {

    //change "192.168.0.14" to the ip of your machine
    //in production mode put url
    private static final String URL_BASE = "http://192.168.0.14:3000/";
    private Map<String, String> params;

    public RESTFull(Map params){
        this.params = params;
    }

    private String encodeParamsToUrlRequest(){

        String urlParams = "?utf8=✓";
        for (Map.Entry<String, String> currentParam : params.entrySet()) {
            String key = currentParam.getKey();
            String value = "";
            try {
                value = URLEncoder.encode(currentParam.getValue(), "UTF-8");
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            urlParams += "&" + key + "=" + value;
        }
        return urlParams;
    }

    public void requestReport(){
        
    }

}
