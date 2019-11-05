package com.luccas.passelivredocumentos.ui.base

import androidx.recyclerview.widget.RecyclerView

/**
 * Generic Base adapter for recycler views
 */
public abstract class BaseAdapter<T : RecyclerView.ViewHolder, D> : RecyclerView.Adapter<T>() {
    abstract fun setData(data: List<*>)
}