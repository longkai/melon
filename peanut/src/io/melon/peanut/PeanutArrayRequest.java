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
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;

/**
 * 一次{@link org.json.JSONArray}请求，在异步线程进行请求和对数据处理，并且在ui线程更新ui（如果有需要）
 * <ul>
 * <li>如果需要对获得的数据做进一步的处理，比如持久化到本地，那么请传入非null的{@link io.melon.peanut.PeanutProcessor}</li>
 * <li>如果需要在处理结束后对ui进行更新，请传入非null的{@link com.android.volley.Response.Listener}</li>
 * </ul>
 *
 * @author longkai
 */
public class PeanutArrayRequest extends JsonArrayRequest {
  private Context context;
  private PeanutAPI api;
  private PeanutProcessor<JSONArray> processor;
  private Response.Listener<JSONArray> listener;

  public PeanutArrayRequest(Context context, PeanutAPI api,
                            PeanutProcessor<JSONArray> processor,
                            Response.Listener<JSONArray> listener,
                            Response.ErrorListener errorListener) {
    super(api.url, null, errorListener);
    this.context = context;
    this.api = api;
    this.processor = processor;
    this.listener = listener;
  }

  @Override
  protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
    try {
      String jsonString =
          new String(response.data, HttpHeaderParser.parseCharset(response.headers));
      Response<JSONArray> success = Response.success(new JSONArray(jsonString),
          HttpHeaderParser.parseCacheHeaders(response));
      if (processor != null) {
        processor.process(context, success.result);
      } else {
        Log.d("PeanutArrayRequest", "no processor needed.");
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
  protected void deliverResponse(JSONArray response) {
    if (listener != null) {
      listener.onResponse(response);
    } else {
      Log.d("PeanutArrayRequest", "complete json array request without ui response.");
    }
  }
}
