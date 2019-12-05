package com.gcc.smartcity.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidboilerplate.NetworkError;
import com.gcc.smartcity.utils.Logger;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;

/**
 * Created by ktt on 8/31/17.
 */

public class ByteArrayRequest extends Request<byte[]> {

    private String mUrl;
    private static final String TAG = ByteArrayRequest.class.getSimpleName();
    private Map<String, String> mHeaders;
    private OnServiceListener mOnServiceListener;
    private int mServiceIdentifier;
    private ResponseParser mErrorParser;
    private ResponseParser mParser;

    /**
     * For volley library, to set timeout, we need to use RetryPolicy that is provided.
     * The Retry policy behaves as,
     * For ex. If RetryPolicy is created with these values
     * 			Timeout - 3000 secs, Num of Attempt - 2, Back Off Multiplier - 2
     * 	Attempt 1:
     * 		time = time + (time * Back Off Multiplier );
     * 		time = 3000 + 6000 = 9000
     * 		Socket Timeout = time;
     * 		Request dispatched with Socket Timeout of 9 Secs
     * 	Attempt 2:
     * 		time = time + (time * Back Off Multiplier );
     * 		time = 9000 + 18000 = 27000
     * 		Socket Timeout = time;
     * 		Request dispatched with Socket Timeout of 27 Secs
     */
    private final int DEFAULT_TIMEOUT = (int) (60 * 1000); // 30 seconds

    private final int RETRY_COUNT = 1;	// To retry once within the Timeout period

    private final float BACKOFF_MULTIPLIER = 1.0f;	// Volley requires this. Not sure what this is for exactly.

    private static final String NETWORK_AUTHENTICATION_ISSUE_STRING		= "java.io.IOException: No authentication challenges found";

    public ByteArrayRequest(int method, String url) {
        super(method, url, null);
        this.mUrl = url;
        setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, RETRY_COUNT, BACKOFF_MULTIPLIER));
//        CookieManager cookieManager = new CookieManager(new PersistantCookieStore(), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
//        CookieHandler.setDefault(cookieManager);


    }

    public static ByteArrayRequest newInstance(int method, String url){
        return new ByteArrayRequest(method, url);
    }

    public void setOnServiceListener(OnServiceListener listener) {
        mOnServiceListener = listener;
    }

    public ByteArrayRequest setHeaders(Map<String, String> headers) {
        mHeaders = headers;
        return this;
    }

    public ByteArrayRequest setUrl(String url) {
        mUrl = url;
        return this;
    }

    public ByteArrayRequest setServiceIdentifier(int serviceIdentifier) {
        mServiceIdentifier = serviceIdentifier;
        return this;
    }

    public ByteArrayRequest setErrorResponseParser(ResponseParser errorResponseParser) {
        mErrorParser = errorResponseParser;
        return this;
    }

    public ByteArrayRequest setResponseParser(ResponseParser parser) {
        mParser = parser;
        return this;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {

        if (mHeaders != null) {

            for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                Logger.d(TAG, "getHeaders - headers: key = " + entry.getKey() + " value = " + entry.getValue());
            }
            return mHeaders;
        }

        return super.getHeaders();
    }

    @Override
    public String getUrl() {
        Logger.d(TAG, "Url:" +mUrl);
        return mUrl;
    }


    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse networkResponse) {
        Response<byte[]> response = null;
        if(networkResponse != null){
                Logger.i("parseNetworkResponse - code: " +networkResponse.statusCode);
                Logger.i("parseNetworkResponse - response: "+networkResponse.data.toString());
            try {
//                Logger.i("Parser :: "+mParser);
                response = Response.success(networkResponse.data, null);
            } catch (ClassCastException e) {
                Logger.exc(e);
            } catch (Exception e) {
                Logger.exc(e);
            }
        }
        return response;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        Logger.i("parseNetworkResponse - response: "+ response);
        mOnServiceListener.onSuccess(mServiceIdentifier, response);
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);



        handleErrorResponse(error);
    }

    // ---------------------------------------
    // - VOLLEY INTERFACE MANDATORY METHODS *E
    // ---------------------------------------

    // ---------------------------------------
    // - ERROR HANDLERS *S
    // ---------------------------------------

    private void handleErrorResponse(VolleyError error) {

        //Log throwing error
        Logger.e("onErrorResponse - error: " + error.getMessage());

        NetworkError networkError = null;

        // Temporary storage for network error response.
        int errorCode = -1;
        String response = null;

        // Parsing the response now. If parsing fails, its HTTP error and we will discard
        // everything here. Else, the parsed response will be used for further processing.
        Object errorResponse = null;
        String localizedStringIssue = error.getLocalizedMessage();

        if(localizedStringIssue!=null && localizedStringIssue.equalsIgnoreCase(NETWORK_AUTHENTICATION_ISSUE_STRING)){
            networkError = null;
        }else{
            networkError = handleNetworkIssue(error);

            if (networkError == null) {
                errorCode = error.networkResponse.statusCode;
                response = new String(error.networkResponse.data);

                Logger.i("handleErrorResponse - response: "+response);

                // There is no network issue. We may have a response here. Try and parse the
                // response to retrieve status.
                if (mErrorParser != null) {
                    errorResponse = mErrorParser.parse(response);
                }

                Logger.i("handleErrorResponse - errorCode: "+errorCode);

                // Check for HTTP error. If we are fortunate, we may not have to process more.
                networkError = handleHttpError(errorCode, errorResponse);
            }
        }

        if (networkError == null) {
            // Its mostly the redundant server errors now. Show appropriate error messages to user.
            // Override the below function in service sub classes to handle independently if needed.

            networkError = handleServerError(errorCode, errorResponse);
        }

        mOnServiceListener.onError(mServiceIdentifier, networkError);
    }

    protected NetworkError handleNetworkIssue(VolleyError error) {
        if ((error.getLocalizedMessage() != null && error.getLocalizedMessage().length() > 0) || error.networkResponse == null) {

            // There is no network response. This means that we are facing a Network
            // connection issue. Inform back that the user needs to check his internet
            // connection.
            Logger.e("handleNetworkIssue - Network Error identified");
            NetworkError networkError = new NetworkError(-1, -1, "There's no network connection right now. Try again later.", null);
            return networkError;
        }

        return null;
    }

    protected NetworkError handleHttpError(int errorCode, Object errorResponse) {
        NetworkError error = null;

        if (errorResponse == null) {
            Logger.e("handleNetworkIssue - Http Error identified");
            error = new NetworkError(errorCode, -1,
                    "Unable to contact server. Please check and try again later.", null);
        }

        return error;
    }

    protected NetworkError handleServerError(int errorCode, Object errorResponse) {
        // We have got an error and in this function, we know its server error and
        // all details are available. No checks required by this time.
        NetworkError networkError = new NetworkError(errorCode, -1, null, errorResponse);
        return networkError;
    }
}
