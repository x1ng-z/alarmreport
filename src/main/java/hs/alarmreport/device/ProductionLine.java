package hs.alarmreport.device;

import java.util.HashMap;
import java.util.Map;

public class ProductionLine {
   private String name;
   private String pNo;
   private Map<String,   Device> devices=null;
   private Map<String,MeasurePoint> totalmeasure_Uniontag;

    public ProductionLine(String name,String pNo){
        this.name=name;
        this.pNo=pNo;
        devices=new HashMap<String,   Device>();
        totalmeasure_Uniontag=new HashMap<String,MeasurePoint>();
    }



    public void addDevice(String deviceno,   Device device){

        devices.put(deviceno,device);

    }

    public  void reomveDevice(String deviceno){

        devices.remove(deviceno);
    }

    public String getName() {
        return name;
    }

    public String getpNo() {
        return pNo;
    }

    public Map<String,   Device> getDevices() {
        return devices;
    }


    public Map<String, MeasurePoint> getTotalmeasure_Uniontag() {
        return totalmeasure_Uniontag;
    }

    public void init_totalmeasure_Uniontag(){

        for(  Device device:getDevices().values()){
            totalmeasure_Uniontag.putAll(device.getAuxiliary_point_Uniontag());
            totalmeasure_Uniontag.putAll(device.getMain_point_Uniontag());
            totalmeasure_Uniontag.putAll(device.getNo_ScorePoint_Uniontag());
        }




    }


}
