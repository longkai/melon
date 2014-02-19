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
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import io.melon.apple.QuickReturnListView;

/**
 * Created by longkai on 14-2-19.
 */
public class QuickReturnListFragment extends Fragment implements AbsListView.OnScrollListener {
  public static final int STATE_ON_SCREEN = 0;
  public static final int STATE_OFF_SCREEN = 1;
  public static final int STATE_RETURNING = 2;

  private QuickReturnListView quickReturnListView;
  private View header;
  private View placeHolderView;
  private TextView quickReturnView;
  private TextView footerView;

  private int quickReturnListHeight;

  private int state = STATE_ON_SCREEN;
  private int minHeaderOffsetY;
  private int quickReturnHeight;

  private int footerState = STATE_ON_SCREEN;
  private int footerViewHeight;
  private int minFooterOffsetY;

  private ArrayAdapter<String> adapter;

  private static final String[] ITEMS = new String[]{
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
      "Android",
  };


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, ITEMS);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.list, container, false);
    quickReturnListView = (QuickReturnListView) view.findViewById(R.id.quick_return_list);
    quickReturnView = (TextView) view.findViewById(R.id.sticky);
    footerView = (TextView) view.findViewById(R.id.footer);
    header = inflater.inflate(R.layout.header, null);
    placeHolderView = header.findViewById(R.id.place_holder);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    quickReturnListView.addHeaderView(header);
    quickReturnListView.setAdapter(adapter);
    quickReturnListView.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            quickReturnHeight = quickReturnView.getHeight();
            quickReturnListView.computeScrollRange();
            quickReturnListHeight = quickReturnListView.getScrollRange();
            footerViewHeight = footerView.getHeight();
            quickReturnView.setTranslationY(placeHolderView.getTop());
          }
        }
    );
    quickReturnListView.setOnScrollListener(this);
  }

  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {

  }

  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    int scrollY = 0;
    if (quickReturnListView.isScrollRangeComputed()) {
      scrollY = quickReturnListView.getComputedScrollY();
    }
    int offsetY = placeHolderView.getTop()
        - Math.min(
        quickReturnListHeight
            - quickReturnListView.getHeight(), scrollY);
    // header
    int translationY = 0;
    switch (state) {
      case STATE_OFF_SCREEN:
        if (offsetY < minHeaderOffsetY) {
          minHeaderOffsetY = offsetY;
        } else {
          state = STATE_RETURNING;
        }
        translationY = offsetY;
        break;
      case STATE_ON_SCREEN:
        if (offsetY < -quickReturnHeight) {
          state = STATE_OFF_SCREEN;
          minHeaderOffsetY = offsetY;
        }
        translationY = offsetY;
        break;
      case STATE_RETURNING:
        translationY = offsetY - minHeaderOffsetY - quickReturnHeight;
        if (translationY > 0) {
          translationY = 0;
          minHeaderOffsetY = offsetY - quickReturnHeight;
        }
        if (offsetY > 0) {
          state = STATE_ON_SCREEN;
          translationY = offsetY;
        }
        if (translationY < -quickReturnHeight) {
          state = STATE_OFF_SCREEN;
          minHeaderOffsetY = offsetY;
        }
        break;
    }
    // for footer
    int footerTranslationY = 0;
    switch (footerState) {
      case STATE_OFF_SCREEN:
        if (scrollY >= minFooterOffsetY) {
          minFooterOffsetY = scrollY;
        } else {
          footerState = STATE_RETURNING;
        }
        footerTranslationY = scrollY;
        break;
      case STATE_ON_SCREEN:
        if (scrollY > footerViewHeight) {
          footerState = STATE_OFF_SCREEN;
          minFooterOffsetY = scrollY;
        }
        footerTranslationY = scrollY;
        break;
      case STATE_RETURNING:
        footerTranslationY = scrollY - minFooterOffsetY + footerViewHeight;
        if (footerTranslationY < 0) {
          footerTranslationY = 0;
          minFooterOffsetY = scrollY + footerViewHeight;
        }
        if (scrollY == 0) {
          footerState = STATE_ON_SCREEN;
          footerTranslationY = 0;
        }
        if (footerTranslationY > footerViewHeight) {
          footerState = STATE_OFF_SCREEN;
          minFooterOffsetY = scrollY;
        }
        break;
      default:
        break;
    }
    // no animation performed
    // footerView.animate().cancel();
    footerView.setTranslationY(footerTranslationY);
    // quickReturnView.animate().cancel();
    quickReturnView.setTranslationY(translationY);
  }
}
