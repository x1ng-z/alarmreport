package hs.alarmreport.opc;

import org.openscada.opc.lib.da.Item;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/2 9:20
 */
public class OpcVeriTag {
    private int tagid  ;
    private String tagName  ;
    private String tag; //'opc验证位号',
    private int opcserveid; //'opc serve的id'
    private Item item;//注册opc serve 得到的item

    public int getTagid() {
        return tagid;
    }

    public void setTagid(int tagid) {
        this.tagid = tagid;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getOpcserveid() {
        return opcserveid;
    }

    public void setOpcserveid(int opcserveid) {
        this.opcserveid = opcserveid;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
