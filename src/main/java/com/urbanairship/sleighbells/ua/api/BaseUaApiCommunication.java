package com.urbanairship.sleighbells.ua.api;

import com.urbanairship.sleighbells.ua.model.App;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class BaseUaApiCommunication {

    private static final String HOST = "go.urbanairship.com";

    protected static final String BASE_URL = "https://" + HOST + "/";

    protected static final DefaultHttpClient CLIENT = new DefaultHttpClient();


    public BaseUaApiCommunication(App app) {
        CLIENT.getCredentialsProvider().setCredentials(
                new AuthScope(HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(app.getAppkey(), app.getSecret()));
    }

    public HttpGet getGet(String path) {
        return new HttpGet(BASE_URL + path);
    }

    public HttpPost getPost(String path) {
        return new HttpPost(BASE_URL + path);
    }

    public void shutdown() {
        CLIENT.getConnectionManager().shutdown();
    }


}
