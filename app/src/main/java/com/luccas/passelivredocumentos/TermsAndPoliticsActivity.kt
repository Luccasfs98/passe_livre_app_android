package com.luccas.passelivredocumentos
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.luccas.passelivredocumentos.ui.base.BaseActivity
import com.luccas.passelivredocumentos.ui.termsandpolitics.TermsAndPoliticsViewModel
import com.luccas.passelivredocumentos.utils.Common
import kotlinx.android.synthetic.main.terms_and_politics_activity.*

class TermsAndPoliticsActivity : BaseActivity<TermsAndPoliticsViewModel>() {

    override val layoutRes = R.layout.terms_and_politics_activity
    override fun getViewModel() = TermsAndPoliticsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        if(intent.getStringExtra("type") == Common.terms){
            toolbar.title = "Termos de Uso"
        }
        if(intent.getStringExtra("type") == Common.politics){
            toolbar.title = "Políticas de privacidade"
        }
        viewModel.getTerms(intent.getStringExtra("type")!!).observe(this, Observer {
            progress_bar.visibility = View.GONE
            tv_text.text = it
        })

        viewModel.error.observe(this, Observer {
            Snackbar.make(scrollView,it,Snackbar.LENGTH_SHORT).show()
            progress_bar.visibility = View.GONE
        })
    }
}
