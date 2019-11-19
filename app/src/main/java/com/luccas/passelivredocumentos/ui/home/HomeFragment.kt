package com.luccas.passelivredocumentos.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.SolicitationMoreDetails
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalData
import com.luccas.passelivredocumentos.utils.Common
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
        getSolicitationStatus()

        swipe.setOnRefreshListener {
            getSolicitationStatus()
            cv_container.visibility = View.GONE
        }

        viewModel.error.observe(this, Observer {
            progress_bar.visibility = View.GONE
            swipe.isRefreshing = false
            cv_container.visibility = View.VISIBLE
            Snackbar.make(swipe,it,Snackbar.LENGTH_SHORT).show()
        })
    }
    fun getSolicitationStatus(){
        viewModel.getUserDocStatus(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, Observer {
            progress_bar.visibility = View.GONE
            swipe.isRefreshing = false
            cv_container.visibility = View.VISIBLE
            if (it!=null){
                    cv_status_docs.visibility = View.VISIBLE
                    cv_send_docs.visibility = View.GONE
                    tv_status_docs.text = it.status
                    if(it.status == Common.apto){
                        tv_status_docs.setTextColor(resources.getColor(R.color.colorApto))
                    }
                    if(it.status == Common.inapto){
                        tv_status_docs.setTextColor(resources.getColor(R.color.colorInapto))
                    }
                    if(it.status == Common.analise){
                        tv_status_docs.setTextColor(resources.getColor(R.color.colorAnalise))
                    }
                    if(it.status == Common.pendente){
                        tv_status_docs.setTextColor(resources.getColor(R.color.colorPendente))
                    }
                    bt_see_more_information.setOnClickListener {
                        activity!!.openActivity<SolicitationMoreDetails> {}
                    }
            } else {
                cv_status_docs.visibility = View.GONE

                cv_send_docs.visibility = View.VISIBLE
            }
        })
    }
}