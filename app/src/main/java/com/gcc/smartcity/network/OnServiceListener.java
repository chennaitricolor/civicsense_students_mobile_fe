package com.gcc.smartcity.network;

import com.gcc.smartcity.utils.NetworkError;

public interface OnServiceListener {

    public void onSuccess(int serviceIdentifier, Object response);

    public void onError(int serviceIdentifier, NetworkError networkError);
}
