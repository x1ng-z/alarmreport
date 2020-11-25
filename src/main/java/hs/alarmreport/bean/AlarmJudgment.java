package hs.alarmreport.bean;

import hs.alarmreport.device.DeviceMgr;
import hs.alarmreport.device.MeasurePoint;
import hs.alarmreport.opc.OPCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public class AlarmJudgment {
    private Logger logger = LoggerFactory.getLogger(AlarmJudgment.class);

    private DeviceMgr deviceMgr;
    private Map<String, List<AlarmMessage>> CurrentAlarm;

    public AlarmJudgment(DeviceMgr deviceMgr, ExecutorService executorService) {
        this.deviceMgr = deviceMgr;
        CurrentAlarm = new ConcurrentHashMap<>();

        //judgment();


        executorService.execute(new Runnable() {



            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {

                        judgment();
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

            }
        });


    }

    /***
     *     AlarType:
     *         +-----------|-------+
     *         |   status  |    NO |
     *         +-----------+-------+
     *         |LowlowAlarm|    0  |
     *         +-------------------+
     *         |LowAlarm   |    1  |
     *         +-------------------+
     *         |HighAlarm  |    2  |
     *         +-------------------+
     *         |HHignAlarm |    3  |
     *         +-------------------+
     *         |ChangeRate |    4  |
     *         +-------------------+
     *
     *     */
    public synchronized void judgment() {
        Map<String, MeasurePoint> pointMap = deviceMgr.getTotal_DBtag();

        for (MeasurePoint measurePoint : pointMap.values()) {
            if(measurePoint.getCurrent_time()==null){
                continue;
            }
            double fix_llo_lim = measurePoint.getFix_llo_lim();
            double fix_low_lim = measurePoint.getFix_low_lim();
            double fix_hign_lim = measurePoint.getFix_hign_lim();
            double fix_hhi_lim = measurePoint.getFix_hhi_lim();
            double fix_change_rate = measurePoint.getFix_change_rate();
            float current_change_rate = measurePoint.getCurrent_Change_Rate();
            float current_value = measurePoint.getCurrent_value();
//            if(sub(current_change_rate,fix_change_rate)>0.001){
//                   AlarmMessage alarmMessage=new  AlarmMessage();
//                alarmMessage.setAlarm_Type(4)
//                        .setCurrentDate(measurePoint.getCurrent_time())
//                        .setCurrentValue(current_value)
//                        .setFix_ch_comment(measurePoint.getFix_ch_comment())
//                        .setFix_de_te_el(measurePoint.getFix_de_te_el())
//                        .setFix_devicename(measurePoint.getFix_devicename())
//                        .setFix_deviceNo(measurePoint.getFix_deviceNo())
//                        .setFix_tagname(measurePoint.getFix_tagname())
//                        .setFix_union_tagname(measurePoint.getFix_union_tagname())
//                        .setFix_process(measurePoint.getFix_process())
//                        .setLevel(measurePoint.getFix_alarm_level())
//                        .setProduction_LineId(measurePoint.getFix_PRL_ID())
//                        .setProduction_Linename(measurePoint.getFix_PRL_NAME())
//                        .setCurrentRate(current_change_rate)
//                        .setFix_change_rate(measurePoint.getFix_change_rate())
//                        .setFix_hhi_lim(measurePoint.getFix_hhi_lim())
//                        .setFix_hign_lim(measurePoint.getFix_hign_lim())
//                        .setFix_low_lim(measurePoint.getFix_low_lim())
//                        .setFix_llo_lim(measurePoint.getFix_llo_lim())
//                        .setFix_is_audio(measurePoint.isFix_is_audio())
//                        .setFix_is_push(measurePoint.isFix_is_push());
//                CurrentAlarm.add(alarmMessage);
//
//                /**
//                 * HH  Alarm
//                 * */
//            }else
            if (sub(current_value, fix_hhi_lim) >0) {
                if (measurePoint.getCurrentAlarmType() != MeasurePoint.ALARTTYPE_HHignAlarm) {
                    measurePoint.setCurrentAlarmType(MeasurePoint.ALARTTYPE_HHignAlarm);
                    AlarmMessage alarmMessage = new AlarmMessage();
                    alarmMessage.setAlarm_Type(MeasurePoint.ALARTTYPE_HHignAlarm)
                            .setCurrentDate(measurePoint.getCurrent_time())
                            .setCurrentValue(current_value)
                            .setFix_ch_comment(measurePoint.getFix_ch_comment())
                            .setFix_de_te_el(measurePoint.getFix_de_te_el())
                            .setFix_devicename(measurePoint.getFix_devicename())
                            .setFix_deviceNo(measurePoint.getFix_deviceNo())
                            .setFix_tagname(measurePoint.getFix_tagname())
                            .setFix_union_tagname(measurePoint.getFix_union_tagname())
                            .setFix_process(measurePoint.getFix_process())
                            .setLevel(measurePoint.getFix_alarm_level())
                            .setProduction_LineId(measurePoint.getFix_PRL_ID())
                            .setProduction_Linename(measurePoint.getFix_PRL_NAME())
                            .setCurrentRate(current_change_rate)
                            .setFix_change_rate(measurePoint.getFix_change_rate())
                            .setFix_hhi_lim(measurePoint.getFix_hhi_lim())
                            .setFix_hign_lim(measurePoint.getFix_hign_lim())
                            .setFix_low_lim(measurePoint.getFix_low_lim())
                            .setFix_llo_lim(measurePoint.getFix_llo_lim())
                            .setFix_is_audio(measurePoint.isFix_is_audio())
                            .setFix_is_push(measurePoint.isFix_is_push())
                            .setDEV_TAG(measurePoint.getFix_tagname())
                            .setFix_is_audio(measurePoint.isFix_is_audio());

                    if (null == CurrentAlarm.get(measurePoint.getFix_tagname())) {
                        CurrentAlarm.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                    }
                    if(CurrentAlarm.get(measurePoint.getFix_tagname())!=null){
                        CurrentAlarm.get(measurePoint.getFix_tagname()).clear();
                    }

                    CurrentAlarm.get(measurePoint.getFix_tagname()).add(alarmMessage);

                }


                /**
                 * H  Alarm
                 * */
            } else {
                if ((sub(current_value, fix_hign_lim) > 0) && (sub(current_value, fix_hhi_lim) < 0)) {

                    if (measurePoint.getCurrentAlarmType() != MeasurePoint.ALARTTYPE_HighAlarm) {
                        measurePoint.setCurrentAlarmType(MeasurePoint.ALARTTYPE_HighAlarm);
                        AlarmMessage alarmMessage = new AlarmMessage();
                        alarmMessage.setAlarm_Type(MeasurePoint.ALARTTYPE_HighAlarm)
                                .setCurrentDate(measurePoint.getCurrent_time())
                                .setCurrentValue(current_value)
                                .setFix_ch_comment(measurePoint.getFix_ch_comment())
                                .setFix_de_te_el(measurePoint.getFix_de_te_el())
                                .setFix_devicename(measurePoint.getFix_devicename())
                                .setFix_deviceNo(measurePoint.getFix_deviceNo())
                                .setFix_tagname(measurePoint.getFix_tagname())
                                .setFix_union_tagname(measurePoint.getFix_union_tagname())
                                .setFix_process(measurePoint.getFix_process())
                                .setLevel(measurePoint.getFix_alarm_level())
                                .setProduction_LineId(measurePoint.getFix_PRL_ID())
                                .setProduction_Linename(measurePoint.getFix_PRL_NAME())
                                .setCurrentRate(current_change_rate)
                                .setFix_change_rate(measurePoint.getFix_change_rate())
                                .setFix_hhi_lim(measurePoint.getFix_hhi_lim())
                                .setFix_hign_lim(measurePoint.getFix_hign_lim())
                                .setFix_low_lim(measurePoint.getFix_low_lim())
                                .setFix_llo_lim(measurePoint.getFix_llo_lim())
                                .setFix_is_audio(measurePoint.isFix_is_audio())
                                .setFix_is_push(measurePoint.isFix_is_push())
                                .setDEV_TAG(measurePoint.getFix_tagname())
                                .setFix_is_audio(measurePoint.isFix_is_audio());
                        if (null == CurrentAlarm.get(measurePoint.getFix_tagname())) {
                            CurrentAlarm.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                        }
                        if(CurrentAlarm.get(measurePoint.getFix_tagname())!=null){
                            CurrentAlarm.get(measurePoint.getFix_tagname()).clear();
                        }

                        CurrentAlarm.get(measurePoint.getFix_tagname()).add(alarmMessage);
                    }
                }
                /**
                 * L L  Alarm
                 * */
                else {

                    if (sub(current_value, fix_llo_lim) < 0) {
                        if (measurePoint.getCurrentAlarmType() != MeasurePoint.ALARTTYPE_LowlowAlarm) {
                            measurePoint.setCurrentAlarmType(MeasurePoint.ALARTTYPE_LowlowAlarm);
                            AlarmMessage alarmMessage = new AlarmMessage();
                            alarmMessage.setAlarm_Type(MeasurePoint.ALARTTYPE_LowlowAlarm)
                                    .setCurrentDate(measurePoint.getCurrent_time())
                                    .setCurrentValue(current_value)
                                    .setFix_ch_comment(measurePoint.getFix_ch_comment())
                                    .setFix_de_te_el(measurePoint.getFix_de_te_el())
                                    .setFix_devicename(measurePoint.getFix_devicename())
                                    .setFix_deviceNo(measurePoint.getFix_deviceNo())
                                    .setFix_tagname(measurePoint.getFix_tagname())
                                    .setFix_union_tagname(measurePoint.getFix_union_tagname())
                                    .setFix_process(measurePoint.getFix_process())
                                    .setLevel(measurePoint.getFix_alarm_level())
                                    .setProduction_LineId(measurePoint.getFix_PRL_ID())
                                    .setProduction_Linename(measurePoint.getFix_PRL_NAME())
                                    .setCurrentRate(current_change_rate)
                                    .setFix_change_rate(measurePoint.getFix_change_rate())
                                    .setFix_hhi_lim(measurePoint.getFix_hhi_lim())
                                    .setFix_hign_lim(measurePoint.getFix_hign_lim())
                                    .setFix_low_lim(measurePoint.getFix_low_lim())
                                    .setFix_llo_lim(measurePoint.getFix_llo_lim())
                                    .setFix_is_audio(measurePoint.isFix_is_audio())
                                    .setFix_is_push(measurePoint.isFix_is_push())
                                    .setDEV_TAG(measurePoint.getFix_tagname())
                                    .setFix_is_audio(measurePoint.isFix_is_audio());

                            if (null == CurrentAlarm.get(measurePoint.getFix_tagname())) {
                                CurrentAlarm.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                            }
                            if(CurrentAlarm.get(measurePoint.getFix_tagname())!=null){
                                CurrentAlarm.get(measurePoint.getFix_tagname()).clear();
                            }

                            CurrentAlarm.get(measurePoint.getFix_tagname()).add(alarmMessage);
                        }
                    }
                    /**
                     * L  Alarm
                     * */
                    else {

                        if ((sub(current_value, fix_low_lim) < 0) && (sub(current_value, fix_llo_lim) > 0)) {
                            if (measurePoint.getCurrentAlarmType() != MeasurePoint.ALARTTYPE_LowAlarm) {
                                measurePoint.setCurrentAlarmType(MeasurePoint.ALARTTYPE_LowAlarm);
                                AlarmMessage alarmMessage = new AlarmMessage();
                                alarmMessage.setAlarm_Type(MeasurePoint.ALARTTYPE_LowAlarm)
                                        .setCurrentDate(measurePoint.getCurrent_time())
                                        .setCurrentValue(current_value)
                                        .setFix_ch_comment(measurePoint.getFix_ch_comment())
                                        .setFix_de_te_el(measurePoint.getFix_de_te_el())
                                        .setFix_devicename(measurePoint.getFix_devicename())
                                        .setFix_deviceNo(measurePoint.getFix_deviceNo())
                                        .setFix_tagname(measurePoint.getFix_tagname())
                                        .setFix_union_tagname(measurePoint.getFix_union_tagname())
                                        .setFix_process(measurePoint.getFix_process())
                                        .setLevel(measurePoint.getFix_alarm_level())
                                        .setProduction_LineId(measurePoint.getFix_PRL_ID())
                                        .setProduction_Linename(measurePoint.getFix_PRL_NAME())
                                        .setCurrentRate(current_change_rate)
                                        .setFix_change_rate(measurePoint.getFix_change_rate())
                                        .setFix_hhi_lim(measurePoint.getFix_hhi_lim())
                                        .setFix_hign_lim(measurePoint.getFix_hign_lim())
                                        .setFix_low_lim(measurePoint.getFix_low_lim())
                                        .setFix_llo_lim(measurePoint.getFix_llo_lim())
                                        .setFix_is_audio(measurePoint.isFix_is_audio())
                                        .setFix_is_push(measurePoint.isFix_is_push())
                                        .setDEV_TAG(measurePoint.getFix_tagname())
                                        .setFix_is_audio(measurePoint.isFix_is_audio());

                                if (null == CurrentAlarm.get(measurePoint.getFix_tagname())) {
                                    CurrentAlarm.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                                }
                                if(CurrentAlarm.get(measurePoint.getFix_tagname())!=null){
                                    CurrentAlarm.get(measurePoint.getFix_tagname()).clear();
                                }

                                CurrentAlarm.get(measurePoint.getFix_tagname()).add(alarmMessage);
                            }

                        } else {
                            measurePoint.setCurrentAlarmType(MeasurePoint.ALARTTYPE_NOAlarm);
                            if(CurrentAlarm.get(measurePoint.getFix_tagname())!=null){
                                CurrentAlarm.get(measurePoint.getFix_tagname()).clear();
                            }
                        }
                    }


                }


            }


        }

        hook();


    }

    public void hook() {

    }

    public  List<AlarmMessage> getAllCurrentAlarm() {
        List<AlarmMessage> selectResult = new ArrayList<AlarmMessage>();

        for (List<AlarmMessage> alarmMessages : CurrentAlarm.values()) {
            selectResult.addAll(alarmMessages);
        }

        return selectResult;
    }

    public synchronized List<AlarmMessage> getCurrentAlarmByDevicename(String devicename, String PLNo) {

        ;
        List<AlarmMessage> selectResult = new ArrayList<AlarmMessage>();

        for (List<AlarmMessage> alarmMessages : CurrentAlarm.values()) {
            for(AlarmMessage alarmMessage:alarmMessages){
                if (alarmMessage.getProduction_LineId().equals(PLNo) && alarmMessage.getFix_deviceNo().equals(devicename)) {
                    selectResult.add(alarmMessage);
                }
            }
        }

        return selectResult;
    }

    public synchronized List<AlarmMessage> getCurrentAlarmByPlNo(String PLNo) {


        List<AlarmMessage> selectResult = new ArrayList<AlarmMessage>();

        for (List<AlarmMessage> alarmMessages : CurrentAlarm.values()) {
            for(AlarmMessage alarmMessage:alarmMessages){
                if (alarmMessage.getProduction_LineId().equals(PLNo)) {
                    selectResult.add(alarmMessage);
                }
            }
        }
        return selectResult;
    }


    private static double sub(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

    private static double sum(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


}
