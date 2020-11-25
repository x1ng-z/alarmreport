package hs.alarmreport.opc;



import org.openscada.opc.lib.da.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/7/16 17:29
 */

@Component
public class ItemManger {
    private static  Logger logger = LoggerFactory.getLogger(DirectOpcServe.class);
    private Map<String, ItemUnit> opcitemunitPool = new ConcurrentHashMap<>();//key=标签，value=ItemUnit

    public List<ItemUnit> getTagOrderList() {
        return tagOrderList;
    }

    private List<ItemUnit> tagOrderList=new CopyOnWriteArrayList<>();

   public void clear(){
       opcitemunitPool.clear();
       tagOrderList.clear();
   }

    public boolean iscontainstag(String tagname){
        return opcitemunitPool.containsKey(tagname);
    }

    public void addItemUnit(String tagname,ItemUnit itemUnit){
        if((itemUnit.getItem()!=null)&&(opcitemunitPool.get(tagname)==null)){
            tagOrderList.add(itemUnit);
            opcitemunitPool.put(tagname,itemUnit);
        }else {
            logger.error("Itemname为空或已经添加item管理池");
        }
    }


    public ItemUnit removeItemUnit(String tagname){
        ItemUnit itemUnit=opcitemunitPool.remove(tagname);
        tagOrderList.remove(itemUnit);
        return itemUnit;
    }


    public ItemUnit getItemUnit(String tagname){
        return opcitemunitPool.get(tagname);
    }




}
