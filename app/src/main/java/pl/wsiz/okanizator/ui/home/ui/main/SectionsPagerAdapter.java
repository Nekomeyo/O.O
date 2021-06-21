package pl.wsiz.okanizator.ui.home.ui.main;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class SectionsPagerAdapter extends FragmentPagerAdapter {


    private  final List<String> titles = new ArrayList<>();
    private  final List<Fragment> fragments = new ArrayList<>();

    public SectionsPagerAdapter( FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }
    public void AddFragment(Fragment fragment, String title ){
        fragments.add(fragment);
        titles.add(title);
    }
}