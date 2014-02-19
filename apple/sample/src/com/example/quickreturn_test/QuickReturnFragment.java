/*
 * The MIT License (MIT)
 * Copyright (c) 2014 longkai
 * The software shall be used for good, not evil.
 */
package com.example.quickreturn_test;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import io.melon.apple.QuickReturnScrollView;

/**
 * Created by longkai on 14-2-18.
 */
public class QuickReturnFragment extends Fragment implements QuickReturnScrollView.Callbacks {
  private QuickReturnScrollView quickReturnScrollView;
  private View placeHolderView;
  private TextView quickReturnView;
  private TextView footerView;

  private int maxScrollY;
  private SettleDownHandler settleDownHandler = new SettleDownHandler();

  private int headerState = STATE_ON_SCREEN;
  private int quickReturnViewHeight;
  private int footerViewHeight;
  private int minHeaderOffsetY;

  private int footerState = STATE_ON_SCREEN;
  private int minFooterOffsetY;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.main, container, false);
    quickReturnScrollView = (QuickReturnScrollView) view.findViewById(R.id.scroll_view);
    quickReturnScrollView.setCallbacks(this);
    placeHolderView = view.findViewById(R.id.place_holder);
    quickReturnView = (TextView) view.findViewById(R.id.quick_return);
    footerView = (TextView) view.findViewById(R.id.footer);
    quickReturnScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            onScrollChanged(quickReturnScrollView.getScrollY());
            quickReturnViewHeight = quickReturnView.getHeight();
            maxScrollY = quickReturnScrollView.computeVerticalScrollRange()
                - quickReturnScrollView.getHeight();
            footerViewHeight = footerView.getHeight();
          }
        });
    return view;
  }

  public void onScrollChanged(int scrollY) {
    // 防止滑到顶部带来了相反的几个像素的偏差
    scrollY = Math.min(maxScrollY, scrollY);
    settleDownHandler.onScroll(scrollY);
    // header
    int translationY = 0;
    int offsetY = placeHolderView.getTop() - scrollY;
    // 原理其实就是中学的物理，分析，画图，临界条件，状态的转移等
    switch (headerState) {
      case STATE_ON_SCREEN:
        if (offsetY < -quickReturnViewHeight) {
          headerState = STATE_OFF_SCREEN;
          minHeaderOffsetY = offsetY;
        }
        translationY = offsetY;
        break;
      case STATE_OFF_SCREEN:
        if (offsetY > minHeaderOffsetY) {
          headerState = STATE_RETURNING;
        } else {
          minHeaderOffsetY = offsetY;
        }
        translationY = offsetY;
        break;
      case STATE_RETURNING:
        translationY = offsetY - minHeaderOffsetY - quickReturnViewHeight;
        if (translationY > 0) {
          translationY = 0;
          minHeaderOffsetY = offsetY - quickReturnViewHeight;
        }
        if (offsetY > 0) {
          headerState = STATE_ON_SCREEN;
          translationY = offsetY;
        }
        if (translationY < -quickReturnViewHeight) {
          headerState = STATE_OFF_SCREEN;
          minHeaderOffsetY = offsetY;
        }
        break;
      default:
        break;
    }
    // footer
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
    footerView.animate().cancel();
    footerView.setTranslationY(footerTranslationY);
    quickReturnView.animate().cancel();
    quickReturnView.setTranslationY(translationY + scrollY);
  }

  @Override
  public void onDownMotionEvent() {
    settleDownHandler.setSettleEnabled(false);
  }

  @Override
  public void onUpOrCancelMotionEvent() {
    settleDownHandler.setSettleEnabled(true);
    settleDownHandler.onScroll(quickReturnScrollView.getScrollY());
  }

  private class SettleDownHandler extends Handler {
    public static final int SETTLE_MILLIS = 200;

    private int settleScrollY = Integer.MIN_VALUE;
    private boolean settleEnabled;

    public void setSettleEnabled(boolean settleEnabled) {
      this.settleEnabled = settleEnabled;
    }

    public void onScroll(int scrollY) {
      if (settleScrollY != scrollY) {
        removeMessages(0);
        sendEmptyMessageDelayed(0, SETTLE_MILLIS);
        settleScrollY = scrollY;
      }
    }

    @Override
    public void handleMessage(Message msg) {
      if (headerState == STATE_RETURNING && settleEnabled) {
        int destTranslationY;
        if (settleScrollY - quickReturnView.getTranslationY() > quickReturnViewHeight / 2) {
          headerState = STATE_OFF_SCREEN;
          destTranslationY = Math.max(
              settleScrollY - quickReturnViewHeight,
              placeHolderView.getTop()
          );
        } else {
          destTranslationY = settleScrollY;
        }
        minHeaderOffsetY = placeHolderView.getTop() - quickReturnViewHeight - destTranslationY;
        quickReturnView.animate().translationY(destTranslationY);
      }
      // for footer
      if (footerState == STATE_RETURNING && settleEnabled) {
        int footerDescTranslationY;
        // todo: 可能需要进一步通过物理论证Orz
        if (settleScrollY - minFooterOffsetY > -footerViewHeight / 2) {
          footerState = STATE_OFF_SCREEN;
          footerDescTranslationY = settleScrollY;
        } else {
          footerDescTranslationY = 0;
        }
        minFooterOffsetY = settleScrollY + footerViewHeight + footerDescTranslationY;
        footerView.animate().translationY(footerDescTranslationY);
      }
      settleScrollY = Integer.MIN_VALUE;
    }
  }
}
