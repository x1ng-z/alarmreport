package hs.alarmreport.resourceparse;

public interface BaseResource {
//    void Save_Data();
    void Find();
    /**
     *
     * @param attr CHA_RATE/ALM_LEV/IS_MAIN/LLO_LIM....
     * @param value true 3.0....
     * */
    void Update(String CompanyNo, String DEV_NO, String DEV_TAG_NO, String attr, String value);
    void Delete();


}
