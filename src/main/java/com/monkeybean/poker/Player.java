package com.monkeybean.poker;

/**
 * 玩家类
 */
public class Player {
    /**
     * 类常量，玩家最多的手牌数
     */
    private static final int CARD_NUM = 5;
    /**
     * 对象常量，玩家初始金币数
     */
    private final int initGoldNum;
    /**
     * 玩家当前的金币数
     */
    private int nowGoldNum;
    /**
     * 记录玩家是否为机器人，备用
     */
    private boolean isRobot;
    /**
     * 玩家当前手牌数量
     */
    private int curCardNum = 0;
    /**
     * 玩家手牌数组引用
     */
    private Card[] myCards;

    /**
     * 当前玩家手牌复制地址，机器人策略使用
     */
    private Card[] ownCards;
    /**
     * 另一玩家手牌复制地址，机器人策略使用
     */
    private Card[] otherCards;

    Player(int gn, boolean ir) {
        this.initGoldNum = this.nowGoldNum = gn;
        this.isRobot = ir;
        this.myCards = new Card[CARD_NUM];
        this.ownCards = new Card[CARD_NUM];
        this.otherCards = new Card[CARD_NUM];

        for (int i = 0; i < Player.CARD_NUM; i++) {
            myCards[i] = new Card(-1, -1);
            ownCards[i] = new Card(-1, -1);
            otherCards[i] = new Card(-1, -1);
        }
    }

    /**
     * 获取玩家可拥有最多手牌数
     *
     * @return 玩家最多可拥有手牌数量
     */
    public static int getTotalCardNum() {
        return Player.CARD_NUM;
    }

    /**
     * 机器人角色策略判断 发牌前，两机器人角色的判断逻辑 机器人采用不怂的策略，即机器人只要不弃牌，不管另一机器人选择加注还是跟，均进入发下一张牌的阶段
     *
     * @param p1      角色1
     * @param p2      角色2
     * @param cardNum 当前已发牌的数量
     * @return 判断结果，包括发牌、角色1胜利和角色2胜利
     */
    public static ChooseType robotJudge(Player p1, Player p2, int cardNum) {

        // 存储判断结果
        ChooseType chooseType1 = p1.getChoose(cardNum, p2.getMyCards());
        ChooseType chooseType2 = p2.getChoose(cardNum, p1.getMyCards());
        switch (chooseType1) {
            case Raise:
                switch (chooseType2) {
                    case Deal:
                        // 判断双方金币是否允许本次加注
                        if ((DataConfig.betTotal + DataConfig.ADD_BET <= p1.getGoldNum()) && (DataConfig.betTotal + DataConfig.ADD_BET <= p2.getGoldNum())) {
                            DataConfig.betTotal += DataConfig.ADD_BET;
                            System.out.println("加注");
                        } else {
                            System.out.println("对战双方有人金币不足，不可加注，发牌");
                        }
                        return ChooseType.Deal;
                    case Fold:
                        return ChooseType.FirstWin;
                    case Raise:
                        if ((DataConfig.betTotal + DataConfig.ADD_BET <= p1.getGoldNum()) && (DataConfig.betTotal + DataConfig.ADD_BET <= p2.getGoldNum())) {
                            DataConfig.betTotal += DataConfig.ADD_BET;
                            System.out.println("加注");

                            if ((DataConfig.betTotal + DataConfig.ADD_BET <= p1.getGoldNum()) && (DataConfig.betTotal + DataConfig.ADD_BET <= p2.getGoldNum())) {
                                DataConfig.betTotal += DataConfig.ADD_BET;
                                System.out.println("再加注");
                            } else {
                                System.out.println("对战双方有人金币不足，不可再加注，跟");
                            }
                        } else {
                            System.out.println("对战双方有人金币不足，不可加注，发牌");
                        }
                        return ChooseType.Deal;
                    default:
                        break;
                }
                break;

            case Fold:
                return ChooseType.SecondWin;

            case Deal:
                switch (chooseType2) {
                    case Deal:
                        System.out.println("发牌");
                        return ChooseType.Deal;

                    case Fold:
                        return ChooseType.FirstWin;

                    case Raise:
                        if ((DataConfig.betTotal + DataConfig.ADD_BET <= p1.getGoldNum()) && (DataConfig.betTotal + DataConfig.ADD_BET <= p2.getGoldNum())) {
                            DataConfig.betTotal += DataConfig.ADD_BET;
                            System.out.println("加注");
                        } else {
                            System.out.println("对战双方有人金币不足，不可加注，发牌");
                        }
                        return ChooseType.Deal;
                    default:
                        break;
                }
                break;

            default:
                break;
        }

        return ChooseType.Deal;
    }

