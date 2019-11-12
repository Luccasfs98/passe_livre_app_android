package com.luccas.passelivredocumentos.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.button.MaterialButton
import com.luccas.passelivredocumentos.ui.identitydocs.IdentityDocsActivity
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.SolicitationMoreDetails
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalData
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment<HomeViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_home

    override fun getViewModel() = HomeViewModel::class.java


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn_passe_livre.setOnClickListener {
            context!!.openActivity<FormPersonalData>(
                enterAnim = R.anim.anim_slide_in_down,
                exitAnim = R.anim.anim_slide_out_up
            ) { }
        }
        if (sharedPref.getBoolean("sendedDocs",false)){
            cv_send_docs.visibility = View.GONE
            cv_status_docs.visibility = View.VISIBLE

            bt_see_more_information.setOnClickListener {
                activity!!.openActivity<SolicitationMoreDetails> {

                }
            }
        }

    }
}