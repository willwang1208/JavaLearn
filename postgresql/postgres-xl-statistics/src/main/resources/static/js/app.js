var pgxls = {};
pgxls.loadChart = function(id, url) {
	var myChart = echarts.init(document.getElementById(id));
	// 显示标题，图例和空的坐标轴
	myChart.setOption({
		title : {
			text : id
		},
		tooltip : {},
		legend : {
			data : []
		},
		xAxis : {
			data : []
		},
		yAxis : {},
		series : []
	});

	myChart.showLoading({
		text : 'Loading...'
	});

	// 异步加载数据
	$.get(url).done(function(data) {
		myChart.setOption(data);
		myChart.hideLoading();
	});
}