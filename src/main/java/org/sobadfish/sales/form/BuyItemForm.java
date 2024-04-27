package org.sobadfish.sales.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.element.ElementSlider;
import cn.nukkit.form.element.ElementToggle;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.sales.SalesMainClass;
import org.sobadfish.sales.Utils;
import org.sobadfish.sales.economy.IMoney;
import org.sobadfish.sales.entity.SalesEntity;
import org.sobadfish.sales.items.SaleItem;

import java.util.LinkedHashMap;

/**
 * @author Sobadfish
 * @date 2024/4/27
 */
public class BuyItemForm {
    private final int id;

    private static int getRid(){
        return Utils.rand(423300,523300);
    }

    public SaleItem salesItem;

    public SalesEntity salesEntity;

    public int getId() {
        return id;
    }

    public static LinkedHashMap<String, BuyItemForm> DISPLAY_FROM = new LinkedHashMap<>();


    public BuyItemForm(SalesEntity entity,SaleItem salesItem) {
        this.salesEntity = entity;
        this.salesItem = salesItem;
        this.id = getRid();
    }

    public void display(Player player){
        IMoney iMoney = SalesMainClass.getMoneyCoreByName(salesItem.loadMoney);

        int maxCount = getLimitCount(player.getName());
        int stock = (int)Math.floor(salesItem.stack / (float)salesItem.saleItem.getCount());
        if(maxCount == -1){
            //最大选择为 99
            maxCount = 99;
        }
        if(!salesItem.tag.contains("noreduce") || !salesItem.tag.getBoolean("noreduce")){
            maxCount = Math.min(stock,99);
        }
        boolean isSell = false;
        String title = "购买";
        if(salesItem.tag.contains("sales_exchange") && salesItem.tag.getBoolean("sales_exchange",false)){
            //收购
            title = "出售";
            isSell = true;
        }

        int disCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("&e").append(iMoney.displayName()).append(" &r: ").append(String.format("%.2f",iMoney.myMoney(player.getName()))).append("\n\n");
        stringBuilder.append("&7售货机名称: &r: ").append(salesEntity.salesData.customname).append("\n\n");
        stringBuilder.append("&7售货机坐标: &r: ").append(salesEntity.salesData.location).append("\n");
        stringBuilder.append("&7售货机主人: &r: ").append(salesEntity.master).append("\n\n");
        stringBuilder.append("&7当前库存: &l&a").append(stock > 0 ? stock : "&c缺货").append("\n\n");
        if(!isSell){
            Item dItem = salesItem.getDiscountItem(player,salesEntity,salesItem.saleItem);
            if(dItem != null){
                disCount = dItem.count;
            }

            stringBuilder.append("&7可用优惠券: &l&a").append(disCount).append("\n\n");
            SaleItem.ZkMoney zkMoney = salesItem.getMoneyStr();
            stringBuilder.append("&7价格: &l&a").append(zkMoney.msg).append("\n");

        }else{
            stringBuilder.append("&7价格: &l&a" + "&r&7").append(iMoney.displayName()).append(" &7* &e").append(salesItem.money != 0 ? salesItem.money : "免费").append("\n\n");
            int pStock = salesItem.getInventoryItemCount(player.getInventory(),salesItem.saleItem);
            int canCell = (int) Math.floor(pStock / (float) salesItem.saleItem.getCount());
            maxCount = Math.min(canCell,99);
            stringBuilder.append("&7我的库存: &l&a").append(pStock).append(" &7(可出售: &e").append(canCell).append("&7)").append("\n\n");
        }


        FormWindowCustom custom = new FormWindowCustom("售货机 ————— "+title);
        custom.addElement(new ElementLabel(TextFormat.colorize('&',stringBuilder.toString())));
        custom.addElement(new ElementSlider("请选择"+title+"数量",0,maxCount,1,0));
        if(disCount > 0){
            custom.addElement(new ElementToggle("是否使用优惠券",true));
        }

        player.showFormWindow(custom,getId());
        DISPLAY_FROM.put(player.getName(),this);
    }


    public void onListener(Player player, FormResponseCustom responseCustom){

        if(salesEntity != null && !salesEntity.finalClose && !salesEntity.closed){
            boolean dis = false;
            if(responseCustom.getResponses().size() > 2){
                dis = responseCustom.getToggleResponse(2);
            }
            int buyCount;
            buyCount = (int) responseCustom.getSliderResponse(1);

            if(buyCount > 0){
                if(salesItem.toBuyItem(salesEntity,player,dis,buyCount)){
                    SalesMainClass.sendMessageToObject("&a交易完成！", player);
                }
            }
        }

    }


    public int getLimitCount(String player){
        if(salesItem.tag.contains("limitCount") ) {
            int limit = salesItem.tag.getInt("limitCount");
            int upsLimit = salesItem.getUserLimitCount(player);
            return Math.max(limit - upsLimit,0);
        }
        return -1;
    }
}