    /**
     * 获取玩家当前金币数
     *
     * @return 玩家当前金币数
     */
    public int getGoldNum() {
        return this.nowGoldNum;
    }

    /**
     * 更改玩家当前的金币数
     *
     * @param addNum 增加的金币数
     */
    public void addGoldNum(int addNum) {
        this.nowGoldNum += addNum;
        return;
    }

    /**
     * 输出玩家当前金币数及损益
     */
    public void printBenefit() {
        System.out.println("当前金币数为" + this.nowGoldNum);
        System.out.println("当前损益为" + (this.nowGoldNum - this.initGoldNum));
        return;
    }

    /**
     * 获取单张手牌
     *
     * @param oneCard 所获取牌的引用
     */
    public void getOneCard(Card oneCard) {
        myCards[this.curCardNum].setNumber(oneCard.getNumber());
        myCards[this.curCardNum].setColor(oneCard.getColor());
        this.curCardNum++;
        return;
    }

    /**
     * 获取玩家当前手牌数量
     *
     * @return 玩家当前手牌数量
     */
    public int getCurCardNum() {
        return this.curCardNum;
    }

    /**
     * 玩家当前手牌数量置0
     */
    public void resetCardNum() {
        this.curCardNum = 0;
        return;
    }

    /**
     * 获取玩家手牌myCards数组的引用
     *
     * @return 玩家手牌数组引用
     */
    public Card[] getMyCards() {
        return this.myCards;
    }

    /**
     * 获取当前角色是否为机器人，备用
     *
     * @return 是否机器人的布尔值
     */
    public boolean isRobot() {
        return this.isRobot;
    }

