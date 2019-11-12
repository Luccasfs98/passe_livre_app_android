package com.luccas.passelivredocumentos.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import java.math.BigDecimal
import java.text.NumberFormat

object MaskUtils {
    const val CPF_MASK = "###.###.###-##"
    const val PHONE_MASK = "(##) # ####-####"
    const val PERIOD_MASK = "##/##"
    const val CC_MASK = "#### #### #### ####"
    const val CC_EXPIRE_MASK = "##/##"
    const val PHONE2_MASK = "(##) ####-####"
    const val CEP_MASK = "#####-###"
    const val BIRTH_MASK = "##/##/####"

    fun monetarioTextView(value: String): String {
        val valor = BigDecimal(value)
        val nf = NumberFormat.getCurrencyInstance()
        return nf.format(valor)
    }

    fun monetario(ediTxt: EditText): TextWatcher {
        return object : TextWatcher {
            // Mascara monetaria para o preço do produto
            private var current = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    ediTxt.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R$,.]".toRegex(), "")

                    val parsed = java.lang.Double.parseDouble(cleanString)
                    val formatted = NumberFormat.getCurrencyInstance().format(parsed / 100)

                    current = formatted.replace("[R$]".toRegex(), "")
                    ediTxt.setText(current)
                    ediTxt.setSelection(current.length)

                    ediTxt.addTextChangedListener(this)
                }
            }

        }
    }

    fun unmask(s: String): String {
        return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "")
            .replace("[/]".toRegex(), "").replace("[(]".toRegex(), "")
            .replace("[)]".toRegex(), "").replace(" ".toRegex(), "")
            .replace(",".toRegex(), "")
    }

    fun isASign(c: Char): Boolean {
        return c == '.' || c == '-' || c == '/' || c == '(' || c == ')' || c == ',' || c == ' '
    }

    fun mask(mask: String, text: String): String {
        var i = 0
        var mascara = ""
        for (m in mask.toCharArray()) {
            if (m != '#') {
                mascara += m
                continue
            }
            try {
                mascara += text[i]
            } catch (e: Exception) {
                break
            }

            i++
        }

        return mascara
    }

    fun insert(mask: String, ediTxt: EditText): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = unmask(s.toString())
                var mascara = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }

                var index = 0
                for (i in 0 until mask.length) {
                    val m = mask[i]
                    if (m != '#') {
                        if (index == str.length && str.length < old.length) {
                            continue
                        }
                        mascara += m
                        continue
                    }

                    try {
                        mascara += str[index]
                    } catch (e: Exception) {
                        break
                    }

                    index++
                }

                if (mascara.isNotEmpty()) {
                    var last_char = mascara[mascara.length - 1]
                    var hadSign = false
                    while (isASign(last_char) && str.length == old.length) {
                        mascara = mascara.substring(0, mascara.length - 1)
                        last_char = mascara[mascara.length - 1]
                        hadSign = true
                    }

                    if (mascara.isNotEmpty() && hadSign) {
                        mascara = mascara.substring(0, mascara.length - 1)
                    }
                }

                isUpdating = true
                ediTxt.setText(mascara)
                ediTxt.setSelection(mascara.length)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {}
        }
    }

    /**
     * Função para ocultar o teclado, mediante a uma quantidade X de caracteres preenchidos
     * em um EditText*/
    fun tvHideKeyboard(editText: EditText, qtdCaracteres: Int) {

        editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length == qtdCaracteres) {
                    editText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
    }
}