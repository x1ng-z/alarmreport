package hs.alarmreport.opc;

import com.sun.jna.Memory;
import hs.alarmreport.device.MeasurePoint;
import opc.item.ItemManger;
import opc.item.ItemUnit;
import opc.serve.OPCServe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/11/25 16:10
 */
@Component
public class OpcConnect implements Runnable {
    private Logger logger = LoggerFactory.getLogger(OpcConnect.class);

    private String opcservename;
    private String opcip;
    private Map<String, List<MeasurePoint>> registeredMeasurePoint = new ConcurrentHashMap();
    private Thread thread;
    private List<MeasurePoint> waittoregistertag = new CopyOnWriteArrayList<>();

    private OPCServe opcServe;

    public synchronized void addtag_offline(MeasurePoint m) {
        waittoregistertag.add(m);
    }


    public void reconnect() {
        opcServe.disconnect();
        opcServe.connect();
        if (opcServe.isConectstatus()) {

            for (String tag : registeredMeasurePoint.keySet()) {
                registerItem2(tag);
            }
            synchronized (waittoregistertag) {
                registerItem(waittoregistertag);
            }
        }
        logger.info("ip="+opcip +" servename="+ opcservename);

    }


    public OpcConnect(@Value("${opc.servename}") String opcservename, @Value("${opc.ip}") String opcip) {
        this.opcservename = opcservename;
        this.opcip = opcip;
        this.opcServe = new OPCServe(opcip, opcservename);
        opcServe.connect();


//        List<MeasurePoint> list=new ArrayList<>();
//        MeasurePoint a1=new MeasurePoint();
//        a1.setFix_tagname("dcs.User.ff1");
//        list.add(a1);
//
//        MeasurePoint a2=new MeasurePoint();
//        a2.setFix_tagname("dcs.User.ff2");
//        list.add(a2);
//
//        MeasurePoint a3=new MeasurePoint();
//        a3.setFix_tagname("dcs.User.ff3");
//        list.add(a3);
//        registerItem(list);
//        if(opcclient!=null){
//            if(OTT.INSTANTCE.ADDITEM(opcclient,"dcs.User.ff1")==0){
//                System.out.println("failed");
//            }
//            if(OTT.INSTANTCE.ADDITEM(opcclient,"dcs.User.ff2")==0){
//                System.out.println("failed");
//            }
//
//            if(OTT.INSTANTCE.ADDITEM(opcclient,"dcs.User.ff3")==0){
//                System.out.println("failed");
//            }
//        }

        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();

    }


    @Override
    public void run() {

        Integer writevloop = 0;
        while (!Thread.currentThread().isInterrupted()) {

            if (!opcServe.isConectstatus()) {
                logger.info("reconnect opc server");
                reconnect();
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
                continue;
            }

            //数据读取统计数据读取
            long startgetdate = System.currentTimeMillis();
            if (registeredMeasurePoint.size() > 0) {
                readAndProcessDataByOnce();
            }
            long endgetdate = System.currentTimeMillis();
            long spendtime = endgetdate - startgetdate;
            logger.info("所有数据处理耗时=" + spendtime + "ms");

            try {
                TimeUnit.MILLISECONDS.sleep((1000 - spendtime) > 0 ? (1000 - spendtime) : 0);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }

        }
    }


    public synchronized void readAndProcessDataByOnce() {

        if (opcServe.readAllItem()) {
            for (ItemUnit itemUnit : opcServe.getItemManger().getTagOrderList()) {
                List<MeasurePoint> list = registeredMeasurePoint.get(itemUnit.getItem());
                if (list != null) {
                    for (MeasurePoint measurePoint : list) {
                        measurePoint.setCurrent_value(itemUnit.getValue());
                        measurePoint.setCurrent_time(new Date());
                        logger.info(measurePoint.getFix_tagname() + " value =" + itemUnit.getValue());
                    }
                }
            }
        }

    }


    public synchronized void removeItem(List<MeasurePoint> tagname) {
        for (MeasurePoint measurePoint : tagname) {
            if (registeredMeasurePoint.get(measurePoint.getFix_tagname()) != null) {
                List<MeasurePoint> chainmeasure = registeredMeasurePoint.get(measurePoint.getFix_tagname());
                chainmeasure.remove(measurePoint);
                if(opcServe.removeItem(measurePoint.getFix_tagname())){
                    logger.info(measurePoint.getFix_tagname() + "remove success");
                }else {
                    logger.info(measurePoint.getFix_tagname() + "remove failed");
                }

            }

        }

    }




    public synchronized void registerItem2(String tagname) {

            if (opcServe.registerItem(tagname)) {
                logger.error(tagname + "register success");
            } else {
                logger.error(tagname + "register failed");
            }
    }


    public synchronized void registerItem(List<MeasurePoint> tagname) {

        for (MeasurePoint measurePoint : tagname) {

            if (!opcServe.isConectstatus()) {
                //离线模式，暂时先存储起来，连接成功再进行插入
                addtag_offline(measurePoint);
                continue;
            }
            if (opcServe.registerItem(measurePoint.getFix_tagname())) {
                if (registeredMeasurePoint.get(measurePoint.getFix_tagname()) == null) {
                    registeredMeasurePoint.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                }
                registeredMeasurePoint.get(measurePoint.getFix_tagname()).add(measurePoint);
            } else {
                logger.error(measurePoint.getFix_tagname() + "register failed");
            }
        }

    }


    @PreDestroy
    public void disconnect() {
        logger.info("Disconect opc serve");
        if (opcServe != null) {
            opcServe.disconnect();
        }
    }

}
