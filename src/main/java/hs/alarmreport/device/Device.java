package hs.alarmreport.device;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public  class Device {
    private Pattern pattern=Pattern.compile(".*电流$");

    //need to compute score
    public Map<String, MeasurePoint> main_point_Uniontag;//主要点 key=DEV_TAG_NO(点号别名)
    public Map<String, MeasurePoint> main_point_DBtag;//key=DEV_TAG(opc点号)
    public Map<String, MeasurePoint> auxiliary_point_Uniontag;//次要点key=DEV_TAG_NO(点号别名)
    public  Map<String, MeasurePoint> auxiliary_point_DBtag;//key=DEV_TAG(opc点号)

    //don't compute score

    public Map<String,MeasurePoint> no_ScorePoint_Uniontag;//key=DEV_TAG_NO(点号别名)
    public Map<String,MeasurePoint> no_ScorePoint_DBtag;//key=DEV_TAG(opc点号)


    private  final  String DEV_NO;
    private final String DEV_NAME;
    private double Tatol_score=0;


    public Device(String DEV_NO,String DEV_NAME) {
        this.DEV_NO=DEV_NO;
        this.DEV_NAME=DEV_NAME;
        this.main_point_Uniontag=new HashMap<String, MeasurePoint>();
        this. main_point_DBtag=new HashMap<String, MeasurePoint>();
        this.auxiliary_point_Uniontag=new HashMap<String, MeasurePoint> ();
        this.auxiliary_point_DBtag=new  HashMap<String, MeasurePoint> ();
        this.no_ScorePoint_DBtag=new HashMap<String, MeasurePoint> ();
        this.no_ScorePoint_Uniontag=new HashMap<String, MeasurePoint> ();

    }

    public void  addmain_point_DBtag(String DBtag,MeasurePoint measurePoint) {
        main_point_DBtag.put(DBtag,measurePoint);
    }
    public void  addMain_point_Uniontag(String uniontag,MeasurePoint measurePoint) {
        main_point_Uniontag.put(uniontag,measurePoint);
    }


    public void  addauxiliary_point_Uniontag(String uniontag,MeasurePoint measurePoint) {
        auxiliary_point_Uniontag.put(uniontag,measurePoint);
    }
    public void  addauxiliary_point_DBtag(String DBtag,MeasurePoint measurePoint) {
        auxiliary_point_DBtag.put(DBtag,measurePoint);
    }


    public void  addno_ScorePoint_Uniontag(String uniontag,MeasurePoint measurePoint) {
        no_ScorePoint_Uniontag.put(uniontag,measurePoint);
    }
    public void  addno_ScorePoint_DBtag(String DBtag,MeasurePoint measurePoint) {
        no_ScorePoint_DBtag.put(DBtag,measurePoint);
    }







    public   void  Compute_Score() {
        double sum=0;
        ArrayList<MeasurePoint> vercey=new ArrayList<MeasurePoint>();

        ArrayList<MeasurePoint> Allin=new ArrayList<MeasurePoint>();
        Allin.addAll(main_point_DBtag.values());
        Allin.addAll(auxiliary_point_DBtag.values());

        for( MeasurePoint measurePoint:Allin){
            String fix_ch_comment=measurePoint.getFix_ch_comment();
            Matcher matcher=pattern.matcher(fix_ch_comment);
            while(matcher.find()){
                vercey.add(measurePoint);
            }

        }

        int total_Verifycode=vercey.size();
        int sum_Verifycode=0;
            for(MeasurePoint measurePoint:vercey){
                if(measurePoint.getCurrent_value()>=1){
                    ++sum_Verifycode;
                }
            }

        if(sum_Verifycode==total_Verifycode){
            for( MeasurePoint measurePoint:main_point_DBtag.values()){
                measurePoint.ComputeScore();
                sum+=measurePoint.getCurrent_score_final();
            }
            for(MeasurePoint measurePoint:auxiliary_point_DBtag.values()){
                measurePoint.ComputeScore();
                sum+=measurePoint.getCurrent_score_final();
            }
        }else {

            for( MeasurePoint measurePoint:main_point_DBtag.values()){
//                measurePoint.ComputeScore();
                measurePoint.setCurrent_score_final(0.0d);
            }
            for(MeasurePoint measurePoint:auxiliary_point_DBtag.values()){
                measurePoint.setCurrent_score_final(0.0d);
            }


        }

        if(sum>100){
                sum=100;
        }

        Tatol_score=sum;

    }





    @Override
    public String toString() {
        return main_point_Uniontag.toString()+auxiliary_point_Uniontag.toString();
    }

    public void setMeasurePoint_ScoreBase(){

        int count_tag = 2 * main_point_Uniontag.size() + auxiliary_point_DBtag.size();
        for(MeasurePoint measurePoint:main_point_DBtag.values()){
            measurePoint.setScore_base( round(2.0f * 100.0f / count_tag));
//            System.out.println("sss"+round(2.0f * 100.0f / count_tag));
        }
        for(MeasurePoint measurePoint:auxiliary_point_DBtag.values()){
            measurePoint.setScore_base(round( 1.0f * 100.0f / count_tag));
//            System.out.println("sss"+round(1.0f * 100.0f / count_tag));

        }

    }


    private float round(float f){

        BigDecimal bg = new BigDecimal(f);
        return bg.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }







    //protected abstract void init();


    public Map<String, MeasurePoint> getMain_point_Uniontag() {
        return main_point_Uniontag;
    }

    public void setMain_point_Uniontag(Map<String, MeasurePoint> main_point_Uniontag) {
        this.main_point_Uniontag = main_point_Uniontag;
    }

    public Map<String, MeasurePoint> getMain_point_DBtag() {
        return main_point_DBtag;
    }

    public void setMain_point_DBtag(Map<String, MeasurePoint> main_point_DBtag) {
        this.main_point_DBtag = main_point_DBtag;
    }

    public Map<String, MeasurePoint> getAuxiliary_point_Uniontag() {
        return auxiliary_point_Uniontag;
    }

    public void setAuxiliary_point_Uniontag(Map<String, MeasurePoint> auxiliary_point_Uniontag) {
        this.auxiliary_point_Uniontag = auxiliary_point_Uniontag;
    }

    public Map<String, MeasurePoint> getAuxiliary_point_DBtag() {
        return auxiliary_point_DBtag;
    }

    public void setAuxiliary_point_DBtag(Map<String, MeasurePoint> auxiliary_point_DBtag) {
        this.auxiliary_point_DBtag = auxiliary_point_DBtag;
    }

    public String getDEV_NO() {
        return DEV_NO;
    }

    public String getDEV_NAME() {
        return DEV_NAME;
    }

    public double getTatol_score() {
        return Tatol_score;
    }

    public void setTatol_score(double tatol_score) {
        Tatol_score = tatol_score;
    }

    public Map<String, MeasurePoint> getNo_ScorePoint_Uniontag() {
        return no_ScorePoint_Uniontag;
    }

    public void setNo_ScorePoint_Uniontag(Map<String, MeasurePoint> no_ScorePoint_Uniontag) {
        this.no_ScorePoint_Uniontag = no_ScorePoint_Uniontag;
    }

    public Map<String, MeasurePoint> getNo_ScorePoint_DBtag() {
        return no_ScorePoint_DBtag;
    }

    public void setNo_ScorePoint_DBtag(Map<String, MeasurePoint> no_ScorePoint_DBtag) {
        this.no_ScorePoint_DBtag = no_ScorePoint_DBtag;
    }
}
