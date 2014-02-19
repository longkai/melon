package com.example.quickreturn_test;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class MyActivity extends Activity implements ActionBar.TabListener {
  private ViewPager viewPager;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pager);
    viewPager = (ViewPager) findViewById(R.id.pager);
    viewPager.setAdapter(new android.support.v13.app.FragmentPagerAdapter(getFragmentManager()) {
      @Override
      public android.app.Fragment getItem(int position) {
        switch (position) {
          case 0:
            return new QuickReturnFragment();
          case 1:
            return new StickyFragment();
          case 2:
            return new QuickReturnListFragment();
          default:
            throw new RuntimeException();
        }
      }

      @Override
      public int getCount() {
        return 3;
      }

      @Override
      public CharSequence getPageTitle(int position) {
        switch (position) {
          case 0:
            return "Quick Return";
          case 1:
            return "Sticky";
          case 2:
            return "List";
          default:
            return "null";
        }
      }
    });
    viewPager.setPageMargin(10);
    viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        getActionBar().setSelectedNavigationItem(position);
      }
    });
    getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    getActionBar().setDisplayShowTitleEnabled(false);
    getActionBar().setDisplayShowHomeEnabled(false);
    for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
      getActionBar().addTab(getActionBar().newTab()
          .setText(viewPager.getAdapter().getPageTitle(i)).setTabListener(this));
    }
  }

  @Override
  public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    viewPager.setCurrentItem(tab.getPosition());
  }

  @Override
  public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

  }

  @Override
  public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

  }
}
