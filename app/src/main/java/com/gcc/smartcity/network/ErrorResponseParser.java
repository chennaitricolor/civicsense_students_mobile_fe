package com.gcc.smartcity.network;

public class ErrorResponseParser<T> implements ResponseParser {

    private Class<T> mClz;

    @Override
    public <T> T parse(String response) {
        return null;
    }
}
