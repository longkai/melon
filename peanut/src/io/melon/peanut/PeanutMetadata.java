/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package io.melon.peanut;

/**
 * 元数据的接口（作为一个待持久化到sqlite的元数据类型必须实现此接口）。
 *
 * @author longkai
 */
public interface PeanutMetadata<From, To> {
  /**
   * 生成元数据的数据库模式（数据表语句）
   *
   * @return sqlite ddl
   */
  String ddl();

  /**
   * 将元数据从一种类型（如{@link org.json.JSONObject}）转化为另一种类型（如{@link android.content.ContentValues}）
   * <p/>
   * 本方法应当在后台线程中调用！
   *
   * @param from the original type data
   * @return converted type data
   */
  To convert(From from);
}
