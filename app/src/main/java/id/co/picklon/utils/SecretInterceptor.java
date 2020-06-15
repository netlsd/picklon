package id.co.picklon.utils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class SecretInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final MediaType JSON_TYPE = MediaType.parse("application/json");
    private static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody body = request.body();
        boolean hasRequestBody = body != null;

        if (hasRequestBody) {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);

            String content = URLDecoder.decode(buffer.readUtf8(), "utf-8");
            String json;
            String strNewBody;

            if (content.startsWith("d=")) {
                json = content.substring(2);
                strNewBody = AES.encrypt(json);
                strNewBody = URLEncoder.encode(strNewBody, "utf-8");
                strNewBody = "d=" + strNewBody;

                // post method response
                Request.Builder requestBuilder = request.newBuilder()
                        .post(RequestBody.create(FORM_TYPE, strNewBody));
                Response response = chain.proceed(requestBuilder.build());

                String decryptedJson = decrypt(getResponseResult(response));
//                L.e("post response is " + decryptedJson);

                return newResponse(response, decryptedJson);
            }

            return chain.proceed(request);
        }

        // get method response
        Response response = chain.proceed(request);

        String decryptedJson = decrypt(getResponseResult(response));
//        L.e("get response is " + decryptedJson);

        return newResponse(response, decryptedJson);
    }

    private String getResponseResult(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.
        Buffer sourceBuffer = source.buffer();
        String responseContent = null;

        if (contentLength != 0) {
            responseContent = sourceBuffer.clone().readString(UTF8);
        }

        return responseContent;
    }

    private String decrypt(String content) {
        if (content == null) {
            return null;
        }

        return AES.decrypt(content);
    }

    private Response newResponse(Response response, String content) {
        if (content == null) {
            return response;
        }

        return response.newBuilder().body(ResponseBody.create(JSON_TYPE, content)).build();
    }
}