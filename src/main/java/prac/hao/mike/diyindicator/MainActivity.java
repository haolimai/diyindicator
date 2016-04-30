package prac.hao.mike.diyindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import prac.hao.mike.diyindicator.view.ViewPagerIndicator;

public class MainActivity extends AppCompatActivity {
    private ViewPagerIndicator viewPagerIndicator;
    private ViewPager viewPager;
    private List<VpSimpleFragment> mContents = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private List<String> mTitles = Arrays.asList("短信", "收藏", "推荐", "好友", "电影", "音乐", "体育", "搞笑", "军事");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerIndicator = (ViewPagerIndicator) findViewById(R.id.viewPagerIndicator);
        viewPagerIndicator.setIndicatorItem(mTitles);
        viewPagerIndicator.setViewPager(viewPager, 0);

        viewPagerIndicator.setOnPageChangeListener(new ViewPagerIndicator.OnPageChangListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Toast.makeText(MainActivity.this, "hello!" + mTitles.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (String title : mTitles) {
            VpSimpleFragment fragment = VpSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };

        viewPager.setAdapter(mAdapter);
    }
}
