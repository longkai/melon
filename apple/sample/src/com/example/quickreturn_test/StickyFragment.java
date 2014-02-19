/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package com.example.quickreturn_test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import io.melon.apple.QuickReturnScrollView;

/**
 * Created by longkai on 14-2-18.
 */
public class StickyFragment extends Fragment implements QuickReturnScrollView.Callbacks {
  private QuickReturnScrollView quickReturnScrollView;
  private View placeHolderView;
  private TextView stickyView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.main, container, false);
    quickReturnScrollView = (QuickReturnScrollView) view.findViewById(R.id.scroll_view);
    quickReturnScrollView.setCallbacks(this);
    placeHolderView = view.findViewById(R.id.place_holder);
    stickyView = (TextView) view.findViewById(R.id.quick_return);
    quickReturnScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        onScrollChanged(quickReturnScrollView.getScrollY());
      }
    });
    return view;
  }

  @Override
  public void onScrollChanged(int scrollY) {
    stickyView.setTranslationY(Math.max(placeHolderView.getTop(), scrollY));
  }

  @Override
  public void onDownMotionEvent() {

  }

  @Override
  public void onUpOrCancelMotionEvent() {

  }
}
