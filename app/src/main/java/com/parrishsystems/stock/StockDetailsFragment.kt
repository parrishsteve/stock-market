package com.parrishsystems.stock

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.androidplot.xy.*
import com.parrishsystems.stock.repo.SavedSymbols
import com.parrishsystems.stock.viewmodel.StockDetailsViewModel
import com.parrishsystems.stock.viewmodel.StockDetailsViewModelFactory
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition

class StockDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = StockDetailsFragment()
    }

    private lateinit var viewModel: StockDetailsViewModel
    private lateinit var h: Holder

    private lateinit var plot: XYPlot

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.stock_details_fragment, container, false)
        h = Holder(view)
        plot = view.findViewById(R.id.plot);
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this,
            StockDetailsViewModelFactory(SavedSymbols.instance)
        ).get(StockDetailsViewModel::class.java)

        viewModel.data.observe(this, Observer {
            initView(it)
            this.activity?.title = it.name
        })

        viewModel.getData()

        viewModel.intradayData.observe(this, Observer {
            activity!!.runOnUiThread {
                plotData(it)
            }
        })

        viewModel.errorMsg.observe(this, Observer<String> {
            Toast.makeText(context!!.applicationContext, it, Toast.LENGTH_LONG).show()
        })

        //viewModel.getIntradayData()
        //plotData(null)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getIntradayData()

    }


    private fun initView(s: StockDetailsViewModel.CompanyView) {
        h.tvPrice.text = s.price
        h.tvOpen.text = s.open
        h.tvDayChange.text = s.dayChange
        h.tvDayChangePct.text = s.dayChangePct
        h.tvPrevClose.text = s.prevClose
        h.tvDayRange.text = s.dayRange
        h.tvYearRange.text = s.yearRange
        h.tvVolume.text = s.volume
        h.tvOther.text = s.other
        h.tvExchange.text = s.exchange

        val resourceId = resources.getColor(if (s.isPriceUp) R.color.price_current_up else R.color.price_current_down, null)
        h.tvPrice.setTextColor(resourceId)
        h.tvDayChange.setTextColor(resourceId)
        h.tvDayChangePct.setTextColor(resourceId)

    }

    inner class Holder(val view: View) {
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvOpen: TextView = view.findViewById(R.id.tvOpen)
        val tvDayChange: TextView = view.findViewById(R.id.tvDayChange)
        val tvDayChangePct: TextView = view.findViewById(R.id.tvDayChangePct)
        val tvPrevClose: TextView = view.findViewById(R.id.tvPrevClose)
        val tvDayRange: TextView = view.findViewById(R.id.tvDayRange)
        val tvYearRange: TextView = view.findViewById(R.id.tvYearRange)
        val tvVolume: TextView = view.findViewById(R.id.tvVolume)
        val tvOther: TextView = view.findViewById(R.id.tvOther)
        val tvExchange: TextView = view.findViewById(R.id.tvExchange)
    }

    fun plotData(points : List<StockDetailsViewModel.IntradayView>?) {
    //fun plotData(points : List<StockDetailsViewModel.IntradayView>) {

        //plot.setRangeBoundaries(1, 3, BoundaryMode.FIXED)

        //plot.setDomainStep(StepMode.SUBDIVIDE, points.size.toDouble())

        plot.setDomainStep(StepMode.SUBDIVIDE, 3.0)
        plot.setRangeStep(StepMode.SUBDIVIDE, 3.0)

        var series1Numbers: List<Number> = listOf(1, 3, 2)

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)

        val yList = mutableListOf<Number>()
        //points.forEach {
        //    yList.add(it.price)
        //}


        //val series1: XYSeries  =  SimpleXYSeries(yList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Steve")

        val series1: XYSeries  =  SimpleXYSeries(series1Numbers, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Steve")

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        var series1Format = LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels)


        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
            CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal))

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format)



        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = (object: Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer?,
                pos: FieldPosition?
            ): StringBuffer {
                var i = Math.round((obj as Number).toFloat())
                return toAppendTo?.append(i.toString())!!
            }

            override fun parseObject(source: String?, pos: ParsePosition?): Any {
                return Any()
            }

        })


    }
}
