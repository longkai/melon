/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package io.melon.peanut;

import java.util.Map;

/**
 * REST API抽象，封装了一些api信息，并提供了auth-header的存取
 *
 * @author longkai
 */
public class PeanutAPI {
  public final int method;
  public final String url;
  public final boolean authRequired;
  /** 通常作为POST和PUT方法中的请求参数，其它方式赋null即可 */
  public final Map<String, String> params;

  private PeanutAPI(int method, String url, boolean authRequired, Map<String, String> params) {
    this.method = method;
    this.url = url;
    this.authRequired = authRequired;
    this.params = params;
  }

  /** 初始化认证信息 */
  public static void init(Map<String, String> header) {
    authHeader = authHeader;
  }

  /** 重置认证信息 */
  public static void reset(Map<String, String> header) {
    destroy();
    authHeader = header;
  }

  /** 获得认证信息 */
  public static Map<String, String> getAuthHeader() {
    return authHeader;
  }

  /** 清除认证信息 */
  public static void destroy() {
    if (authHeader != null) {
      authHeader.clear();
      authHeader = null;
    }
  }

  private static Map<String, String> authHeader;
}
