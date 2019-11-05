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
import com.luccas.passelivredocumentos.utils.openActivity

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.findViewById<MaterialButton>(R.id.btn_passe_livre).setOnClickListener{
            context!!.openActivity<IdentityDocsActivity>(
                enterAnim = R.anim.anim_slide_in_down,
                exitAnim = R.anim.anim_slide_out_up
            ) {  }
        }

        return root
    }
}