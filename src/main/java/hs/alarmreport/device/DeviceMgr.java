package hs.alarmreport.device;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.alarmreport.opc.OpcConnect;
import hs.alarmreport.resourceparse.Base4Xml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DeviceMgr {
    private Logger logger = LoggerFactory.getLogger(DeviceMgr.class);
    private static DeviceMgr deviceMgr=null;

    private List<ProductionLine> productionLines;


    public void setOpcService(OpcConnect opcService) {
        this.opcService = opcService;
    }

    private OpcConnect opcService;


// for real data flush simple
    private Map<String,MeasurePoint>Total_DBtag=new HashMap<String,MeasurePoint>();//key=DEV_TAG(点号别名)
    private Map<String,MeasurePoint>Total_Uniontag=new HashMap<String,MeasurePoint>();//key=DEV_TAG_NO(点号别名)


    @Autowired
    public DeviceMgr(Base4Xml base4Xml,OpcConnect opcService){
        logger.info("init DeviceMgr");
        this.opcService=opcService;
        productionLines=base4Xml.getPCollects();
        for(ProductionLine productionLine:productionLines){
            for(Device device:productionLine.getDevices().values()){
                Total_DBtag.putAll(device.getMain_point_DBtag());
                Total_DBtag.putAll(device.getAuxiliary_point_DBtag());
                Total_DBtag.putAll(device.getNo_ScorePoint_DBtag());

                Total_Uniontag.putAll(device.getMain_point_Uniontag());
                Total_Uniontag.putAll(device.getAuxiliary_point_Uniontag());
                Total_Uniontag.putAll(device.getNo_ScorePoint_Uniontag());

            }

            productionLine.init_totalmeasure_Uniontag();

            List<MeasurePoint> tempmeasure=new ArrayList<>();
            tempmeasure.addAll(productionLine.getTotalmeasure_Uniontag().values());
            opcService.registerItem(tempmeasure);
        }
    }
    /**
     *  apcche service get data
     *
     * */

    public  synchronized void Update_MeasurePoint_RealData(JSONArray valueArray){

        for (int i = 0; i < valueArray.size(); i++) {
            String itemStr = valueArray.getJSONObject(i).toJSONString();
            JSONObject itemObject = JSONObject.parseObject(itemStr);
            String String_date = itemObject.getString("time");
            try {
                //System.out.println(String_date);
                Matcher m=Pattern.compile("\\d.*\\.").matcher(String_date);
                String new_String_date=null;
                if(m.find()&&(m.start()==0)){
                    new_String_date=m.group().replace(".","Z");
                    // System.out.println(new_String_date);
                }

                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(new_String_date);
                date=new Date(date.getTime()+(long)8 * 60 * 60 * 1000);
                UpRealdata(itemObject,date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     *
     * @param deviceName
     * @return
     * */
    public synchronized Map<String,MeasurePoint>getMeasurePointByDevicename(String PLNo,String deviceName){

        for(ProductionLine productionLine:productionLines){
            if(productionLine.getpNo().equals(PLNo)){
               Device device= productionLine.getDevices().get(deviceName);

                Map<String,MeasurePoint> deviceMeasure=new HashMap<String,MeasurePoint>();

                deviceMeasure.putAll(device.getAuxiliary_point_DBtag());
                deviceMeasure.putAll(device.getMain_point_DBtag());
                return deviceMeasure;

            }


        }

        return null;

    }


    public synchronized Device getDeviceByDevicename(String PLNo,String deviceName){

        for(ProductionLine productionLine:productionLines) {

            if(productionLine.getpNo().equals(PLNo)) {
                Device device = productionLine.getDevices().get(deviceName);
                return device;
            }

        }

        return null;


    }


    public synchronized double getDeviceScoreBydevicename(String PLNo,String deviceName){

        Device device=getDeviceByDevicename(PLNo,deviceName);
        return device.getTatol_score();
    }


    public  synchronized List<MeasurePoint> getMeasurePonitByPL(String PLNo,String[] uniontags){

        List<MeasurePoint> list=new ArrayList<MeasurePoint>();
        for(ProductionLine productionLine:productionLines){
            if(productionLine.getpNo().equals(PLNo)){
//                System.out.println("PL true");
                Map<String, MeasurePoint> collections=productionLine.getTotalmeasure_Uniontag();
//                System.out.println("PL true"+collections.toString());
                for(String uniontag:uniontags){
                    for(MeasurePoint measurePoint:collections.values()){
                        if(measurePoint.getFix_union_tagname().equals(uniontag)){
                            list.add(measurePoint);
                        }
                    }

                }

            }

        }
        return list;


    }



    public synchronized  List<MeasurePoint> getPLMeasurePointByPLNo(String PLNo){

        List<MeasurePoint> list=new ArrayList<MeasurePoint>();

        for(ProductionLine productionLine:productionLines){
            if(productionLine.getpNo().equals(PLNo)){
                Collection<Device> collections=productionLine.getDevices().values();
                for(Device device:collections){
                    list.addAll(device.getMain_point_DBtag().values());
                    list.addAll(device.getAuxiliary_point_DBtag().values());
                }


            }

        }
        return list;

    }





    public synchronized void ComputScore(){

        for(ProductionLine productionLine:productionLines){

            for(Device device:productionLine.getDevices().values()){

                device.Compute_Score();
            }
        }

    }



    public synchronized void Clearup_RealDataList(){
        Set<String> total_dbtag=Total_DBtag.keySet();

        for(String dbtag:total_dbtag) {
            Total_DBtag.get(dbtag).Clear_UPdataList();
        }


    }



    private void UpRealdata(JSONObject jsonObject, Date date){
        Set<String> total_dbtag=Total_DBtag.keySet();
        for(String dbtag:total_dbtag){
            try {
                Total_DBtag.get(dbtag).add_Data( jsonObject.getFloat(dbtag));
                Total_DBtag.get(dbtag).add_Date((Date)date.clone());
            }catch (Exception e){
                Total_DBtag.get(dbtag).add_Data( 0f);
                Total_DBtag.get(dbtag).add_Date((Date)date.clone());
            }
        }


    }

    private  void UpRealdata(Float data,Date date,String tagname){

        Total_DBtag.get(tagname).add_Date(date);
        Total_DBtag.get(tagname).add_Data(data);

    }



    public synchronized void MeasurePoint_Filter(){
        for(MeasurePoint measurePoint:Total_DBtag.values()){
            measurePoint.Filter_Data();
        }

    }

    /***
     *
     * @return device(owner) MeasurePoint Dbtag
     */
   public Map<String, MeasurePoint>  quickSearch4DEV_NAME_DB(String PRL_ID,String DEV_NO){
       for(ProductionLine productionLine:productionLines){

          if(productionLine.getpNo().equals(PRL_ID)){
              Device device=productionLine.getDevices().get(DEV_NO);
              Map<String, MeasurePoint> Auxiliary_point_DBtag=device.getAuxiliary_point_DBtag();
              Map<String, MeasurePoint> Main_point_DBtag=device.getMain_point_DBtag();
              Map<String, MeasurePoint> DBtag=new HashMap<String, MeasurePoint>();
              DBtag.putAll(Auxiliary_point_DBtag);
              DBtag.putAll(Main_point_DBtag);
              return DBtag;


          }

       }

      return null;
    }



    /***
     *
     * @return device(owner) MeasurePoint Uniontag
     */
    public Map<String, MeasurePoint>  quickSearch4DEV_TAG_NO_Un(String PRL_ID,String DEV_NO){


        for(ProductionLine productionLine:productionLines){

            if(productionLine.getpNo().equals(PRL_ID)){
                Device device=productionLine.getDevices().get(DEV_NO);
                Map<String, MeasurePoint> Auxiliary_point_Uniontag=device.getAuxiliary_point_Uniontag();
                Map<String, MeasurePoint> Main_point_Uniontag=device.getMain_point_Uniontag();
                Map<String, MeasurePoint> Uniontag=new HashMap<String, MeasurePoint>();
                Uniontag.putAll(Auxiliary_point_Uniontag);
                Uniontag.putAll(Main_point_Uniontag);
                return Uniontag;


            }

        }

        return null;

    }


    public synchronized Map<String, MeasurePoint> getTotal_DBtag() {
        return Total_DBtag;
    }

    public synchronized Map<String, MeasurePoint> getTotal_Uniontag() {
        return Total_Uniontag;
    }

    public synchronized List<ProductionLine> getProductionLines() {
        return productionLines;
    }
}
