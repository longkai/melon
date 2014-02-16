扩展android-volley来开发Android restful client
==============================================

## Android Restful Client的介绍
请看Google/IO 2010的[视频][]，年代有些久远了，可能有些不合适，但是仍然有借鉴意义。

## 关于这个简单的类库
本类库简单的扩展了android-volley这个http异步请求类库，具体来说，提供以下几个功能

1. 在后台线程进行http请求
2. api的封装和认证头部的管理
3. 在后台线程对获得的数据进一步处理，比如持久化到本地的sqlite（可选操作）
4. 更新ui，在ui线程（可选）
5. 在ui线程提示请求过程中出现的错误，如果出错了

## 例子
```java
public class TestActivity extends Activity {
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 初始化你的认证header，如果有
    // PeanutAPI.init(header);
    int method = Request.Method.GET;
    String uri = "http://example.org/path";
    boolean authRequired = true;
    Map<String, String> params = null;
    PeanutAPI api = new PeanutAPI(method, uri, authRequired, params);
    Volley.newRequestQueue(this).add(new PeanutRequest(
        this,
        api,
        new PeanutProcessor<JSONObject>() { // 可选，如果需要对data进一步处理，此方法调用在后台线程
          @Override
          public void process(Context context, JSONObject data) throws Exception {
            // 对json object进一步处理，比如
            // ContentValues values = convert(data);
            // context.getContentResolver().insert(...);
          }
        },
        new Response.Listener<JSONObject>() { // 可选，如果需要更新ui
          @Override
          public void onResponse(JSONObject response) {
            Toast.makeText(TestActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
          }
        },
        new Response.ErrorListener() { // 必选，告知用户请求出了问题
          @Override
          public void onErrorResponse(VolleyError error) {
             Toast.makeText(TestActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
          }
        }
    ));
  }
}
```

## 其它
1. 除了json object，peanut还提供了json array的处理，基本类似，事实上，你可以任意扩展volley的Request来满足自己的需要，可以参考PeanutRequest的实现
2. 本类库同时支持gradle构建和一般的Eclipse/idea构建。由于依赖android-volley，所以如果不使用gradle你需要自行[下载volley][]并引入


## License
> ```
> The MIT License (MIT)
> Copyright (c) 2014 longkai
> The software shall be used for good, not evil.
> ```

[视频]: http://www.google.com/events/io/2010/sessions/developing-RESTful-android-apps.html "developing-RESTful-android-apps"
[下载volley]: https://android.googlesource.com/platform/frameworks/volley "android-volley"