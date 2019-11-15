package com.luccas.passelivredocumentos.ui.termsandpolitics

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment

class TermsAndPoliticsFragment : BaseFragment<TermsAndPoliticsViewModel>() {

    override val layoutRes = R.layout.terms_and_politics_fragment
    override fun getViewModel() = TermsAndPoliticsViewModel::class.java

    companion object{
        fun newInstance() = TermsAndPoliticsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getTerms(sharedPref.getString("userID","")!!).observe(this, Observer {

        })
    }
}
