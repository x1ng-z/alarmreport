package hs.alarmreport.resourceparse;


import hs.alarmreport.bean.AlarmMessage;
import hs.alarmreport.device.Device;
import hs.alarmreport.device.MeasurePoint;
import hs.alarmreport.device.ProductionLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class Base4Xml implements BaseResource {
    private Logger logger = LoggerFactory.getLogger(Base4Xml.class);
    List<ProductionLine> PCollects;
    private boolean Flag_F2R = false;
    private final static String version = "soft";

    public Base4Xml() {
        this.PCollects = new ArrayList<ProductionLine>();
        Find();
    }


    @Override
    public void Find() {
        PCollects.clear();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            String filepath = System.getProperty("user.dir")+"\\conf\\Production_Line.xml";;

//            ApplicationHome h = new ApplicationHome(this.getClass());
//            // 本地获取的路径 D:\idea\springboot2.x\target  upload 跟 项目jar平级
//            String path = h.getSource().getParent();
//            String realPath = path +"\\conf";
//            filepath = realPath+"\\Production_Line.xml";
//            if(version.equals("soft")){
//                filepath=System.getProperty("user.dir")+"/conf/Production_Line.xml";
//            }else if(version.equals("web")){
//                filepath=servletContext.getRealPath("/WEB-INF")+"/conf/Production_Line.xml";
//
//            }
            Document xmldoc = null;
            xmldoc = db.parse(new BufferedInputStream(new FileInputStream(new File(filepath))));
            Element root = xmldoc.getDocumentElement();
            NodeList PLList = root.getElementsByTagName("Production_Line");

            for (int i = 0; i < PLList.getLength(); i++) {
                if (PLList.item(i) instanceof Element) {
                    Node ProductionLine_Node = PLList.item(i);
                    String CompanyNo = ((Element) ProductionLine_Node).getAttribute("PRL_ID");
                    String CompanyName = ((Element) ProductionLine_Node).getAttribute("PRL_NAME");
                    ProductionLine newPL = new ProductionLine(CompanyName, CompanyNo);

                    NodeList DeviceNodeList = ProductionLine_Node.getChildNodes();

                    PCollects.add(newPL);

                    for (int j = 0; j < DeviceNodeList.getLength(); j++) {

                        if (DeviceNodeList.item(j) instanceof Element) {
                            Element DeviceNode = ((Element) DeviceNodeList.item(j));
                            String DEV_NAME = DeviceNode.getAttribute("DEV_NAME");
                            String DEV_NO = DeviceNode.getAttribute("DEV_NO");
                            Device device = new Device(DEV_NO, DEV_NAME);

                            newPL.addDevice(DEV_NO, device);

                            NodeList MeasurePointNodeList = DeviceNode.getChildNodes();


                            for (int k = 0; k < MeasurePointNodeList.getLength(); ++k) {

                                if (MeasurePointNodeList.item(k) instanceof Element) {

                                    Element MeasurePointNode = ((Element) MeasurePointNodeList.item(k));

                                    float ALM_LEV = Float.valueOf(MeasurePointNode.getAttribute("ALM_LEV"));
                                    float CHA_RATE = Float.valueOf(MeasurePointNode.getAttribute("CHA_RATE"));
                                    String DEV_PRO = MeasurePointNode.getAttribute("DEV_PRO");
                                    String DEV_TAG = MeasurePointNode.getAttribute("DEV_TAG");
                                    String DEV_TAGCH = MeasurePointNode.getAttribute("DEV_TAGCH");
                                    String DEV_TAG_NO = MeasurePointNode.getAttribute("DEV_TAG_NO");
                                    List<String> DE_TE_EL = Arrays.asList(MeasurePointNode.getAttribute("DE_TE_EL").split("&"));
                                    float HEA_VALUE = Float.valueOf(MeasurePointNode.getAttribute("HEA_VALUE"));
                                    float HHI_LIM = Float.valueOf(MeasurePointNode.getAttribute("HHI_LIM"));
                                    float HIGH_LIM = Float.valueOf(MeasurePointNode.getAttribute("HIGH_LIM"));
                                    boolean IS_AUDIO = Boolean.valueOf(MeasurePointNode.getAttribute("IS_AUDIO"));
                                    boolean IS_MAIN = Boolean.valueOf(MeasurePointNode.getAttribute("IS_MAIN"));
                                    boolean IS_PUSH = Boolean.valueOf(MeasurePointNode.getAttribute("IS_PUSH"));
                                    float LLO_LIM = Float.valueOf(MeasurePointNode.getAttribute("LLO_LIM"));
                                    float LOW_LIM = Float.valueOf(MeasurePointNode.getAttribute("LOW_LIM"));
                                    boolean IS_SCORE = Boolean.valueOf(MeasurePointNode.getAttribute("IS_SCORE"));

                                    MeasurePoint measurePoint = new MeasurePoint(
                                            DEV_NAME,
                                            DEV_NO,
                                            DEV_TAG_NO,
                                            DEV_TAG,
                                            DEV_TAGCH,
                                            DE_TE_EL, HHI_LIM
                                            , HIGH_LIM,
                                            LLO_LIM,
                                            LOW_LIM,
                                            CHA_RATE,
                                            IS_AUDIO,
                                            ALM_LEV,
                                            DEV_PRO,
                                            IS_PUSH,
                                            HEA_VALUE,
                                            IS_MAIN,
                                            CompanyNo, CompanyName, IS_SCORE);


                                    if (!IS_SCORE) {
                                        device.addno_ScorePoint_DBtag(DEV_TAG, measurePoint);
                                        device.addno_ScorePoint_Uniontag(DEV_TAG_NO, measurePoint);
                                    } else {
                                        if (IS_MAIN) {
                                            device.addmain_point_DBtag(DEV_TAG, measurePoint);
                                            device.addMain_point_Uniontag(DEV_TAG_NO, measurePoint);
                                        } else {
                                            device.addauxiliary_point_DBtag(DEV_TAG, measurePoint);
                                            device.addauxiliary_point_Uniontag(DEV_TAG_NO, measurePoint);
                                        }


                                    }

                                }


                            }
                            device.setMeasurePoint_ScoreBase();

                        }


                    }


                }


            }
            Flag_F2R = true;
//            notifyAll();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void Update(String PRL_ID, String DEV_NO, String DEV_TAG_NO, String attr, String value) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setIgnoringElementContentWhitespace(true);
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            String filepath = System.getProperty("user.dir") + "/conf/Production_Line.xml";
            Document xmldoc = db.parse(new BufferedInputStream(new FileInputStream(new File(filepath))));//LX_Production_Line_one.class.getResourceAsStream("Production_Line1.xml")

            Element root = xmldoc.getDocumentElement();

            NodeList PLList = root.getChildNodes();
            for (int i = 0; i < PLList.getLength(); i++) {
                if (PLList.item(i) instanceof Element) {
                    Element PLElment = (Element) PLList.item(i);
                    if (PLElment.getAttribute("PRL_ID").equals(PRL_ID)) {

                        NodeList DeviceNodeList = PLElment.getChildNodes();
                        for (int j = 0; j < DeviceNodeList.getLength(); j++) {
                            if (DeviceNodeList.item(j) instanceof Element) {

                                Element DeviceElment = (Element) DeviceNodeList.item(j);
                                if (DeviceElment.getAttribute("DEV_NO").equals(DEV_NO)) {

                                    NodeList MeasurePointNodeList = DeviceElment.getChildNodes();
                                    for (int k = 0; k < MeasurePointNodeList.getLength(); k++) {
                                        if (MeasurePointNodeList.item(k) instanceof Element) {

                                            Element MeasurePointElment = (Element) MeasurePointNodeList.item(k);
                                            if (MeasurePointElment.getAttribute("DEV_TAG_NO").equals(DEV_TAG_NO)) {
                                                MeasurePointElment.setAttribute(attr, value);

                                            }
                                        }

                                    }


                                }
                            }


                        }

                    }


                }

            }

            BufferedOutputStream filename = new BufferedOutputStream(new FileOutputStream(new File(filepath)));
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer former = factory.newTransformer();
            former.transform(new DOMSource(xmldoc), new StreamResult(filename));
            filename.flush();
            filename.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public synchronized void Delete() {
        throw new UnsupportedOperationException();

    }


    public  List<ProductionLine> getPCollects() {
        return PCollects;
    }


//    public List<ProductionLine> getPCollects() {
//        return PCollects;
//    }
}
