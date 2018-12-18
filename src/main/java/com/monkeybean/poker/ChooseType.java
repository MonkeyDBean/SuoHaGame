package com.monkeybean.poker;

/**
 * 机器人选择枚举类
 */
public enum ChooseType {
    /**
     * 弃牌
     */
    Fold,
    /**
     * 发牌
     */
    Deal,
    /**
     * 跟注
     */
    Call,
    /**
     * 加注
     */
    Raise,
    /**
     * 第一个角色获胜
     */
    FirstWin,
    /**
     * 第二个角色获胜
     */
    SecondWin
}
