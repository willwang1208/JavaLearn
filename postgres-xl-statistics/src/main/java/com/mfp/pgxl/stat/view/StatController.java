package com.mfp.pgxl.stat.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mfp.pgxl.stat.service.StatisticsService;
import com.mfp.pgxl.stat.utils.AllInOne;
import com.mfp.pgxl.stat.view.echarts.Legend;
import com.mfp.pgxl.stat.view.echarts.Option;
import com.mfp.pgxl.stat.view.echarts.Series;
import com.mfp.pgxl.stat.view.echarts.Xaxis;

@Controller
@RequestMapping("/stat")
public class StatController {
    
    @Autowired
    private Environment env;
    
    @Autowired
    StatisticsService statisticsService;
    
    @RequestMapping("/statement")
    public String statement(HttpServletRequest req, ModelMap map) {
        // 设置应用名称和标签栏标记
        map.addAttribute("appName", AllInOne.getValueIfNullThenDefault(env.getProperty("app.name"), ""));
        map.addAttribute("navTag", "statement");
        
        // 取参，全空将尝试读取默认值
        String node = req.getParameter("node");
        String unit = req.getParameter("unit");
        String begin_time = req.getParameter("begin_time");
        String end_time = req.getParameter("end_time");
        if(AllInOne.isAllNull(node, begin_time, end_time, unit)){
            String con = statisticsService.findDefaultConditions("statement");
            if(con != null){
                JSONObject jo = JSON.parseObject(con);
                node = (String)jo.get("p_node");
                unit = (String)jo.get("p_unit");
                //时间默认写死1小时
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE, 1);
                end_time = formatter.format(calendar.getTime());
                calendar.add(Calendar.HOUR, -1);
                begin_time = formatter.format(calendar.getTime());
            }
        }
        
        // 设置用于组织页面的数据
        map.addAttribute("p_node", node);
        map.addAttribute("p_unit", unit);
        map.addAttribute("p_begin_time", begin_time);
        map.addAttribute("p_end_time", end_time);
        String[] coords = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.coord.names"), "").split(",");
        String[] datanodes = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(",");
        String[] nodenames = new String[coords.length + datanodes.length];
        System.arraycopy(coords, 0, nodenames, 0, coords.length);
        System.arraycopy(datanodes, 0, nodenames, coords.length, datanodes.length);
        map.addAttribute("nodenames", nodenames);
        
        // 是否保存为默认值
        String set_default = req.getParameter("set_default");
        if("1".equals(set_default)){
            statisticsService.saveDefaultConditions("statement", JSON.toJSONString(map));
        }
        
