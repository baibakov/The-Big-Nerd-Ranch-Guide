package com.bignerdranch.android.criminalintent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mToFirtsItemButton;
    private Button mToLastItemButton;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.crime_view_pager);

        /**
         * получаем от CrimeLab набор данных — контейнер List объектов Crime.
         * */
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();

        /**
         * адаптером назначается безымянный экземпляр FragmentStatePagerAdapter. Для создания
         * FragmentStatePagerAdapter необходим объект FragmentManager. Не забывайте, что FragmentStatePagerAdapter —
         * ваш агент, управляющий взаимодействием с ViewPager. Чтобы агент мог выпол-
         * нить свою работу с фрагментами, возвращаемыми в getItem(int), он должен
         * быть способен добавить их в активность.
         * */
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            /**
             * Метод получает экземпляр Crime для заданной позиции в наборе дан-
             * ных, после чего использует его идентификатор для создания и возвращения пра-
             * вильно настроенного экземпляра CrimeFragment.
             * */
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            /**
             * Метод getCount() возвращает текущее количество элементов в списке.
             * @return int
             * */
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mToFirtsItemButton.setEnabled(false);
                    mToLastItemButton.setEnabled(true);
                } else if (position == mCrimes.size() - 1) {
                    mToLastItemButton.setEnabled(false);
                    mToFirtsItemButton.setEnabled(true);
                } else {
                    mToFirtsItemButton.setEnabled(true);
                    mToLastItemButton.setEnabled(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        mToFirtsItemButton = (Button) findViewById(R.id.to_first_button);
        mToFirtsItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });

        mToLastItemButton = (Button) findViewById(R.id.to_last_button);
        mToLastItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });

    }
}