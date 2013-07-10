/*
Copyright 2013 Urban Airship and Contributors
*/
package com.urbanairship.sleighbells.ua.model;

public class App {

    private final String appkey;
    private final String masterSecret;

    public App(String appkey, String masterSecret) {
        this.appkey = appkey;
        this.masterSecret = masterSecret;
    }

    public String getAppkey() {
        return appkey;
    }

    public String getSecret() {
        return masterSecret;
    }
}
