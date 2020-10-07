package tw.tonyyang.englishwords

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import tw.tonyyang.englishwords.chooser.FileChooserFragment

class MainActivity : BaseActivity() {

    override fun initToolbar() {
        super.initToolbar()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override val layoutResource: Int
        get() = R.layout.activity_main

    override fun onViewCreated() {
        val tabs = findViewById<TabLayout>(R.id.tabs).apply {
            removeAllTabs()
            addTab(newTab())
            addTab(newTab())
            addTab(newTab())
        }
        val viewpager = findViewById<ViewPager>(R.id.viewpager).apply {
            adapter = SamplePagerAdapter(supportFragmentManager, tabs.tabCount)
        }
        tabs.setupWithViewPager(viewpager)
    }

    private class SamplePagerAdapter internal constructor(
            fm: FragmentManager,
            private val numOfTabs: Int
    ) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val tabsTitle = listOf("讀取 / 更新", "單字列表", "單字考試")

        override fun getPageTitle(position: Int): CharSequence? {
            return if (position < tabsTitle.size) tabsTitle[position] else null
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> FileChooserFragment()
                1 -> WordListM1Fragment()
                2 -> ExamFragment()
                else -> throw RuntimeException("Could not get fragment.")
            }
        }

        override fun getCount(): Int {
            return numOfTabs
        }
    }
}