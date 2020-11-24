package hs.alarmreport.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * @author zzx
 * @version 1.0
 * @date 2020/10/8 13:23
 */
public class Help {

    /**
     * @param count 数量
     * @param datas 数据
     *
     * */
    public static JSONObject sendLayuiPage(int count, JSONArray datas){
        JSONObject result = new JSONObject();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("count", count);
        result.put("data", datas);
        return result;
    }

}