        // 对应thymeleaf模板目录下的文件名
        return "statement";
    }
    
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/statement/{node}/{unit}/{beginTime}/{endTime}", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String statement_tpu(@PathVariable String node, @PathVariable String unit, @PathVariable String beginTime, @PathVariable String endTime){
        
        if(beginTime != null){
            beginTime = beginTime.replaceAll("_", " ");
        }
        
        if(endTime != null){
            endTime = endTime.replaceAll("_", " ");
        }
        
        Object[] objs = statisticsService.tpu(node, beginTime, endTime, unit);
        
        if(objs != null){
            Legend legend = new Legend();
            legend.setData(Arrays.asList(new String[]{node}));
            
            Xaxis xAxis = new Xaxis();
            xAxis.setData((List<String>)objs[0]);
            
            List<Series> series = new ArrayList<>();
            
            Series coord = new Series();
            coord.setName(node);
            coord.setType("line");
            coord.setData((List<Number>)objs[1]);
            series.add(coord);
            
            Option option = new Option();
            option.setLegend(legend);
            option.setxAxis(xAxis);
            option.setSeries(series);
            return JSON.toJSONString(option);
        }
        return "{}";
    }
    
    @RequestMapping("/top-sql")
    public String top_sql(HttpServletRequest req, ModelMap map) {
        map.addAttribute("appName", AllInOne.getValueIfNullThenDefault(env.getProperty("app.name"), ""));
        map.addAttribute("navTag", "top-sql");
        
        String node = req.getParameter("node");
        String[] dbs = req.getParameterValues("dbs");
        String[] rolnames = req.getParameterValues("rolnames");
        String order_col = req.getParameter("order_col");
        String asc_or_desc = req.getParameter("asc_or_desc");
        if(AllInOne.isAllNull(node, dbs, rolnames, order_col, asc_or_desc)){
            String con = statisticsService.findDefaultConditions("top-sql");
            if(con != null){
                JSONObject jo = JSON.parseObject(con);
                node = (String)jo.get("p_node");
                JSONArray ja_dbs = (JSONArray)jo.get("p_dbs");
                if(ja_dbs != null){
                	dbs = ja_dbs.toArray(new String[ja_dbs.size()]);
                }
                JSONArray ja_rolnames = (JSONArray)jo.get("p_rolnames");
                if(ja_rolnames != null){
                	rolnames = ja_rolnames.toArray(new String[ja_rolnames.size()]);
                }
                order_col = (String)jo.get("p_order_col");
                asc_or_desc = (String)jo.get("p_asc_or_desc");
            }
        }
        
        map.addAttribute("p_node", node);
        map.addAttribute("p_dbs", dbs);
        map.addAttribute("p_rolnames", rolnames);
        map.addAttribute("p_order_col", order_col);
        map.addAttribute("p_asc_or_desc", asc_or_desc);
        map.addAttribute("rs", statisticsService.top_sql(node, rolnames, dbs, order_col, asc_or_desc));
        map.addAttribute("dbnames", statisticsService.distinct_name("snap_pg_stat_statements", "dbname"));
        map.addAttribute("rolnames", statisticsService.distinct_name("snap_pg_stat_statements", "rolname"));
        String[] coords = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.coord.names"), "").split(",");
        String[] datanodes = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(",");
        String[] nodenames = new String[coords.length + datanodes.length];
        System.arraycopy(coords, 0, nodenames, 0, coords.length);
        System.arraycopy(datanodes, 0, nodenames, coords.length, datanodes.length);
        map.addAttribute("nodenames", nodenames);
        
        String set_default = req.getParameter("set_default");
        if("1".equals(set_default)){
            statisticsService.saveDefaultConditions("top-sql", JSON.toJSONString(map));
        }
        
        return "top-sql";  
    }
    
    @RequestMapping("/activity")
    public String activity(HttpServletRequest req, ModelMap map) {
        map.addAttribute("appName", AllInOne.getValueIfNullThenDefault(env.getProperty("app.name"), ""));
        map.addAttribute("navTag", "activity");
        
        String node = req.getParameter("node");
        String[] dbs = req.getParameterValues("dbs");
        String order_col = req.getParameter("order_col");
        String asc_or_desc = req.getParameter("asc_or_desc");
        if(AllInOne.isAllNull(node, dbs, order_col, asc_or_desc)){
            String con = statisticsService.findDefaultConditions("activity");
            if(con != null){
                JSONObject jo = JSON.parseObject(con);
                node = (String)jo.get("p_node");
                JSONArray ja_dbs = (JSONArray)jo.get("p_dbs");
                if(ja_dbs != null){
                	dbs = ja_dbs.toArray(new String[ja_dbs.size()]);
                }
                order_col = (String)jo.get("p_order_col");
                asc_or_desc = (String)jo.get("p_asc_or_desc");
            }
        }
        
        map.addAttribute("p_node", node);
        map.addAttribute("p_dbs", dbs);
        map.addAttribute("p_order_col", order_col);
        map.addAttribute("p_asc_or_desc", asc_or_desc);
        map.addAttribute("rs", statisticsService.activity(node, dbs, order_col, asc_or_desc));
        map.addAttribute("dbnames", statisticsService.distinct_name("snap_pg_stat_activity", "datname"));
        String[] coords = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.coord.names"), "").split(",");
        String[] datanodes = AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(",");
        String[] nodenames = new String[coords.length + datanodes.length];
        System.arraycopy(coords, 0, nodenames, 0, coords.length);
        System.arraycopy(datanodes, 0, nodenames, coords.length, datanodes.length);
        map.addAttribute("nodenames", nodenames);
        
        String set_default = req.getParameter("set_default");
        if("1".equals(set_default)){
            statisticsService.saveDefaultConditions("activity", JSON.toJSONString(map));
        }
        
        return "activity";  
    }
    
    @RequestMapping("/tables")
    public String tables(HttpServletRequest req, ModelMap map) {
        map.addAttribute("appName", AllInOne.getValueIfNullThenDefault(env.getProperty("app.name"), ""));
        map.addAttribute("navTag", "tables");
        
        String node = req.getParameter("node");
        String[] dbs = req.getParameterValues("dbs");
        String[] schemas = req.getParameterValues("schemas");
        String order_col = req.getParameter("order_col");
        String asc_or_desc = req.getParameter("asc_or_desc");
        if(AllInOne.isAllNull(node, dbs, schemas, order_col, asc_or_desc)){
            String con = statisticsService.findDefaultConditions("tables");
            if(con != null){
                JSONObject jo = JSON.parseObject(con);
                node = (String)jo.get("p_node");
                JSONArray ja_dbs = (JSONArray)jo.get("p_dbs");
                if(ja_dbs != null){
                	dbs = ja_dbs.toArray(new String[ja_dbs.size()]);
                }
                JSONArray ja_schemas = (JSONArray)jo.get("p_schemas");
                if(ja_schemas != null){
                	schemas = ja_schemas.toArray(new String[ja_schemas.size()]);
                }
                order_col = (String)jo.get("p_order_col");
                asc_or_desc = (String)jo.get("p_asc_or_desc");
            }
        }
        
        map.addAttribute("p_node", node);
        map.addAttribute("p_dbs", dbs);
        map.addAttribute("p_schemas", schemas);
        map.addAttribute("p_order_col", order_col);
        map.addAttribute("p_asc_or_desc", asc_or_desc);
        map.addAttribute("rs", statisticsService.table_stat(node, dbs, schemas, order_col, asc_or_desc));
        map.addAttribute("nodenames", AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(","));
        map.addAttribute("dbnames", statisticsService.distinct_name("stat_table", "db"));
        map.addAttribute("schemanames", statisticsService.distinct_name("stat_table", "schemaname"));
        
        String set_default = req.getParameter("set_default");
        if("1".equals(set_default)){
            statisticsService.saveDefaultConditions("tables", JSON.toJSONString(map));
        }
        
        return "tables";  
    }
    
    @RequestMapping("/indexes")
    public String indexes(HttpServletRequest req, ModelMap map) {
        map.addAttribute("appName", AllInOne.getValueIfNullThenDefault(env.getProperty("app.name"), ""));
        map.addAttribute("navTag", "indexes");
        
        String node = req.getParameter("node");
        String[] dbs = req.getParameterValues("dbs");
        String[] schemas = req.getParameterValues("schemas");
        String order_col = req.getParameter("order_col");
        String asc_or_desc = req.getParameter("asc_or_desc");
        if(AllInOne.isAllNull(node, dbs, schemas, order_col, asc_or_desc)){
            String con = statisticsService.findDefaultConditions("indexes");
            if(con != null){
                JSONObject jo = JSON.parseObject(con);
                node = (String)jo.get("p_node");
                JSONArray ja_dbs = (JSONArray)jo.get("p_dbs");
                if(ja_dbs != null){
                	dbs = ja_dbs.toArray(new String[ja_dbs.size()]);
                }
                JSONArray ja_schemas = (JSONArray)jo.get("p_schemas");
                if(ja_schemas != null){
                	schemas = ja_schemas.toArray(new String[ja_schemas.size()]);
                }
                order_col = (String)jo.get("p_order_col");
                asc_or_desc = (String)jo.get("p_asc_or_desc");
            }
        }
        
        map.addAttribute("p_node", node);
        map.addAttribute("p_dbs", dbs);
        map.addAttribute("p_schemas", schemas);
        map.addAttribute("p_order_col", order_col);
        map.addAttribute("p_asc_or_desc", asc_or_desc);
        map.addAttribute("rs", statisticsService.index_stat(node, dbs, schemas, order_col, asc_or_desc));
        map.addAttribute("nodenames", AllInOne.getValueIfNullThenDefault(env.getProperty("pgdb.dn.names"), "").split(","));
        map.addAttribute("dbnames", statisticsService.distinct_name("stat_index", "db"));
        map.addAttribute("schemanames", statisticsService.distinct_name("stat_index", "schemaname"));
        
        String set_default = req.getParameter("set_default");
        if("1".equals(set_default)){
            statisticsService.saveDefaultConditions("indexes", JSON.toJSONString(map));
        }
        
        return "indexes";  
    }
}
