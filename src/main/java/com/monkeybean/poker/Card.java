package com.monkeybean.poker;

/**
 * 单张扑克牌类
 */
public class Card {

    /**
     * 牌的数字大小 2-13为2-K，14为A，0为鬼牌
     */
    private int number;

    /**
     * 牌的花色标记 1-Club(草花)，2-Diamond(方块)，3-Heart(红桃)，4-Spade(黑桃)，0-Ghost(鬼牌)
     */
    private int colorFlag;

    public Card(int n, int cf) {
        this.number = n;
        this.colorFlag = cf;
    }

    /**
     * 获取牌的数字
     *
     * @return 扑克牌的数字大小
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * 设置牌的数字
     *
     * @param num 设置的数字
     */
    public void setNumber(int num) {
        this.number = num;
    }

    /**
     * 获取牌的花色
     *
     * @return 扑克牌的花色标记
     **/
    public int getColor() {
        return this.colorFlag;
    }

    /**
     * 设置牌的花色
     *
     * @param cf 设置的花色标记
     **/
    public void setColor(int cf) {
        this.colorFlag = cf;
    }
}
