package com.luccas.passelivredocumentos.ui.solicitationmoredetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.luccas.passelivredocumentos.R
import com.luccas.passelivredocumentos.ui.base.BaseFragment
import com.luccas.passelivredocumentos.ui.formpersonaldata.FormPersonalData
import com.luccas.passelivredocumentos.utils.Common
import com.luccas.passelivredocumentos.utils.openActivity
import kotlinx.android.synthetic.main.solicitation_more_details_fragment.*

class SolicitationMoreDetailsFragment : BaseFragment<SolicitationMoreDetailsViewModel>() {

    override val layoutRes: Int
        get() = R.layout.solicitation_more_details_fragment

    override fun getViewModel() = SolicitationMoreDetailsViewModel::class.java

    companion object {
        fun newInstance() = SolicitationMoreDetailsFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener {
            activity!!.finish()
        }
        viewModel.getDocuments(FirebaseAuth.getInstance().currentUser!!.uid).observe(this, Observer {
            tv_status.text = it.status
            tv_message.text = "${it.reason}"
            when(it.status){
                  Common.pendente -> {
                      tv_message.setText(resources.getString(R.string.text_pendente))
                      tv_status.setTextColor(resources.getColor(R.color.colorPendente))
                  }
                  Common.apto -> {
                      tv_message.setText(resources.getString(R.string.text_apto_dec)+"\n\n"+it.reason)

                      tv_status.setTextColor(resources.getColor(R.color.colorApto))
                  }
                  Common.inapto -> {
                      tv_message.setText(resources.getString(R.string.text_inapto)+"\n\n"+it.reason)
                      tv_status.setTextColor(resources.getColor(R.color.colorInapto))
                  }
                  Common.analise -> {
                      tv_message.setText(R.string.text_analise)
                      tv_status.text = "Análise"
                      tv_status.setTextColor(resources.getColor(R.color.colorAnalise))
                  }
                  Common.devolvido ->{
                      tv_message.setText(resources.getString(R.string.text_devolvido)+"\n\n"+it.reason)
                      tv_status.setTextColor(resources.getColor(R.color.colorDevolvido))
                      btn_passe_livre.visibility = View.VISIBLE
                      btn_passe_livre.setOnClickListener {
                          context!!.openActivity<FormPersonalData>(finishWhenOpen = true, enterAnim = R.anim.anim_slide_in_up,exitAnim = R.anim.anim_slide_out_down
                          ) {  }
                      }
                  }
            }

        })
    }

}
