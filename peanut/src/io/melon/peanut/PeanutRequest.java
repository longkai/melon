/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package io.melon.peanut;

import android.content.Context;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

/**
 * 一次{@link org.json.JSONObject}请求，在异步线程进行请求和对数据处理，并且在ui线程更新ui（如果有需要）
 * <ul>
 * <li>如果需要对获得的数据做进一步的处理，比如持久化到本地，那么请传入非null的{@link io.melon.peanut.PeanutProcessor}</li>
 * <li>如果需要在处理结束后对ui进行更新，请传入非null的{@link com.android.volley.Response.Listener}</li>
 * </ul>
 *
 * @author longkai
 */
public class PeanutRequest extends JsonObjectRequest {
  private Context context;
  private PeanutAPI api;
  private PeanutProcessor<JSONObject> processor;
  private Response.Listener<JSONObject> listener;

  public PeanutRequest(Context context, PeanutAPI api,
                       PeanutProcessor<JSONObject> processor,
                       Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener) {
    super(api.method, api.url, null, null, errorListener);
    this.context = context;
    this.api = api;
    this.listener = listener;
    this.processor = processor;
  }

  @Override
  protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
    try {
      String jsonString =
          new String(response.data, HttpHeaderParser.parseCharset(response.headers));
      Response<JSONObject> success = Response.success(new JSONObject(jsonString),
          HttpHeaderParser.parseCacheHeaders(response));
      if (processor != null) {
        processor.process(context, success.result);
      } else {
        Log.d("PeanutRequest", "no processor needed.");
      }
      return success;
    } catch (UnsupportedEncodingException e) {
      return Response.error(new ParseError(e));
    } catch (JSONException je) {
      return Response.error(new ParseError(je));
    } catch (Exception e) {
      return Response.error(new ParseError(e));
    }
  }

  @Override
  public Map<String, String> getHeaders() throws AuthFailureError {
    return api.authRequired ? PeanutAPI.getAuthHeader() : Collections.EMPTY_MAP;
  }

  @Override
  protected Map<String, String> getParams() throws AuthFailureError {
    return api.params;
  }

  @Override
  protected void deliverResponse(JSONObject response) {
    if (listener != null) {
      listener.onResponse(response);
    } else {
      Log.d("PeanutRequest", "complete json object request without ui response.");
    }
  }
}
