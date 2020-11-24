package hs.alarmreport.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AlarmMessage {
    private Logger logger = LoggerFactory.getLogger(AlarmMessage.class);

     /**
    AlarType:
        +-----------|-------+
        |   status  |    NO |
        +-----------+-------+
        |LowlowAlarm|    0  |
        +-------------------+
        |LowAlarm   |    1  |
        +-------------------+
        |HighAlarm  |    2  |
        +-------------------+
        |HHignAlarm |    3  |
        +-------------------+
        |ChangeRate |    4  |
        +-------------------+

    */
     private String DEV_TAG;
    private String production_Linename;
    private String production_LineId;
    private String fix_devicename;//0主机设备（DEV_NAME）
    private String fix_deviceNo;//1主机设备编号（DEV_NO ）
    private String fix_union_tagname;//3位号说明编号（ DEV_TAG_NO）
    private String fix_tagname;//4位号	           DEV_TAG
    private String fix_ch_comment;//2位号说明	   DEV_TAGCH
    private List<String> fix_de_te_el;//设备|工艺|电气	   DE_TE_EL
    private String fix_process;//工序	           DEV_PRO

    private float fix_hhi_lim;//高高限	           HHI_LIM
    private float fix_hign_lim;//高限	           HIGH_LIM
    private float fix_llo_lim;//低低限	           LLO_LIM
    private float fix_low_lim;//低限	           LOW_LIM
    private float fix_change_rate;//变化率



    private float CurrentValue=0f;
    private Integer Alarm_Type=null;
    private float CurrentRate=0f;
    private Date CurrentDate=null;
    private float Level;


    private String defaultId=null;//md5
    private List<String> cause_issue=null;
    private List<String> Treatment=null;

    //SEND CONTRL
    private boolean fix_is_audio;
    private boolean fix_is_push;


    public AlarmMessage(){}

    public AlarmMessage(float CurrentValue,
                        String Production_Linename,
                        String fix_tagname,
                        int Alarm_Type,  //LowLevel HighLevel ChangeRate Recovery
                        float CurrentRate,
                        Date CurrentDate,
                        float level,
                        String fixprocess,
                        String fix_devicename,
                        String fix_ch_comment,
                        String fix_deviceNo,
                        String fix_union_tagname,
                        List<String> fix_de_te_el,
                        String production_LineId,
                        float fix_hhi_lim,//高高限	           HHI_LIM
                        float fix_hign_lim,//高限	           HIGH_LIM
                        float fix_llo_lim,//低低限	           LLO_LIM
                        float fix_low_lim,//低限	           LOW_LIM
                        float fix_change_rate,/*变化率*/
                        boolean fix_is_audio,
                        boolean fix_is_push,
                        String DEV_TAG){

        this.CurrentValue=CurrentValue;
        this.production_Linename = Production_Linename;
        this.fix_tagname = fix_tagname;
        this.Alarm_Type=Alarm_Type;  //LowLevel HighLevel ChangeRate Recovery
        this.CurrentRate=CurrentRate;
        this.CurrentDate=CurrentDate;
        this.Level=level;
        this.fix_process = fixprocess;
        this.fix_ch_comment=fix_ch_comment;
        this.fix_de_te_el=fix_de_te_el;
        this.fix_deviceNo=fix_deviceNo;
        this.fix_devicename=fix_devicename;
        this.fix_union_tagname=fix_union_tagname;
        this.production_LineId=production_LineId;
        this.fix_hhi_lim=fix_hhi_lim;
        this.fix_hign_lim=fix_hign_lim;
        this.fix_llo_lim=fix_llo_lim;
        this.fix_low_lim=fix_low_lim;
        this.fix_change_rate=fix_change_rate;
        this.fix_is_audio=fix_is_audio;
        this.fix_is_push=fix_is_push;
        this.DEV_TAG=DEV_TAG;
    }




    @Override
    public Object clone() throws CloneNotSupportedException {
        List<String> copy=new ArrayList<String>();
        for(String el:fix_de_te_el){
            copy.add(el);
        }

        return new   AlarmMessage(CurrentValue,
                production_Linename,
                fix_tagname,
                Alarm_Type,  //LowLevel HighLevel ChangeRate Recovery
                CurrentRate, (Date) CurrentDate.clone(),Level, fix_process,fix_devicename,fix_ch_comment,fix_deviceNo,fix_union_tagname,copy,production_LineId, fix_hhi_lim, fix_hign_lim, fix_llo_lim, fix_low_lim ,fix_change_rate/*变化率*/,fix_is_audio,fix_is_push,DEV_TAG);
    }

    @Override
    public String toString() {
        return  CurrentValue
                + production_Linename
                + fix_tagname
                +Alarm_Type
                +CurrentRate
                +CurrentDate
                + Level
                + fix_process;

    }


    public double getCurrentValue() {
        return CurrentValue;
    }

    public   AlarmMessage setCurrentValue(float currentvalue) {
        CurrentValue = currentvalue;
        return this;
    }

    public String getProduction_Linename() {
        return production_Linename;
    }

    public   AlarmMessage setProduction_Linename(String production_Linename) {
        this.production_Linename = production_Linename;
        return this;
    }

    public String getFix_tagname() {
        return fix_tagname;
    }

    public   AlarmMessage setFix_tagname(String fix_tagname) {
        this.fix_tagname = fix_tagname;
        return this;
    }
    public int getAlarm_Type() {
        return Alarm_Type;
    }

    public   AlarmMessage setAlarm_Type(int alarm_Type) {
        Alarm_Type = alarm_Type;
        return this;
    }

    public double getCurrentRate() {
        return CurrentRate;
    }

    public   AlarmMessage setCurrentRate(float currentRate) {
        CurrentRate = currentRate;
        return this;
    }

    public Date getCurrentDate() {
        return CurrentDate;
    }

    public   AlarmMessage setCurrentDate(Date currentDate) {
        CurrentDate = currentDate;
        return this;
    }

    public float getLevel() {
        return Level;
    }

    public   AlarmMessage setLevel(float level) {
        this.Level = level;
        return this;
    }



    public String getFix_process() {
        return fix_process;
    }

    public   AlarmMessage setFix_process(String fix_process) {
        this.fix_process = fix_process;
        return this;
    }




    public String getFix_devicename() {
        return fix_devicename;
    }

    public   AlarmMessage setFix_devicename(String fix_devicename) {
        this.fix_devicename = fix_devicename;
        return this;
    }

    public String getFix_deviceNo() {
        return fix_deviceNo;
    }

    public   AlarmMessage setFix_deviceNo(String fix_deviceNo) {
        this.fix_deviceNo = fix_deviceNo;
        return this;
    }

    public String getFix_union_tagname() {
        return fix_union_tagname;
    }

    public   AlarmMessage setFix_union_tagname(String fix_union_tagname) {
        this.fix_union_tagname = fix_union_tagname;
        return this;
    }

    public String getFix_ch_comment() {
        return fix_ch_comment;
    }

    public   AlarmMessage setFix_ch_comment(String fix_ch_comment) {
        this.fix_ch_comment = fix_ch_comment;
        return this;
    }

    public List<String> getFix_de_te_el() {
        return fix_de_te_el;
    }

    public   AlarmMessage setFix_de_te_el(List<String> fix_de_te_el) {
        this.fix_de_te_el = fix_de_te_el;
        return this;
    }

    public   AlarmMessage setAlarm_Type(Integer alarm_Type) {
        Alarm_Type = alarm_Type;
        return this;
    }

    public String getDefaultId() {
        return defaultId;
    }

    public   AlarmMessage setDefaultId(String defaultId) {
        this.defaultId = defaultId;
        return this;
    }

    public List<String> getCause_issue() {
        return cause_issue;
    }

    public   AlarmMessage setCause_issue(List<String> cause_issue) {
        this.cause_issue = cause_issue;
        return this;
    }

    public List<String> getTreatment() {
        return Treatment;
    }

    public   AlarmMessage setTreatment(List<String> treatment) {
        Treatment = treatment;
        return this;
    }

    public String getProduction_LineId() {
        return production_LineId;
    }

    public   AlarmMessage setProduction_LineId(String production_LineId) {
        this.production_LineId = production_LineId;
        return this;
    }

    public float getFix_hhi_lim() {
        return fix_hhi_lim;
    }

    public   AlarmMessage setFix_hhi_lim(float fix_hhi_lim) {
        this.fix_hhi_lim = fix_hhi_lim;
        return this;
    }

    public float getFix_hign_lim() {
        return fix_hign_lim;
    }

    public   AlarmMessage setFix_hign_lim(float fix_hign_lim) {
        this.fix_hign_lim = fix_hign_lim;
        return this;
    }

    public float getFix_llo_lim() {
        return fix_llo_lim;
    }

    public   AlarmMessage setFix_llo_lim(float fix_llo_lim) {
        this.fix_llo_lim = fix_llo_lim;
        return this;
    }

    public float getFix_low_lim() {
        return fix_low_lim;
    }

    public   AlarmMessage setFix_low_lim(float fix_low_lim) {
        this.fix_low_lim = fix_low_lim;
        return this;
    }

    public float getFix_change_rate() {
        return fix_change_rate;
    }

    public   AlarmMessage setFix_change_rate(float fix_change_rate) {
        this.fix_change_rate = fix_change_rate;
        return this;
    }

    public boolean isFix_is_audio() {
        return fix_is_audio;
    }

    public   AlarmMessage setFix_is_audio(boolean fix_is_audio) {
        this.fix_is_audio = fix_is_audio;
        return this;
    }

    public boolean isFix_is_push() {
        return fix_is_push;
    }

    public   AlarmMessage setFix_is_push(boolean fix_is_push) {
        this.fix_is_push = fix_is_push;
        return this;
    }

    public String getDEV_TAG() {
        return DEV_TAG;
    }

    public AlarmMessage setDEV_TAG(String DEV_TAG) {
        this.DEV_TAG = DEV_TAG;
        return this;
    }
}
