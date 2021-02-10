package addnote.vnps.addnotes.kt.activity

import addnote.vnps.addnotes.R
import addnote.vnps.addnotes.kt.handlers.NavigationHandler
import android.os.Bundle
import android.os.PersistableBundle
import android.transition.AutoTransition
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

open class BaseActivity : AppCompatActivity(), NavigationHandler {

    private var mFragmentManager: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.exitTransition = AutoTransition()

        mFragmentManager = supportFragmentManager
    }

    override fun loadFragment(fragment: Fragment, isAddToBackStack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            mFragmentManager?.beginTransaction() ?: supportFragmentManager.beginTransaction()

        mFragmentTransaction.replace(R.id.baseContainer, fragment)

        if (isAddToBackStack) {
            mFragmentTransaction.addToBackStack(fragment.tag)
        }
        mFragmentTransaction.commit()
    }

    override fun setScreenTitle(title: String) {
        setTitle(title)
    }
}
