package com.urbanairship.sleighbells.ua.api;

import com.urbanairship.sleighbells.api.DeviceIdentifierSource;
import com.urbanairship.sleighbells.ua.model.App;
import com.urbanairship.sleighbells.ua.model.Device;
import com.urbanairship.sleighbells.ua.model.DeviceFamily;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DeviceIdentifierSourceUaApiImpl extends BaseUaApiCommunication implements DeviceIdentifierSource {

    private static final String DEVICE_TOKENS_PATH = "api/device_tokens/";
    private static final String APIDS_PATH = "api/apids/";

    public DeviceIdentifierSourceUaApiImpl(App app) {
        super(app);
    }

    @Override
    public List<Device> getIdentifiers() {
        List<Device> devices = new ArrayList<Device>();
        devices.addAll(getDeviceTokens());
        devices.addAll(getApids());
        return devices;
    }

    private List<Device> getDeviceTokens() {
        List<Device> devices = new LinkedList<Device>();
        DeviceTokenListing deviceTokenListing = pageDeviceTokens(BASE_URL + DEVICE_TOKENS_PATH, devices);
        while (deviceTokenListing.getNextPage() != null && !deviceTokenListing.getNextPage().isEmpty()) {
            deviceTokenListing = pageDeviceTokens(deviceTokenListing.getNextPage(), devices);
        }
        return devices;
    }

    private DeviceTokenListing pageDeviceTokens(String uri, List<Device> devices) {
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpGet get = new HttpGet(uri);
        try {
            String responseBody = CLIENT.execute(get, responseHandler);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            final DeviceTokenListing tokenListing = mapper.readValue(responseBody, DeviceTokenListing.class);
            for (DeviceToken deviceToken : tokenListing.getDeviceTokens()) {
                devices.add(new Device(deviceToken.deviceToken, DeviceFamily.IOS));
            }
            return tokenListing;
        } catch (Exception e) {
            throw new RuntimeException("error paging device tokens", e);
        }
    }

    private List<Device> getApids() {
        List<Device> devices = new LinkedList<Device>();
        ApidListing apidListing = pageApids(BASE_URL + APIDS_PATH, devices);
        while (apidListing.getNextPage() != null && !apidListing.getNextPage().isEmpty()) {
            apidListing = pageApids(apidListing.getNextPage(), devices);
        }
        return devices;
    }

    private ApidListing pageApids(String uri, List<Device> devices) {
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        HttpGet get = new HttpGet(uri);
        try {
            String responseBody = CLIENT.execute(get, responseHandler);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
            final ApidListing tokenListing = mapper.readValue(responseBody, ApidListing.class);
            for (Apid apid : tokenListing.getApids()) {
                devices.add(new Device(apid.apid, DeviceFamily.ANDROID));
            }
            return tokenListing;
        } catch (Exception e) {
            throw new RuntimeException("error paging apids", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeviceTokenListing {
        String nextPage;
        List<DeviceToken> deviceTokens;

        public String getNextPage() {
            return nextPage;
        }

        public void setNextPage(String nextPage) {
            this.nextPage = nextPage;
        }

        public List<DeviceToken> getDeviceTokens() {
            return deviceTokens;
        }

        public void setDeviceTokens(List<DeviceToken> deviceTokens) {
            this.deviceTokens = deviceTokens;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class DeviceToken {
        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        String deviceToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ApidListing {
        String nextPage;
        List<Apid> apids;

        public String getNextPage() {
            return nextPage;
        }

        public void setNextPage(String nextPage) {
            this.nextPage = nextPage;
        }

        public List<Apid> getApids() {
            return apids;
        }

        public void setApids(List<Apid> apids) {
            this.apids = apids;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Apid {
        String apid;

        public String getApid() {
            return apid;
        }

        public void setApid(String apid) {
            this.apid = apid;
        }
    }


}
