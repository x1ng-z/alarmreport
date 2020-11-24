package hs.alarmreport.contrl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.alarmreport.bean.AlarmJudgment;
import hs.alarmreport.bean.AlarmMessage;
import hs.alarmreport.device.MeasurePoint;
import hs.alarmreport.opc.OPCService;
import hs.alarmreport.utils.Help;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/10/31 17:12
 */
@Controller
@RequestMapping("/alarmreport")
public class ALarmInfoContrl {
    private Logger logger = LoggerFactory.getLogger(ALarmInfoContrl.class);


    @Autowired
    public void setJudgment(AlarmJudgment judgment) {
        this.judgment = judgment;
    }

    private AlarmJudgment judgment;


    @ResponseBody
    @RequestMapping("getAlarmInfo")
    public String getAlarmInfo(){

        JSONArray data=new JSONArray();
        int count=0;
        for(AlarmMessage alarmMessage:judgment.getAllCurrentAlarm()){


            String AlarmContext=null;

            switch (alarmMessage.getAlarm_Type()){

                case MeasurePoint.ALARTTYPE_LowlowAlarm:
                    AlarmContext=alarmMessage.getFix_ch_comment()+"低低报";

                    break;
                case MeasurePoint.ALARTTYPE_LowAlarm:
                    AlarmContext=alarmMessage.getFix_ch_comment()+"低报";
                    break;
                case MeasurePoint.ALARTTYPE_HighAlarm:
                    AlarmContext=alarmMessage.getFix_ch_comment()+"高报";
                    break;
                case MeasurePoint.ALARTTYPE_HHignAlarm:
                    AlarmContext=alarmMessage.getFix_ch_comment()+"高高报";
                    break;
                case MeasurePoint.ALARTTYPE_ChangeRate:
                    AlarmContext=alarmMessage.getFix_ch_comment()+"偏高";
                    break;
                default:
                    logger.warn("no such alarm type");
                    continue;
            }
            JSONObject subresult= new JSONObject();
            subresult.put("almlevel", alarmMessage.getLevel());
            subresult.put("almcontent", AlarmContext);
            subresult.put("almcurvalue", alarmMessage.getCurrentValue());
            subresult.put("almchgrate", alarmMessage.getCurrentRate());
            subresult.put("almtime", alarmMessage.getCurrentDate().toLocaleString());
            subresult.put("proline", alarmMessage.getProduction_LineId());
            subresult.put("process", alarmMessage.getFix_process().equals("生料")?"1":(alarmMessage.getFix_process().equals("烧成")?"2":"3"));
            subresult.put("defaultId",alarmMessage.getDefaultId());
            subresult.put("cause_issue",alarmMessage.getCause_issue());
            subresult.put("treatment", alarmMessage.getTreatment());
            subresult.put("is_audio",alarmMessage.isFix_is_audio());
            alarmMessage.setFix_is_audio(false);
            data.add(subresult);
            count++;
        }

        return Help.sendLayuiPage(count,data).toJSONString();
    }
}
