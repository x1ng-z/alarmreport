package hs.alarmreport.opc;

import hs.alarmreport.device.MeasurePoint;
import org.openscada.opc.lib.common.ConnectionInformation;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/10/31 10:57
 */
public interface Service {
    void init();
    void connect();
    void reconnect();
    void registerItem(List<MeasurePoint> tagname);
    void disconnect();
}
