package com.luccas.passelivredocumentos.ui.solicitationmoredetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import kotlinx.android.synthetic.main.solicitation_more_details_fragment.*

class SolicitationMoreDetailsFragment : BaseFragment<SolicitationMoreDetailsViewModel>() {

    override val layoutRes: Int
        get() = R.layout.solicitation_more_details_fragment

    override fun getViewModel() = SolicitationMoreDetailsViewModel::class.java

    companion object {
        fun newInstance() = SolicitationMoreDetailsFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }
    }

}
