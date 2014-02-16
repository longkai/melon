/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package io.melon.peanut;

import android.content.Context;

/**
 * 对从http获得的数据做进一步处理（比如持久化到本地），此方法应在异步线程调用！
 *
 * @author longkai
 */
public interface PeanutProcessor<T> {
  /**
   * 对数据进行处理
   *
   * @param context {@link android.content.Context}
   * @param data    the generic type data
   * @throws Exception whatever make the process fail
   */
  void process(Context context, T data) throws Exception;
}
