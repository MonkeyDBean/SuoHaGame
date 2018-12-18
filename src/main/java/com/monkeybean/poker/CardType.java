package com.monkeybean.poker;

/**
 * 扑克牌型枚举类
 */
public enum CardType {
    /**
     * 单一型态的五张散牌所组成，0
     */
    SanPai,
    /**
     * 由两张相同的牌加上三张单张所组成，1
     */
    DanDui,
    /**
     * 由两组两张同数字的牌所组成，2
     */
    ErDui,
    /**
     * 由三张相同的牌组成，3
     */
    SanTiao,
    /**
     * 五张连续数字的牌组，4
     */
    ShunZi,
    /**
     * 不构成顺子的五张同花色的牌，5
     */
    TongHua,
    /**
     * 由SanTiao加一个ErDui组成，6
     */
    Hulu,
    /**
     * 四张相同数字的牌，外加一单张，7
     */
    SiTiao,
    /**
     * 五张连续性同花色的顺子，8
     */
    TongHuaShun,
    /**
     * 五张相同数字的牌，其中至少一张为鬼，9
     */
    WuTiao,
    /**
     * WuGui---五张鬼，10
     */
    WuGui
}
