package edu.utap.stocknotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecAdap(private val viewModel: MyViewModel)
    : RecyclerView.Adapter<RecAdap.VH>() {

     var stocks = mutableListOf<stocksResult>()


    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        internal var shrtname = itemView.findViewById<TextView>(R.id.shortname)
        internal var lngname = itemView.findViewById<TextView>(R.id.longname)

//        init {
//            textView.setOnClickListener {
//                val previous = viewModel.selected
//                val position = adapterPosition
//                // Manipulate ViewModel here because this call back comes directly from
//                // View (though ViewHolder is an inner class)
//                viewModel.selected = position
//                if (previous >= 0)
//                    notifyItemChanged(previous)
//                notifyItemChanged(position)
//                val context = it.context
//                val item = viewModel.getListAt(position)
//                item?.let {
//                    val selected = "You selected $position ${it.name}"
//                    Toast.makeText(context, selected, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
        fun bind(item: SymbolNote) {
            shrtname.text = item.symbol
            lngname.text = item.desc
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecAdap.VH{
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.search_row, viewGroup, false)

        return VH(view)
    }

    fun add(item: stocksResult) {
        stocks.add(item)
    }
    //SSS
    fun addAll(items: List<stocksResult>) {
        removeAll()
        stocks.addAll(items)
    }
    fun removeItemAt(position: Int) {
        stocks.removeAt(position)
    }
    fun removeAll(){
        stocks = mutableListOf<stocksResult>()
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
//        holder.bind(viewModel.getSto(holder.adapterPosition))
        val s = stocks[holder.adapterPosition]
        val c = viewModel.convertToSymbol(s)
        holder.bind(c)
    }

    override fun getItemCount() = stocks.size

//    override fun getItemCount() = viewModel.getItemCount()
}