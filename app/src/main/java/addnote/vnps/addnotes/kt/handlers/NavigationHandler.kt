package addnote.vnps.addnotes.kt.handlers

import androidx.fragment.app.Fragment

interface NavigationHandler {

    fun loadFragment(fragment: Fragment, isAddToBackStack: Boolean)

    fun setScreenTitle(title: String)

}