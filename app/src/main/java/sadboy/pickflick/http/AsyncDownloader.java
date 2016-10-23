package sadboy.pickflick.http;

import android.os.AsyncTask;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class AsyncDownloader extends AsyncTask<String, Integer, String>{

    private String jsonData = null;

    public interface JsonDataSetter {
        void setJsonData(String str);
    }


    JsonDataSetter jsonDataSetterListener;

    public AsyncDownloader(JsonDataSetter context) {

       this.jsonDataSetterListener = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String url = params[0];

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);

        Response response = null;

        try {
            response = call.execute();

            if (response.isSuccessful()) {
                jsonData = response.body().string();

            } else {
                jsonData = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonData;
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);
        jsonDataSetterListener.setJsonData(jsonData);
    }


}