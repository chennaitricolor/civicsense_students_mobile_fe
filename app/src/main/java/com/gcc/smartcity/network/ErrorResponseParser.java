package com.gcc.smartcity.network;

/**
 * Created by NSM Services on 6/16/16.
 */
public class ErrorResponseParser<T> implements ResponseParser {

    private Class<T> mClz;

  /*  public ErrorResponseParser(){

    }*/

    @Override
    public <T> T parse(String response) {
        return null;
    }
}
