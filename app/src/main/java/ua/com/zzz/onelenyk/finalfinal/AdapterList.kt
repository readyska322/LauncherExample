package ua.com.zzz.onelenyk.finalfinal

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*


class AdapterList(
        context: Context,
        private val listener: View.OnClickListener,
        private val list: List<AppDetail>) : RecyclerView.Adapter<AdapterList.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = list[position]

        holder.setTag(position)
        holder.appIcon.setImageDrawable(pos.icon)
        holder.appLabel.text = pos.label
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder internal constructor(view: View, listener: View.OnClickListener) : RecyclerView.ViewHolder(view) {
        var appIcon = view.item_app_icon
        var appLabel = view.item_app_label

        init {
            view.setOnClickListener(listener)
        }

        fun setTag(id: Int) {
            itemView.tag = id
        }

    }

}