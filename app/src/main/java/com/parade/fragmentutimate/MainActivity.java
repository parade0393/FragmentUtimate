package com.parade.fragmentutimate;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView nav_bottom;
    private List<Fragment> mFragments;

    private int lastPosition;//上次fragment的位置
    private Fragment currentFragment;//要显示的Fragment
    private Fragment hideFragment;//要隐藏的Fragment

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("last_position",lastPosition);//activity重建时保存页面的位置
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        lastPosition = savedInstanceState.getInt("last_position");//获取重建时的fragment的位置
        setSelectedFragment(lastPosition);//恢复销毁前显示的fragment
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView(savedInstanceState);
    }

    private void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(ClothesFragment.newInstance());
        mFragments.add(FoodFragment.newInstance());
        mFragments.add(BookFragment.newInstance());
    }

    private void initView(Bundle savedInstanceState) {
        nav_bottom = (BottomNavigationView) findViewById(R.id.nav_bottom);
        if (savedInstanceState == null){
            //根据传入的Bundle对象判断是正常启动还是重建 true表示正常启动，false表示重建
            setSelectedFragment(0);
        }
        nav_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_cloth:
                        setSelectedFragment(0);
                        break;
                    case R.id.menu_item_food:
                        setSelectedFragment(1);
                        break;
                    case R.id.menu_item_hotel:
                        setSelectedFragment(2);
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 根据位置选择Fragment
     * @param position 要选中的fragment的位置
     */
    private void  setSelectedFragment(int position){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        currentFragment = fragmentManager.findFragmentByTag("fragment"+position);//要显示的fragment(解决了activity重建时新建实例的问题)
        hideFragment = fragmentManager.findFragmentByTag("fragment" + lastPosition);//要隐藏的fragment(解决了activity重建时新建实例的问题)
        if (position != lastPosition){//如果位置不同
            if (hideFragment != null){//如果要隐藏的fragment存在，则隐藏
                transaction.hide(hideFragment);
            }
            if (currentFragment == null){//如果要显示的fragment不存在，则新加并提交事务
                currentFragment = mFragments.get(position);
                transaction.add(R.id.fl_container,currentFragment,"fragment"+position);
            }else {//如果要显示的存在则直接显示
                transaction.show(currentFragment);
            }
        }

        if (position == lastPosition){//如果位置相同
            if (currentFragment == null){//如果fragment不存在(第一次启动应用的时候)
                currentFragment = mFragments.get(position);
                transaction.add(R.id.fl_container,currentFragment,"fragment"+position);
            }//如果位置相同，且fragment存在，则不作任何操作
        }
        transaction.commit();//提交事务
        lastPosition = position;//更新要隐藏的fragment的位置
    }
}
