package hs.alarmreport.opc;

import org.openscada.opc.lib.da.Item;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/6/15 11:51
 */
public class ItemUnit {
    private String tagname;//opc itermname  xx.pv
    private volatile int refrencecount=0;//pin、filter的引用次数

    public String getItem() {
        return tagname;
    }

    public void setItem(String tagname) {
        this.tagname = tagname;
    }

    public synchronized void  addrefrencecount(){
        ++refrencecount;
    }
    public synchronized void minsrefrencecount(){
        --refrencecount;
    }

    /**
     * 位号没有被引用了，那就可以被group移除
     * */
    public  synchronized boolean isnorefrence(){
        if(refrencecount==0){
            return true;
        }else {
            return false;
        }

    }
}
