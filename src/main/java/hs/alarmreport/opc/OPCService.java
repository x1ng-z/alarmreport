package hs.alarmreport.opc;

import hs.alarmreport.device.MeasurePoint;
import org.jinterop.dcom.common.JIException;
import org.openscada.opc.lib.common.AlreadyConnectedException;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.common.NotConnectedException;
import org.openscada.opc.lib.da.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/3/23 12:10
 */

//@Component
public class OPCService implements Runnable,Service {
    private Logger logger = LoggerFactory.getLogger(OPCService.class);
    private static final boolean DEBUG = false;
    private static Pattern pvenablepattern = Pattern.compile("(^pvenable\\d+$)");
    private static Pattern pvpattern = Pattern.compile("(^pv\\d+$)");
    private static Pattern ffpattern = Pattern.compile("(^ff\\d+$)");
    private static Pattern mvpattern = Pattern.compile("(^mv\\d+$)");

    private Pattern opcpattern = Pattern.compile("([a-zA-Z]*)([0-9|.]*)");
    private ConnectionInformation ci = null;
    /**
     * opc 配置信息
     */
    private int opcserveid;

    @Value("${opc.user}")
    private String opcuser;

    @Value("${opc.password}")
    private String opcpassword;

    @Value("${opc.ip}")
    private String opcip;

    @Value("${opc.clsid}")
    private String opcclsid;
    @Value("${opc.servename}")
    private String opcservename;

    private ExecutorService executorService;

    private Map<String, Item> mintagspool = new ConcurrentHashMap<>();//直接mabaties中初始化来的

    private Map<String, MeasurePoint> registeredMeasurePoint = new ConcurrentHashMap();


    private Server server;

    private Group group = null;

    /**
     * 链接状态
     */
    private boolean connectStatus = false;

    @Autowired
    public OPCService(@Value("${opc.user}")String opcuser,
                      @Value("${opc.password}") String opcpassword,
                      @Value("${opc.ip}") String opcip,
                      @Value("${opc.clsid}") String opcclsid,ExecutorService executorService) {

        this.executorService=executorService;
         this.opcuser=opcuser;

       this.opcpassword=opcpassword;
        this.opcip=opcip;

        this.opcclsid=opcclsid;


        init();
        connect();

        executorService.execute(this);
    }


