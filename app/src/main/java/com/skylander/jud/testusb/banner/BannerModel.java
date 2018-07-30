package com.skylander.jud.testusb.banner;

import java.io.Serializable;

public class BannerModel implements Serializable{

    private String uri;
    private String type;

    public BannerModel(String uri, String type) {
        this.uri = uri;
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
