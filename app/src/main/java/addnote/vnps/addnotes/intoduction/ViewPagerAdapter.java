package addnote.vnps.addnotes.intoduction;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
    int currentPage = 0;
    List<Class> fragmentsAttached;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        try {

      /*      switch (position) {
                case 0:
                    return new ScreenOne();
                case 1:
                    return new ScreenTwo();
                case 2:
                    return new ScreenThree();
            }*/

            return (Fragment) fragmentsAttached.get(position).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @Override
    public int getCount() {
        return fragmentsAttached.size();
    }

    public void setFragments(List<Class> fragments) {
        fragmentsAttached = fragments;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int newPageIndex) {
        currentPage = newPageIndex;
    }

}


