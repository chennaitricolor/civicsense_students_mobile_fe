package com.gcc.smartcity.network;

public interface ResponseParser {

    public <T> T parse(String response);

}
