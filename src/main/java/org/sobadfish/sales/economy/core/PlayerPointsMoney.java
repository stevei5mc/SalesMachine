package org.sobadfish.sales.economy.core;

import net.player.api.Point;
import org.sobadfish.sales.economy.IMoney;

/**
 * @author Sobadfish
 * @date 2024/4/8
 */
public class PlayerPointsMoney implements IMoney {
    @Override
    public String displayName() {
        return "点券";
    }

    @Override
    public boolean reduceMoney(String player, double money) {
        return Point.reducePoint(player, money);
    }

    @Override
    public boolean addMoney(String player, double money) {
        Point.addPoint(player, money);
        return true;
    }

    @Override
    public double myMoney(String player) {
        return Point.myPoint(player);
    }
}