    /**
     * 获取当前机器人的选择
     *
     * @param cardNum 当前已发牌的数量
     * @param others  其他角色的手牌引用，第一张为暗牌，判断中可预测
     * @return 选择结果，包括发牌、加注、跟注、弃牌
     */
    public ChooseType getChoose(int cardNum, Card[] others) {
        //由于暗牌的存在，每次排序为复制空间的手牌数组
        for (int i = 0; i < cardNum; i++) {
            this.ownCards[i].setNumber(this.myCards[i].getNumber());
            this.ownCards[i].setColor(this.myCards[i].getColor());

            this.otherCards[i].setNumber(others[i].getNumber());
            this.otherCards[i].setColor(others[i].getColor());
        }

        // 两张牌的判断，若自己有鬼牌或者一对则加注，对面明牌是鬼牌则弃牌，其他情况选择发牌
        if (cardNum == 2) {
            if ((this.ownCards[0].getNumber() == 0) || (this.ownCards[1].getNumber() == 0) || (this.ownCards[0].getNumber() == this.ownCards[1].getNumber())) {
                return ChooseType.Raise;
            } else if (this.otherCards[1].getNumber() == 0) {
                return ChooseType.Fold;
            } else {
                return ChooseType.Deal;
            }
        } else if (cardNum == 3 || cardNum == 4) {
            int sameNumber = 0, otherSame = 0;
            int normalNum = cardNum, ghostNum = 0, otherNormal = cardNum, otherGhost = 0;
            boolean sameColor = true;
            // 牌型，枚举类的标记
            CardType resultType = null;
            CardType otherType = null;

            // 当前角色牌进行排序
            for (int i = 0; i < cardNum; i++) {
                int index = i;
                Card tmpCard = null;

                for (int j = i + 1; j < cardNum; j++) {
                    if (this.ownCards[j].getNumber() > this.ownCards[index].getNumber()) {
                        index = j;
                    } else if (this.ownCards[j].getNumber() == this.ownCards[index].getNumber()) {
                        if (this.ownCards[j].getColor() > this.ownCards[index].getColor()) {
                            index = j;
                        }
                    }
                }
                if (index != i) {
                    tmpCard = this.ownCards[i];
                    this.ownCards[i] = this.ownCards[index];
                    this.ownCards[index] = tmpCard;
                }

                // 判断是否为鬼牌
                if (this.ownCards[i].getNumber() == 0) {
                    normalNum--;
                    ghostNum++;
                }
            }

            // 判断当前角色两两相同牌的数量
            for (int i = 0; i < normalNum; i++) {
                for (int j = i + 1; j < normalNum; j++) {
                    if (this.ownCards[i].getNumber() == this.ownCards[j].getNumber()) {
                        sameNumber++;
                    }
                }

                // 判断是否为同花
                if (this.ownCards[i].getColor() != this.ownCards[0].getColor()) {
                    sameColor = false;
                }
            }

            // 另一角色牌进行排序
            for (int i = 1; i < cardNum; i++) {
                int index = i;
                Card tmpCard = null;

                for (int j = i + 1; j < cardNum; j++) {
                    if (this.otherCards[j].getNumber() > this.otherCards[index].getNumber()) {
                        index = j;
                    } else if (this.otherCards[j].getNumber() == this.otherCards[index].getNumber()) {
                        if (this.otherCards[j].getColor() > this.otherCards[index].getColor()) {
                            index = j;
                        }
                    }
                }
                if (index != i) {
                    tmpCard = this.otherCards[i];
                    this.otherCards[i] = this.otherCards[index];
                    this.otherCards[index] = tmpCard;
                }

                // 判断是否为鬼牌
                if (this.otherCards[i].getNumber() == 0) {
                    otherNormal--;
                    otherGhost++;
                }
            }

            // 判断另一角色两两相同牌的数量
            for (int i = 1; i < otherNormal; i++) {
                for (int j = i + 1; j < otherNormal; j++) {
                    if (this.otherCards[i].getNumber() == this.otherCards[j].getNumber()) {
                        otherSame++;
                    }
                }
            }

            // 三张牌的判断
            if (cardNum == 3) {
                // 判断自己牌型
                switch (sameNumber) {
                    case 3:
                        resultType = CardType.SanTiao;
                        break;
                    case 1:
                        if (ghostNum == 1) {
                            resultType = CardType.SanTiao;
                        } else {
                            resultType = CardType.DanDui;
                        }
                        break;
                    case 0:
                        if (ghostNum == 3) {
                            resultType = CardType.SanTiao;
                        } else if (ghostNum == 2) {
                            resultType = CardType.SanTiao;
                        } else if (sameColor) {
                            resultType = CardType.TongHua;
                        } else if (ghostNum == 1) {
                            resultType = CardType.DanDui;
                        } else {
                            resultType = CardType.SanPai;
                        }
                        break;
                }

                // 判断另一角色牌型
                switch (otherSame) {
                    case 1:
                        otherType = CardType.DanDui;
                        break;
                    case 0:
                        if (otherGhost == 2) {
                            otherType = CardType.SanTiao;
                        } else if (otherGhost == 1) {
                            otherType = CardType.DanDui;
                        } else {
                            otherType = CardType.SanPai;
                        }
                }
            } // end of if (cardNum == 3)
            // 四张牌的判断
            else if (cardNum == 4) {
                // 判断自己牌型
                switch (sameNumber) {
                    case 6:
                        resultType = CardType.SiTiao;
                        break;
                    case 3:
                        if (ghostNum == 0) {
                            resultType = CardType.SanTiao;
                        } else if (ghostNum == 1) {
                            resultType = CardType.SiTiao;
                        }
                        break;
                    case 2:
                        resultType = CardType.ErDui;
                        break;
                    case 1:
                        if (ghostNum == 2) {
                            resultType = CardType.SiTiao;
                        } else if (ghostNum == 1) {
                            resultType = CardType.SanTiao;
                        } else {
                            resultType = CardType.DanDui;
                        }
                        break;
                    case 0:
                        if (ghostNum == 4) {
                            resultType = CardType.SiTiao;
                        } else if (ghostNum == 3) {
                            resultType = CardType.SiTiao;
                        } else if (ghostNum == 2) {
                            resultType = CardType.SanTiao;
                        } else if (sameColor) {
                            resultType = CardType.TongHua;
                        } else if (ghostNum == 1) {
                            resultType = CardType.DanDui;
                        } else {
                            resultType = CardType.SanPai;
                        }
                        break;
                }

                // 判断另一角色牌型
                switch (otherSame) {
                    case 3:
                        otherType = CardType.SanTiao;
                        break;
                    case 1:
                        if (otherGhost == 1) {
                            otherType = CardType.SanTiao;
                        } else {
                            otherType = CardType.DanDui;
                        }
                        break;
                    case 0:
                        if (otherGhost == 3) {
                            otherType = CardType.SiTiao;
                        }
                        if (otherGhost == 2) {
                            otherType = CardType.SanTiao;
                        } else if (otherGhost == 1) {
                            otherType = CardType.DanDui;
                        } else {
                            otherType = CardType.SanPai;
                        }
                        break;
                    default:
                        break;
                }
            } // end of else if (cardNum == 4)

            ControlTools.printOut("选择权玩家牌型:" + resultType);
            ControlTools.printOut("另一玩家牌型:" + otherType);

            // 当前角色手牌为3或4时，根据牌型权值判断
            if (resultType.ordinal() >= otherType.ordinal()) {
                if (resultType.ordinal() == 0) {
                    return ChooseType.Deal;
                } else {
                    return ChooseType.Raise;
                }
            } else {
                return ChooseType.Fold;
            }
        } // end of else if (cardNum == 3 || cardNum == 4)

        return ChooseType.Deal;
    }
}
