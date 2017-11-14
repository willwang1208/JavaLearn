package com.mfp.pgxl.stat.view.echarts;

import java.util.List;

public class Option {

	Legend legend;
	
	Xaxis xAxis;
	
	List<Series> series;

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public Xaxis getxAxis() {
		return xAxis;
	}

	public void setxAxis(Xaxis xAxis) {
		this.xAxis = xAxis;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}
	
}