    /**
     * 重连初始化
     */
    public void initAndReConnect() {

        ConnectionInformation ci = new ConnectionInformation();
        ci.setHost(opcip);
        ci.setUser(opcuser);
        ci.setPassword(opcpassword);
        ci.setProgId(null);
        ci.setClsid(opcclsid);
        server = new Server(ci, Executors.newSingleThreadScheduledExecutor());

        try {
            server.connect();
            connectStatus = true;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (AlreadyConnectedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            group = server.addGroup("opc");
            /**设置平台通信验证点号，这个要实时写入*/

        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (NotConnectedException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (DuplicateGroupException e) {
            logger.error(e.getMessage(), e);
        }
        if (group != null) {
            logger.debug("reconnect success");
            logger.debug("begin to register pin opc tag");
            List<String> tempitem = new ArrayList<>();
            for (String item : mintagspool.keySet()) {
                tempitem.add(item);
            }
            mintagspool.clear();
            List<MeasurePoint> tempmeasurelist = new ArrayList<>();
            tempmeasurelist.addAll(registeredMeasurePoint.values());
            registerItem(tempmeasurelist);
        } else {
            connectStatus = false;
            logger.warn("opc serve reconnect failed!");
        }

    }


    @Override
    public void run() {
        Integer writevloop = 0;
        while (!Thread.currentThread().isInterrupted()) {

            if (!connectStatus) {
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
            if (mintagspool.size() > 0) {
                try {
                    readAndProcessDataByOnce();
                } catch (JIException e) {
                    logger.error(e.getMessage(), e);
                    connectStatus = false;//进行重新连接
                }
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


    /**
     * 一次性获取所有数据，并进行处理
     */
    public void readAndProcessDataByOnce() throws JIException {
        Item[] temReadItems = new Item[mintagspool.size()];
        int index = 0;
        for (Item item : mintagspool.values()) {
            temReadItems[index] = item;
            index++;
        }
        Map<Item, ItemState> itemItemStateMap = group.read(true, temReadItems);
        logger.info("in readAndProcessDataByOnce size=" + itemItemStateMap.size());
        int errortagnamenum = 0;//取数报错的tag
        for (Map.Entry<Item, ItemState> entrie : itemItemStateMap.entrySet()) {
            Item item = entrie.getKey();
            ItemState itemState = entrie.getValue();
            try {
                pinValueUpdate(item, itemState);
            } catch (JIException e) {
                ++errortagnamenum;
                logger.error("opc tag=" + findItemName(item) + " maybe error when get real data");
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                ++errortagnamenum;
                logger.error(e.getMessage(), e);
                logger.error("opc tag=" + findItemName(item) + " maybe styele error when get real data");

            }
        }
        logger.info("error tag num=" + errortagnamenum);
        if (errortagnamenum == mintagspool.size()) {
            connectStatus = false;//进行重新连接
        }

    }


    /**
     * s数据处理分发
     */
    private void pinValueUpdate(Item item, ItemState itemState) throws JIException {

        String valueStringstyle;
        if (DEBUG) {
            valueStringstyle = "" + (itemState.getValue().getObjectAsUnsigned().getValue().shortValue());
        } else {
            valueStringstyle = itemState.getValue().getObject().toString();
        }


        String opcname = findItemName(item);
        if (opcname == null) {
            //位号被移除了
            return;
        }
        MeasurePoint measurePoint = registeredMeasurePoint.get(opcname);

        if (measurePoint == null) {
            //pins列表为null说明这个不是pin引脚的tag，可能是filter的opctag，这个主要是用于反写的，不需要读取
            return;
        }

        if (valueStringstyle.equals("true") || valueStringstyle.equals("on")) {
            valueStringstyle = 1 + "";
        }
        if (valueStringstyle.equals("false") || valueStringstyle.equals("off")) {
            valueStringstyle = 0 + "";
        }
        measurePoint.setCurrent_value(Float.valueOf(valueStringstyle));
        measurePoint.setCurrent_time(new Date());
    }


    @Override
    public void reconnect() {
        /***/
        try {
            server.removeGroup(group, true);
            server.disconnect();
        } catch (JIException jiException) {
            logger.error(jiException.getMessage(), jiException);
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
        }
        group = null;
        initAndReConnect();
    }


    @PostConstruct
    @Override
    public void init() {
        ci = OpcConfigure.createInstance(opcip, null, opcuser, opcpassword, opcclsid);
    }

    @Override
    public void connect() {
        try {
            server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
            server.connect();
            connectStatus = true;
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (AlreadyConnectedException e) {
            logger.error(e.getMessage(), e);
        }

        try {
            group = server.addGroup("opc");
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        } catch (NotConnectedException e) {
            logger.error(e.getMessage(), e);
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        } catch (DuplicateGroupException e) {
            logger.error(e.getMessage(), e);
        }
        if (group != null) {
            logger.debug("opc connect success");
        }

    }

    @Override
    public synchronized void registerItem(List<MeasurePoint> measurePoints) {
        /**设置平台通信验证点号，这个要实时写入*/
        for (MeasurePoint measurePoint : measurePoints) {
            try {
                Item item = group.addItem(measurePoint.getFix_tagname());
                mintagspool.put(measurePoint.getFix_tagname(), item);
                registeredMeasurePoint.put(measurePoint.getFix_tagname(), measurePoint);
            } catch (JIException e) {
                logger.error("opctag=" + measurePoint.getFix_tagname() + "registe failed");
                logger.error(e.getMessage(), e);

            } catch (AddFailedException e) {
                logger.error("opctag=" + measurePoint.getFix_tagname() + "registe failed");

                logger.error(e.getMessage(), e);
            }
        }
    }

    @PreDestroy
    @Override
    public void disconnect() {
        try {
            mintagspool.clear();
            group.clear();
            server.removeGroup(group, true);
            server.disconnect();
        } catch (JIException e) {
            logger.error(e.getMessage(), e);
        }
    }


    public String findItemName(Item item) {
        for (Map.Entry<String, Item> stringItemEntry : mintagspool.entrySet()) {
            if (stringItemEntry.getValue().equals(item)) {
                return stringItemEntry.getKey();
            }
        }
        return null;
    }

}
