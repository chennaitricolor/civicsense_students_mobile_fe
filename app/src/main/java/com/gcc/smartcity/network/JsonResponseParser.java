package com.gcc.smartcity.network;

import com.bluelinelabs.logansquare.LoganSquare;
import com.gcc.smartcity.utils.Logger;

import java.io.IOException;

public class JsonResponseParser<T> implements ResponseParser {

    private Class<T> mClz;

    public JsonResponseParser(Class<T> clz) {
        mClz = clz;
    }

    @Override
    public T parse(String response) {
        try {
            Logger.d("Response -- " + response);
            return LoganSquare.parse(response, mClz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
