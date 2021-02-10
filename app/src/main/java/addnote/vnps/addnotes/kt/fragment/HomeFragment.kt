package addnote.vnps.addnotes.kt.fragment

import addnote.vnps.addnotes.R
import addnote.vnps.addnotes.kt.handlers.NavigationHandler
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    var mNavigationHandler: NavigationHandler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_viewnotes, container, false);
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mNavigationHandler = context as NavigationHandler
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString() + " must implement NavigationalHandler")
        }
    }

    companion object {

        const val NOTESACTIVITY_TYPE_KEY = "type_key"
        const val NOTESACTIVITY_TYPE_UPDATE = "type_update"
        const val NOTESACTIVITY_TYPE_ADD = "type_add"
        const val NOTESACTIVITY_TYPE_POSITION = "type_position"
        const val MyPREFERENCES = "myPreference"
        const val PREF_VERSION_CODE_KEY = "version_code"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}