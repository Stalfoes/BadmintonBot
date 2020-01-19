package com.anthonymarkd.soccergamemanager.settings

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.anthonymarkd.soccergamemanager.R
import kotlinx.android.synthetic.main.settings_parent_fragment.view.*


class SettingsParentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_parent_fragment, container, false)

        childFragmentManager.beginTransaction()
            .replace(
                R.id.settings_container,
                SettingsFragment()
            )
            .commitNow()
        view.toolbar2.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp,activity!!.theme)
        view.toolbar2.navigationIcon?.setColorFilter(
            ContextCompat.getColor(
                context!!,
                R.color.material_on_surface_emphasis_high_type
            ), PorterDuff.Mode.SRC_ATOP
        )
        view.toolbar2.setNavigationOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        return view
    }
}