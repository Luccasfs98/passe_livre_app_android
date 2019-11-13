package com.luccas.passelivredocumentos.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.viewpager.widget.ViewPager


import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.luccas.passelivredocumentos.R
import java.util.regex.Pattern



fun hideKeyboard(activity: Activity) {
    val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

inline fun <reified T : Activity> Context.openActivity(
    options: Bundle? = null,
    finishWhenOpen: Boolean = false,
    @AnimRes enterAnim: Int = R.anim.activity_slide_pop_vertical_open_in,
    @AnimRes exitAnim: Int = R.anim.activity_slide_pop_vertical_open_out,
    noinline f: Intent.() -> Unit = {}) {

    val intent = Intent(this, T::class.java)
    intent.f()
    startActivity(intent, options)
    if (finishWhenOpen) (this as Activity).finish()
    (this as Activity).overridePendingTransition(enterAnim, exitAnim)
}
fun isEmailValid(email: String): Boolean {
    val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(email)
    return matcher.matches()
}

fun isPhoneValid(phone: String): Boolean {
    return phone.length==14
}

fun isPasswordValid(password: String): Boolean{
    val expression  ="^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#\$%^&+=!])(?=\\\\S+\$).{4,}\$";
    val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
    val matcher = pattern.matcher(password)
    return matcher.matches()
}

fun isCpfValid(cpf : String) : Boolean{
    val cpfClean = cpf.replace(".", "").replace("-", "")

    //## check if size is eleven
    if (cpfClean.length != 11)
        return false

    //## check if is number
    try {
        val number  = cpfClean.toLong()
    }catch (e : Exception){
        return false
    }

    //continue
    var dvCurrent10 = cpfClean.substring(9,10).toInt()
    var dvCurrent11= cpfClean.substring(10,11).toInt()

    //the sum of the nine first digits determines the tenth digit
    val cpfNineFirst = IntArray(9)
    var i = 9
    while (i > 0 ) {
        cpfNineFirst[i-1] = cpfClean.substring(i-1, i).toInt()
        i--
    }
    //multiple the nine digits for your weights: 10,9..2
    var sumProductNine = IntArray(9)
    var weight = 10
    var position = 0
    while (weight >= 2){
        sumProductNine[position] = weight * cpfNineFirst[position]
        weight--
        position++
    }
    //Verify the nineth digit
    var dvForTenthDigit = sumProductNine.sum() % 11
    dvForTenthDigit = 11 - dvForTenthDigit //rule for tenth digit
    if(dvForTenthDigit > 9)
        dvForTenthDigit = 0
    if (dvForTenthDigit != dvCurrent10)
        return false

    //### verify tenth digit
    var cpfTenFirst = cpfNineFirst.copyOf(10)
    cpfTenFirst[9] = dvCurrent10
    //multiple the nine digits for your weights: 10,9..2
    var sumProductTen = IntArray(10)
    var w = 11
    var p = 0
    while (w >= 2){
        sumProductTen[p] = w * cpfTenFirst[p]
        w--
        p++
    }
    //Verify the nineth digit
    var dvForeleventhDigit = sumProductTen.sum() % 11
    dvForeleventhDigit = 11 - dvForeleventhDigit //rule for tenth digit
    if(dvForeleventhDigit > 9)
        dvForeleventhDigit = 0
    if (dvForeleventhDigit != dvCurrent11)
        return false

    return true
}




internal fun ViewPager.onPageSelected(selection: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) = selection(position)

        override fun onPageScrollStateChanged(state: Int) = Unit

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) = Unit
    })
}