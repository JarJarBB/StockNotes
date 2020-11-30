package edu.utap.stocknotes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.get
import androidx.viewpager.widget.PagerAdapter
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class MyPageAdapter(private val theView: Int,
                    private val theContext: Context,
                    private val symbolNotes: List<SymbolNote>,
                    private val map: Map<String, List<Value>>
                    ) : PagerAdapter() {

    override fun getCount(): Int {
        return PlaceholderData.values.size // Should return the number of GraphNotes
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` // not sure about this
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(theContext)
        val layout = inflater.inflate(theView, container, false) as ViewGroup
        container.addView(layout)
        val stockName = layout[0] as TextView
        val graphView = layout[1] as GraphView
        val noteText = layout[3] as EditText
        stockName.text = symbolNotes[position].symbol
        noteText.setText(symbolNotes[position].note)
        val series = LineGraphSeries<DataPoint>()
        for (data in map[symbolNotes[position].symbol]!!) {
            series.appendData(DataPoint(data.id.toDouble(), data.value.toDouble()), true, 1000)
        }
        graphView.addSeries(series)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
        return container.removeView(view as View)
    }
}
