package hs.alarmreport.device;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MeasurePoint {
    //属性设置
    private String fix_devicename;//0主机设备（DEV_NAME）
    private String fix_deviceNo;//1主机设备编号（DEV_NO ）
    private String fix_union_tagname;//3位号说明编号（ DEV_TAG_NO）
    private String fix_tagname;//4位号	           DEV_TAG
    private String fix_ch_comment;//2位号说明	   DEV_TAGCH
    private List<String> fix_de_te_el;//设备|工艺|电气	   DE_TE_EL
    private float fix_hhi_lim;//高高限	           HHI_LIM
    private float fix_hign_lim;//高限	           HIGH_LIM
    private float fix_llo_lim;//低低限	           LLO_LIM
    private float fix_low_lim;//低限	           LOW_LIM
    private float fix_change_rate;//变化率	           CHA_RATE
    private boolean fix_is_audio;//是否语音报警	   IS_AUDIO
    private float fix_alarm_level;//报警等级	   ALM_LEV
    private String fix_process;//工序	           DEV_PRO
    private boolean fix_is_push;//是否推送	   IS_PUSH
    private float fix_healthvalue;//健康值	           HEA_VALUE
    private boolean fix_is_main;//是否主要参数       IS_MAIN
    private String fix_PRL_ID;
    private String fix_PRL_NAME;
    private boolean fix_IS_SCORE;

    private float score_base;//基础分值


    private float current_value;//当前值



    /*AlarType:
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


    public final static int ALARTTYPE_LowlowAlarm=0;
    public final static int ALARTTYPE_LowAlarm=1;
    public final static int ALARTTYPE_HighAlarm=2;
    public final static int ALARTTYPE_HHignAlarm=3;
    public final static int ALARTTYPE_ChangeRate=4;
    public final static int ALARTTYPE_NOAlarm=-1;

    private int currentAlarmType =ALARTTYPE_NOAlarm;//当前报警类型
    private ArrayList<Date> current_dateList;//一段数据的日期
    private ArrayList<Float> current_dataList;//一段数据
    private ArrayList<Date> current_NotExceptions_Date;//一段数据的日期
    private ArrayList<Float> current_NotExceptions_Data;//一段数据

    private float current_data_Mean;//平均值
    private float current_Change_Rate;//数据变化率
    private Date current_time;//当前时间
    private double current_score_final;//测点最终评分

    public MeasurePoint( String fix_devicename,//0主机设备（DEV_NAME）
                  String fix_deviceNo,//1主机设备编号（DEV_NO ）
                  String fix_union_tagname,//3位号说明编号（ DEV_TAG_NO）
                  String fix_tagnname,//4位号	           DEV_TAG
                  String fix_ch_comment,//2位号说明	   DEV_TAGCH
                  List<String> fix_de_te_el,//设备|工艺|电气	   DE_TE_EL
                  float fix_hhi_lim,//高高限	           HHI_LIM
                  float fix_hign_lim,//高限	           HIGH_LIM
                  float fix_llo_lim,//低低限	           LLO_LIM
                  float fix_low_lim,//低限	           LOW_LIM
                  float fix_change_rate,//变化率	           CHA_RATE
                  boolean fix_is_audio,//是否语音报警	   IS_AUDIO
                  float fix_alarm_level,//报警等级	   ALM_LEV
                  String fix_process,//工序	           DEV_PRO
                  boolean fix_is_push,//是否推送	   IS_PUSH
                  float fix_healthvalue,//健康值	           HEA_VALUE
                  boolean fix_is_main,
                  String fix_PRL_ID,
                  String fix_PRL_NAME,
                  boolean fix_IS_SCORE
                  ){
        this.fix_devicename=fix_devicename;//0主机设备（DEV_NAME）
        this.fix_deviceNo=fix_deviceNo;//1主机设备编号（DEV_NO ）
        this.fix_union_tagname=fix_union_tagname;//3位号说明编号（ DEV_TAG_NO）
        this.fix_tagname =fix_tagnname;//位号	           DEV_TAG
        this.fix_ch_comment=fix_ch_comment;//位号说明	   DEV_TAGCH
        this.fix_de_te_el=fix_de_te_el;//设备|工艺|电气	   DE_TE_EL
        this.fix_hhi_lim=fix_hhi_lim;//高高限	           HHI_LIM
        this.fix_hign_lim=fix_hign_lim;//高限	           HIGH_LIM
        this.fix_llo_lim=fix_llo_lim;//低低限	           LLO_LIM
        this.fix_low_lim=fix_low_lim;//低限	           LOW_LIM
        this.fix_change_rate=fix_change_rate;//变化率	           CHA_RATE
        this.fix_is_audio=fix_is_audio;//是否语音报警	   IS_AUDIO
        this.fix_alarm_level=fix_alarm_level;//报警等级	   ALM_LEV
        this.fix_process=fix_process;//工序	           DEV_PRO
        this.fix_is_push=fix_is_push;//是否推送	   IS_PUSH
        this.fix_healthvalue=fix_healthvalue;//健康值	           HEA_VALUE
        this.fix_is_main=fix_is_main;
        this.fix_PRL_ID=fix_PRL_ID;
        this.fix_PRL_NAME=fix_PRL_NAME;
        this.fix_IS_SCORE=fix_IS_SCORE;
        current_dateList=new ArrayList<Date>();
        current_dataList=new ArrayList<Float>();
        current_NotExceptions_Data=new ArrayList<Float>();
        current_NotExceptions_Date=new ArrayList<Date>();

    }


/**
 *
 *
 * @param Current_value
 * @param Current_time

 * @param Current_dataList
 * @param Current_dateList
// * @param Tagname
 * */
    public MeasurePoint(ArrayList<Date> Current_dateList,
                        ArrayList<Float> Current_dataList,
                        float Current_Data_Mean,
                        float Current_Change_Rate,
                        Date  Current_time,
                        float Current_value,
                        double current_score_final,
                        ArrayList<Float> current_NotExceptions_Data,
                        ArrayList<Date> current_NotExceptions_Date,

                        String fix_devicename,//0主机设备（DEV_NAME）
                        String fix_deviceNo,//1主机设备编号（DEV_NO ）
                        String fix_union_tagname,//3位号说明编号（ DEV_TAG_NO）
                        String fix_tagnname,//4位号	           DEV_TAG
                        String fix_ch_comment,//2位号说明	   DEV_TAGCH
                        List<String> fix_de_te_el,//设备|工艺|电气	   DE_TE_EL
                        float fix_hhi_lim,//高高限	           HHI_LIM
                        float fix_hign_lim,//高限	           HIGH_LIM
                        float fix_llo_lim,//低低限	           LLO_LIM
                        float fix_low_lim,//低限	           LOW_LIM
                        float fix_change_rate,//变化率	           CHA_RATE
                        boolean fix_is_audio,//是否语音报警	   IS_AUDIO
                        float fix_alarm_level,//报警等级	   ALM_LEV
                        String fix_process,//工序	           DEV_PRO
                        boolean fix_is_push,//是否推送	   IS_PUSH
                        float fix_healthvalue,//健康值	           HEA_VALUE
                        boolean fix_is_main,
                        float score_base,
                        String fix_PRL_ID,
                        String fix_PRL_NAME,
                        boolean fix_IS_SCORE){

        this.current_dateList=Current_dateList;
        this.current_dataList=Current_dataList;
        this.current_data_Mean=Current_Data_Mean;
        this.current_Change_Rate=Current_Change_Rate;
        this.current_value=Current_value;
        this.current_score_final=current_score_final;
        this.current_time=Current_time;
        this.current_NotExceptions_Data=current_NotExceptions_Data;
        this.current_NotExceptions_Date=current_NotExceptions_Date;

        this.fix_devicename=fix_devicename;//0主机设备（DEV_NAME）
        this.fix_deviceNo=fix_deviceNo;//1主机设备编号（DEV_NO ）
        this.fix_union_tagname=fix_union_tagname;//3位号说明编号（ DEV_TAG_NO）
        this.fix_tagname =fix_tagnname;//4位号	           DEV_TAG
        this.fix_ch_comment=fix_ch_comment;//2位号说明	   DEV_TAGCH
        this.fix_de_te_el=fix_de_te_el;//设备|工艺|电气	   DE_TE_EL
        this.fix_hhi_lim=fix_hhi_lim;//高高限	           HHI_LIM
        this.fix_hign_lim=fix_hign_lim;//高限	           HIGH_LIM
        this.fix_llo_lim=fix_llo_lim;//低低限	           LLO_LIM
        this.fix_low_lim=fix_low_lim;//低限	           LOW_LIM
        this.fix_change_rate=fix_change_rate;//变化率	           CHA_RATE
        this.fix_is_audio=fix_is_audio;//是否语音报警	   IS_AUDIO
        this.fix_alarm_level=fix_alarm_level;//报警等级	   ALM_LEV
        this.fix_process=fix_process;//工序	           DEV_PRO
        this.fix_is_push=fix_is_push;//是否推送	   IS_PUSH
        this.fix_healthvalue=fix_healthvalue;//健康值	           HEA_VALUE
        this.fix_is_main=fix_is_main;
        this. score_base=score_base;
        this.fix_PRL_ID=fix_PRL_ID;
        this.fix_PRL_NAME=fix_PRL_NAME;
        this.fix_IS_SCORE=fix_IS_SCORE;

    }


/**
 *
 *
 * @paramcompute the point score
 */


    public final void  ComputeScore(){

        if(this.fix_IS_SCORE){
            if(current_value <fix_healthvalue){

                current_score_final =round(score_base);
            }else if((current_value >=fix_healthvalue)&&(current_value <fix_hign_lim)){
                double cov=1.0-(current_value -fix_healthvalue)/(fix_hign_lim-fix_healthvalue);
                current_score_final =round(cov*score_base);
            }else if(current_value >fix_hign_lim){
                current_score_final =round(-score_base);
            }

        }




    }



    public void add_Date(Date date){
        current_dateList.add(date);
    }

    public void add_Data(Float data){
        current_dataList.add(data);

    }

    public void clear_dateList(){
        current_dateList.clear();
    }

    public void clear_dataList(){
        current_dataList.clear();
    }

/**
 *
 * the  function for filt the strange data point
 * */
    public  void Filter_Data(){

        //mean
        double Sum=0d;
        int sz= current_dataList.size();

        double D_mean=Requir_Mean(current_dataList);

        //std
        Sum=0d;
        for(int i = 0;i < sz;i++){
            double unit=(double) current_dataList.get(i);
            Sum+=Math.pow(( unit-D_mean),2);
        }
        double D_std=Math.sqrt(Sum/(sz - 1));

        double Cut_three_Meg=D_mean-3*D_std;
        double Plus_three_Meg=D_mean+3*D_std;
        //Filter

        current_NotExceptions_Data.clear();
        current_NotExceptions_Date.clear();

        //find the elements who aren't exception
        for(int i=0;i<sz;i++){

            if((current_dataList.get(i)>=Cut_three_Meg) && (current_dataList.get(i)<=Plus_three_Meg)){
                current_NotExceptions_Data.add(current_dataList.get(i));
                current_NotExceptions_Date.add(current_dateList.get(i));
            }
        }

        if(!current_NotExceptions_Data.isEmpty()){

            DecimalFormat df = new DecimalFormat(".00");

//            setCurrent_data_Mean((float)Requir_Mean(current_NotExceptions_Data));
            this.current_data_Mean=Float.valueOf(df.format(Requir_Mean(current_NotExceptions_Data)));
            this.current_Change_Rate=current_NotExceptions_Data.get(0)-current_NotExceptions_Data.get(current_NotExceptions_Data.size()-1);//alarm

            current_Change_Rate=Float.valueOf(df.format(current_Change_Rate));
            current_value= current_NotExceptions_Data.get(0);
            current_time= current_NotExceptions_Date.get(0);

        }



    }

    public void Clear_UPdataList(){
        clear_dateList();
        clear_dataList();
//        getCurrent_dateList().clear();
    }

    private double Requir_Mean(List<Float> datalist){

        double Sum=0d;
        int sz=datalist.size();
        for(int i=0;i<sz;i++){
            Sum+=(double)datalist.get(i);
        }
        return Sum/sz;

    }



//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//
//        ArrayList<Date> temp_Date=new ArrayList<Date>();
//        ArrayList<Float> temp_Data=new ArrayList<Float>();
//
//        ArrayList<Date> temp_NoExDate=new ArrayList<Date>();
//        ArrayList<Float> temp_NoExData=new ArrayList<Float>();
//
//
//        List<String> temp_fix_de_te_el=new ArrayList<String>();
//        Iterator<Date> dateiter= current_dateList.iterator();
//        Iterator<Float> dataiter= current_dataList.iterator();
//        while(dateiter.hasNext()){
//            temp_Date.add((Date)dateiter.next().clone());
//            temp_Data.add(dataiter.next());
//        }
//        for(String s:fix_de_te_el){
//            temp_fix_de_te_el.add(s);
//        }
//
//        for(Date date:this.current_NotExceptions_Date){
//            temp_NoExDate.add(date);
//        }
//        for(float data:this.current_NotExceptions_Data){
//            temp_NoExData.add(data);
//        }
//
//
//        return new MeasurePoint(temp_Date,temp_Data,current_data_Mean,current_Change_Rate,(Date)current_time.clone(),current_value,current_score_final,temp_NoExData,temp_NoExDate,fix_devicename,fix_deviceNo,fix_union_tagname,fix_tagname,fix_ch_comment,temp_fix_de_te_el,fix_hhi_lim,fix_hign_lim,fix_llo_lim,fix_low_lim,fix_change_rate,fix_is_audio,fix_alarm_level,fix_process,fix_is_push,fix_healthvalue,fix_is_main,score_base,fix_PRL_ID,fix_PRL_NAME,fix_IS_SCORE);//temp_Date,temp_Data,Data_Mean,Change_Rate,Tagname,(Date)Current_time.clone(),Current_value);
//    }

    public ArrayList<Date> getCurrent_dateList() {
        return current_dateList;
    }

    public ArrayList<Float> getCurrent_dataList() {
        return current_dataList;
    }








    @Override
    public String toString() {
        return   ""+this.current_dateList+
        this.current_dataList+
        this.current_data_Mean+
        this.current_Change_Rate+
        this.current_value+
        this.current_score_final+
        this.current_time+
        this.fix_devicename+
        this.fix_deviceNo+
        this.fix_union_tagname+
        this.fix_tagname+
        this.fix_ch_comment+
        this.fix_de_te_el+
        this.fix_hhi_lim+
        this.fix_hign_lim+
        this.fix_llo_lim+
        this.fix_low_lim+
        this.fix_change_rate+
        this.fix_is_audio+
        this.fix_alarm_level+
        this.fix_process+
        this.fix_is_push+
        this.fix_healthvalue+
        this.fix_is_main+
        this. score_base+"\n";

    }

    private double round(double f){

        BigDecimal bg = new BigDecimal(f);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public String getFix_devicename() {
        return fix_devicename;
    }

    public void setFix_devicename(String fix_devicename) {
        this.fix_devicename = fix_devicename;
    }

    public String getFix_deviceNo() {
        return fix_deviceNo;
    }

    public void setFix_deviceNo(String fix_deviceNo) {
        this.fix_deviceNo = fix_deviceNo;
    }

    public String getFix_union_tagname() {
        return fix_union_tagname;
    }

    public void setFix_union_tagname(String fix_union_tagname) {
        this.fix_union_tagname = fix_union_tagname;
    }

    public String getFix_tagname() {
        return fix_tagname;
    }

    public void setFix_tagname(String fix_tagname) {
        this.fix_tagname = fix_tagname;
    }

    public String getFix_ch_comment() {
        return fix_ch_comment;
    }

    public void setFix_ch_comment(String fix_ch_comment) {
        this.fix_ch_comment = fix_ch_comment;
    }

    public List<String> getFix_de_te_el() {
        return fix_de_te_el;
    }

    public void setFix_de_te_el(List<String> fix_de_te_el) {
        this.fix_de_te_el = fix_de_te_el;
    }

    public float getFix_hhi_lim() {
        return fix_hhi_lim;
    }

    public void setFix_hhi_lim(float fix_hhi_lim) {
        this.fix_hhi_lim = fix_hhi_lim;
    }

    public float getFix_hign_lim() {
        return fix_hign_lim;
    }

    public void setFix_hign_lim(float fix_hign_lim) {
        this.fix_hign_lim = fix_hign_lim;
    }

    public float getFix_llo_lim() {
        return fix_llo_lim;
    }

    public void setFix_llo_lim(float fix_llo_lim) {
        this.fix_llo_lim = fix_llo_lim;
    }

    public float getFix_low_lim() {
        return fix_low_lim;
    }

    public void setFix_low_lim(float fix_low_lim) {
        this.fix_low_lim = fix_low_lim;
    }

    public float getFix_change_rate() {
        return fix_change_rate;
    }

    public void setFix_change_rate(float fix_change_rate) {
        this.fix_change_rate = fix_change_rate;
    }

    public boolean isFix_is_audio() {
        return fix_is_audio;
    }

    public void setFix_is_audio(boolean fix_is_audio) {
        this.fix_is_audio = fix_is_audio;
    }

    public float getFix_alarm_level() {
        return fix_alarm_level;
    }

    public void setFix_alarm_level(int fix_alarm_level) {
        this.fix_alarm_level = fix_alarm_level;
    }

    public String getFix_process() {
        return fix_process;
    }

    public void setFix_process(String fix_process) {
        this.fix_process = fix_process;
    }

    public boolean isFix_is_push() {
        return fix_is_push;
    }

    public void setFix_is_push(boolean fix_is_push) {
        this.fix_is_push = fix_is_push;
    }

    public double getFix_healthvalue() {
        return fix_healthvalue;
    }

    public void setFix_healthvalue(float fix_healthvalue) {
        this.fix_healthvalue = fix_healthvalue;
    }

    public boolean isFix_is_main() {
        return fix_is_main;
    }

    public void setFix_is_main(boolean fix_is_main) {
        this.fix_is_main = fix_is_main;
    }




    public double getScore_base() {
        return score_base;
    }

    public void setScore_base(float score_base) {
        this.score_base = score_base;
    }

    public ArrayList<Date> getDateList() {
        return current_dateList;
    }

    public void setCurrent_dateList(ArrayList<Date> current_dateList) {
        this.current_dateList = current_dateList;
    }

    public ArrayList<Float> getDataList() {
        return current_dataList;
    }

    public void setCurrent_dataList(ArrayList<Float> current_dataList) {
        this.current_dataList = current_dataList;
    }

    public float getCurrent_data_Mean() {
        return current_data_Mean;
    }

    public void setCurrent_data_Mean(float current_data_Mean) {
        this.current_data_Mean = current_data_Mean;
    }

    public float getCurrent_Change_Rate() {
        return current_Change_Rate;
    }

    public void setCurrent_Change_Rate(float current_Change_Rate) {
        this.current_Change_Rate = current_Change_Rate;
    }

    public Date getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(Date current_time) {
        this.current_time = current_time;
    }

    public float getCurrent_value() {
        return current_value;
    }

    public void setCurrent_value(float current_value) {
        this.current_value = current_value;
    }

    public double getCurrent_score_final() {
        return current_score_final;
    }

    public void setCurrent_score_final(double current_score_final) {
        this.current_score_final = current_score_final;
    }

    public void setFix_alarm_level(float fix_alarm_level) {
        this.fix_alarm_level = fix_alarm_level;
    }

    public String getFix_PRL_ID() {
        return fix_PRL_ID;
    }

    public void setFix_PRL_ID(String fix_PRL_ID) {
        this.fix_PRL_ID = fix_PRL_ID;
    }

    public String getFix_PRL_NAME() {
        return fix_PRL_NAME;
    }

    public void setFix_PRL_NAME(String fix_PRL_NAME) {
        this.fix_PRL_NAME = fix_PRL_NAME;
    }

    public int getCurrentAlarmType() {
        return currentAlarmType;
    }

    public void setCurrentAlarmType(int currentAlarmType) {
        this.currentAlarmType = currentAlarmType;
    }
}
