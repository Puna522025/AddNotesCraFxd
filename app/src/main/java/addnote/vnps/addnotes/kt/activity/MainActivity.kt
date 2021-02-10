package addnote.vnps.addnotes.kt.activity

import addnote.vnps.addnotes.R
import addnote.vnps.addnotes.kt.fragment.HomeFragment
import android.os.Bundle

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            loadFragment(HomeFragment.newInstance(), false)
        }
    }
}