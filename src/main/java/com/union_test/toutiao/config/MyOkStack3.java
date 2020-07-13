package com.union_test.toutiao.config;

import android.text.TextUtils;

import com.bytedance.sdk.adnet.core.Header;
import com.bytedance.sdk.adnet.core.HttpResponse;
import com.bytedance.sdk.adnet.core.Request;
import com.bytedance.sdk.adnet.err.VAdError;
import com.bytedance.sdk.adnet.face.IHttpStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Custom okhttp3 network
 */
public class MyOkStack3 implements IHttpStack {

    private final OkHttpClient mClient;

    /**
     * Can be replaced with your own okClient
     *
     * @param client okhttp client
     */
    public MyOkStack3(OkHttpClient client) {
        this.mClient = client;
    }

    /**
     * Default constructor
     */
    public MyOkStack3() {
        this(new OkHttpClient());
    }

    @SuppressWarnings("deprecation")
    private static void setConnectionParametersForRequest
            (okhttp3.Request.Builder builder, Request<?> request)
            throws IOException {
        switch (request.getMethod()) {
            case Request.METHOD_GET:
            case Request.METHOD_DEPRECATED_GET_OR_POST:
                builder.get();
                break;
            case Request.METHOD_DELETE:
                builder.delete();
                break;
            case Request.METHOD_POST:
                builder.post(createRequestBody(request));
                break;
            case Request.METHOD_PUT:
                builder.put(createRequestBody(request));
                break;
            case Request.METHOD_HEAD:
                builder.head();
                break;
            case Request.METHOD_OPTIONS:
                builder.method("OPTIONS", null);
                break;
            case Request.METHOD_TRACE:
                builder.method("TRACE", null);
                break;
            case Request.METHOD_PATCH:
                builder.patch(createRequestBody(request));
                break;
            default:
                throw new IllegalStateException("Unknown method type.");
        }
    }

    private static RequestBody createRequestBody(Request request) {
        byte[] body = null;
        try {
            body = request.getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (body == null) return null;

        return RequestBody.create(MediaType.parse(request.getBodyContentType()), body);
    }

    @Override
    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, VAdError {
        int timeoutMs = request.getTimeoutMs();
        OkHttpClient client = mClient.newBuilder()
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build();

        okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
        okHttpRequestBuilder.url(request.getUrl());

        //Handling internal headers
        if (request.getHeaders() != null) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                String k = entry.getKey();
                String v = entry.getKey();
                if (!TextUtils.isEmpty(k) && !TextUtils.isEmpty(v)) {
                    okHttpRequestBuilder.addHeader(k, v);
                }
            }
        }

        //Handling extra headers
        if (additionalHeaders != null) {
            for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
                String k = entry.getKey();
                String v = entry.getKey();
                if (!TextUtils.isEmpty(k) && !TextUtils.isEmpty(v)) {
                    okHttpRequestBuilder.addHeader(k, v);
                }
            }
        }

        setConnectionParametersForRequest(okHttpRequestBuilder, request);
        okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
        Call okHttpCall = client.newCall(okHttpRequest);
        Response okHttpResponse = okHttpCall.execute();


        return responseFromConnection(okHttpResponse);
    }

    private HttpResponse responseFromConnection(Response okHttpResponse) throws IOException {

        int code = okHttpResponse.code();

        if (code == -1) {
            throw new IOException("response code error from okhttp.");
        }

        int length = Long.valueOf(okHttpResponse.body().contentLength()).intValue();

        Headers okHeaders = okHttpResponse.headers();
        List<Header> headers = new ArrayList<>();
        if (okHeaders != null) {
            for (int i = 0, len = okHeaders.size(); i < len; i++) {
                final String name = okHeaders.name(i), value = okHeaders.value(i);
                if (name != null) {
                    headers.add(new Header(name, value));
                }
            }
        }

        return new HttpResponse(code, headers, length, okHttpResponse.body().byteStream());
    }


}
