package com.luccas.passelivredocumentos.ui.terms

import android.os.Bundle
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment

class TermsFragment : BaseFragment<TermsViewModel>() {
    override val layoutRes = R.layout.terms_fragment
    override fun getViewModel() = TermsViewModel::class.java

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*viewModel.getTerms(activity!!.intent.getStringExtra("type")!!).observe(this, Observer {
            progress_bar.visibility = View.GONE
            tv_text.text = it
        })

        viewModel.error.observe(this, Observer {
            Snackbar.make(scrollView,it,Snackbar.LENGTH_SHORT).show()
            progress_bar.visibility = View.GONE
        })*/
    }

}
