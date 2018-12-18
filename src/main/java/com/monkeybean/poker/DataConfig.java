package com.monkeybean.poker;

/**
 * 数据配置类
 */
public class DataConfig {
    /**
     * 玩家初始金币数
     */
    public static final int INIT_GOLD_NUM = 1000;
    /**
     * 每局底注
     */
    public static final int INIT_BET = 10;
    /**
     * 机器人每次加注金币数
     */
    public static final int ADD_BET = 10;
    /**
     * 格式控制，每行可输出牌的最大量
     */
    public static final int PRINT_NUM = 8;
    /**
     * 每局牌局总押注
     */
    public static int betTotal = 10;
    /**
     * 是否为机器人对战测试
     */
    public static boolean isRobotTest = false;
    /**
     * 机器人对战测试次数
     */
    public static int robotTestNum = 1000;
}
