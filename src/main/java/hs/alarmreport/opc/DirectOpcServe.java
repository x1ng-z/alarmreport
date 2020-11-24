package hs.alarmreport.opc;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import hs.alarmreport.device.MeasurePoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
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
 * @date 2020/11/23 23:20
 */

@Component
public class DirectOpcServe implements Runnable, Service {
    private Logger logger = LoggerFactory.getLogger(DirectOpcServe.class);

    private String opcservename;
    private String opcip;

    private Pointer opcclient;

    private boolean connectstatus = false;

    private Map<String, List<MeasurePoint>> registeredMeasurePoint = new ConcurrentHashMap();

    private ItemManger itemManger;
    ExecutorService executorService;
    Thread thread;

    private List<MeasurePoint> waittoregistertag=new CopyOnWriteArrayList<>();


    public synchronized void addtag_offline(MeasurePoint m){
        waittoregistertag.add(m);
    }



    public DirectOpcServe(@Value("${opc.servename}") String opcservename, @Value("${opc.ip}") String opcip, @Autowired ItemManger itemManger, @Autowired ExecutorService executorService) {
        this.opcservename = opcservename;
        this.opcip = opcip;
        this.executorService = executorService;
        this.itemManger = itemManger;

        connect();


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
    public void init() {

    }


    @Override
    public synchronized void connect() {
        opcclient = OTT.INSTANTCE.CONNECT(opcip, opcservename);
        if (opcclient == null) {
            connectstatus = false;
        } else {
            connectstatus = true;
        }
    }

    @Override
    public void reconnect() {
        if (opcclient != null) {
            OTT.INSTANTCE.DISCONNECT(opcclient);
            opcclient = null;
        }
        logger.info(opcip + opcservename);

        connect();
        if (opcclient != null) {
            logger.info("connect success");
            connectstatus = true;
            itemManger.clear();
            for (String tag : registeredMeasurePoint.keySet()) {
                registerItem2(tag);
            }
            synchronized (waittoregistertag){
                registerItem(waittoregistertag);
            }


        } else {
            connectstatus = false;
        }

    }


    private boolean _registerItem(String tagname) {
        if (!itemManger.iscontainstag(tagname)) {
            logger.info("try to add " + tagname);
            if (OTT.INSTANTCE.ADDITEM(opcclient, tagname) == 1) {

                ItemUnit newitem = new ItemUnit();
                newitem.setItem(tagname);
                newitem.addrefrencecount();
                itemManger.addItemUnit(tagname, newitem);

                logger.info(tagname + "register success");
                return true;
            } else {
                logger.error(tagname + "register failed");
                return false;
            }
        }

        return false;
    }

    public synchronized void registerItem2(String tagname) {

        if (!itemManger.iscontainstag(tagname)) {
            if (_registerItem(tagname)) {
                logger.error(tagname + "register success");
            } else {
                logger.error(tagname + "register failed");
            }

        }


    }


    @Override
    public synchronized void registerItem(List<MeasurePoint> tagname) {

        for (MeasurePoint measurePoint : tagname) {

            if (!itemManger.iscontainstag(measurePoint.getFix_tagname())) {
                if(opcclient==null){
                    //离线模式，暂时先存储起来，连接成功再进行插入
                    addtag_offline(measurePoint);
                    continue;
                }
                if (_registerItem(measurePoint.getFix_tagname())) {
                    if (registeredMeasurePoint.get(measurePoint.getFix_tagname()) == null) {
                        registeredMeasurePoint.put(measurePoint.getFix_tagname(), new CopyOnWriteArrayList<>());
                    }
                    registeredMeasurePoint.get(measurePoint.getFix_tagname()).add(measurePoint);
                } else {
                    logger.error(measurePoint.getFix_tagname() + "register failed");
                }

            }
        }

    }


    private boolean _removeItem(String tagname) {
        if (!itemManger.iscontainstag(tagname)) {
            String[] waitremovetag = new String[]{tagname};
            if (OTT.INSTANTCE.REMOVEITEMS(opcclient, waitremovetag, 1) == 1) {

                itemManger.removeItemUnit(tagname);

                logger.info(tagname + "remove success");
                return true;
            } else {
                logger.error(tagname + "remove failed");
                return false;
            }
        }

        return false;
    }


    public synchronized void removeItem(List<MeasurePoint> tagname) {
        for (MeasurePoint measurePoint : tagname) {
            if (registeredMeasurePoint.get(measurePoint.getFix_tagname()) != null) {
                List<MeasurePoint> chainmeasure = registeredMeasurePoint.get(measurePoint.getFix_tagname());
                chainmeasure.remove(measurePoint);
                itemManger.getItemUnit(measurePoint.getFix_tagname()).minsrefrencecount();
                if (itemManger.getItemUnit(measurePoint.getFix_tagname()).isnorefrence()) {
                    if (_removeItem(measurePoint.getFix_tagname())) {
                        logger.info(measurePoint.getFix_tagname() + "remove success");
                    }
                }
            }

        }

    }

    @PreDestroy
    @Override
    public void disconnect() {
        logger.info("Disconect opc serve");
        if (opcclient != null) {
            OTT.INSTANTCE.DISCONNECT(opcclient);
            opcclient = null;
        }
    }

    @Override
    public void run() {

        Integer writevloop = 0;
        while (!Thread.currentThread().isInterrupted()) {

            if (!connectstatus) {
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

        Memory valueBuf = new Memory(registeredMeasurePoint.size() * 4);
        if (OTT.INSTANTCE.READALLREGISTERPOINTNUMS(opcclient, valueBuf) == 0) {
            connectstatus = false;
            return;
        }
        logger.info("in readAndProcessDataByOnce size=" + registeredMeasurePoint.size());

        float[] values = valueBuf.getFloatArray(0, registeredMeasurePoint.size());

        pinValueUpdate(values);

    }


    private void pinValueUpdate(float[] values) {


        List<ItemUnit> readOrder = itemManger.getTagOrderList();

        int index = 0;
        for (ItemUnit itemUnit : readOrder) {

            List<MeasurePoint> list = registeredMeasurePoint.get(itemUnit.getItem());
            if (list != null) {
                for (MeasurePoint measurePoint : list) {
                    measurePoint.setCurrent_value(values[index]);
                    measurePoint.setCurrent_time(new Date());
                    logger.info(measurePoint.getFix_tagname() + " value =" + values[index]);
                }
            }
            index++;
        }

    }
}
