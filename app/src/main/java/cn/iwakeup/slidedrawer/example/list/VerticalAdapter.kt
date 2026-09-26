package cn.iwakeup.slidedrawer.example.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.iwakeup.slidedrawer.example.R


class VerticalAdapter(
    private val items: List<List<String>>
) : RecyclerView.Adapter<VerticalAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView =
            view.findViewById(R.id.title)

        val horizontalList: RecyclerView =
            view.findViewById(R.id.horizontalList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_vertical,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.title.text = "第 ${position + 1} 行"

        holder.horizontalList.layoutManager =
            LinearLayoutManager(
                holder.itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )

//        holder.horizontalList.setOnTouchListener { v, event ->
//
//            when (event.actionMasked) {
//
//                MotionEvent.ACTION_DOWN -> {
//                    v.parent.requestDisallowInterceptTouchEvent(true)
//                }
//
//                MotionEvent.ACTION_UP,
//                MotionEvent.ACTION_CANCEL -> {
//                    v.parent.requestDisallowInterceptTouchEvent(false)
//                }
//            }
//
//            false
//        }

        holder.horizontalList.adapter =
            HorizontalAdapter(items[position])
    }

    override fun getItemCount() = items.size
}