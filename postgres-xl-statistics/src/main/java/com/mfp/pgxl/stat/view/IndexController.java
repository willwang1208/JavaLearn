package com.mfp.pgxl.stat.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.alibaba.fastjson.JSON;
import com.mfp.pgxl.stat.view.echarts.Legend;
import com.mfp.pgxl.stat.view.echarts.Option;
import com.mfp.pgxl.stat.view.echarts.Series;
import com.mfp.pgxl.stat.view.echarts.Xaxis;

@Controller
@RequestMapping("/")
public class IndexController {
	
	@RequestMapping(value = "")
	public ModelAndView index(HttpServletRequest req) {
        return new ModelAndView(new RedirectView(req.getContextPath() + "/stat/statement"));  
    }
    
	@RequestMapping(value = "/echarts/demo", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String demo(){
		Legend legend = new Legend();
		legend.setData(Arrays.asList(new String[]{"apple", "oppo"}));
		
		Xaxis xAxis = new Xaxis();
		xAxis.setData(Arrays.asList(new String[]{"13:00:00", "13:00:10", "13:00:20", "13:00:30", "13:00:40", "13:00:50", "13:01:00"}));
		
		List<Series> series = new ArrayList<>();
		
		Series apple = new Series();
		apple.setName("apple");
		apple.setType("line");
		apple.setData(Arrays.asList(new Integer[]{10, 13, 32, 23, 76, 44, 35}));
		series.add(apple);
		
		Series oppo = new Series();
		oppo.setName("oppo");
		oppo.setType("line");
		oppo.setData(Arrays.asList(new Double[]{13.45, 23.2, 22.1, 31.1, 76.2, 44.4, 35.09}));
		series.add(oppo);
		
		Option option = new Option();
		option.setLegend(legend);
		option.setxAxis(xAxis);
		option.setSeries(series);
		
//		System.out.println(JSON.toJSONString(option));
//		Group group = JSON.parseObject(jsonString, Group.class);
		
		return JSON.toJSONString(option);
	}
}
