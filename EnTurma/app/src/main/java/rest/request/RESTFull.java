package rest.request;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;


public class RESTFull {

    //change "192.168.0.14" to the ip of your machine
    //in production mode put url
    private static final String URL_BASE = "http://www.projetoenturma.com.br/";
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

    public void requestReport(JsonHttpResponseHandler handler){
        String requestUrl = URL_BASE + "report/request_report.json" + encodeParamsToUrlRequest();

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(requestUrl, handler);
    }

    public  void requestCompareReport(JsonHttpResponseHandler handler){
        String requestUrl = URL_BASE + "compare_reports/request_comparation.json" + encodeParamsToUrlRequest();

        AsyncHttpClient client = new AsyncHttpClient();

        System.out.println(requestUrl);

        client.get(requestUrl, handler);
    }


}
