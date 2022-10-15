package com.example.weatherapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.weatherapplication.fragments.CurrentLocation
import com.example.weatherapplication.fragments.OtherLocation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(mainActivityToolbar)
        supportActionBar?.title = "Weather App"

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(CurrentLocation(), "Current Location")
        viewPagerAdapter.addFragment(OtherLocation(), "Other Location")

        mainActivityViewPager.adapter = viewPagerAdapter
        mainActivityTabLayout.setupWithViewPager(mainActivityViewPager)

        setIconTabLayout(0, R.drawable.ic_current_location)
        setIconTabLayout(1, R.drawable.ic_other_location)
    }

    private fun setIconTabLayout(tab: Int, icon: Int) {
        mainActivityTabLayout.getTabAt(tab)?.setIcon(icon)
        mainActivityTabLayout.getTabAt(tab)?.icon?.setTint(resources.getColor(R.color.white))
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        private val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles: ArrayList<String> = ArrayList<String>()

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }
    }
}