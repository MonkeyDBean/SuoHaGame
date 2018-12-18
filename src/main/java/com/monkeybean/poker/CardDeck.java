package com.monkeybean.poker;

/**
 * 单副扑克牌类
 */
public class CardDeck {
    /**
     * 类常量，整副牌的数量，包括5张鬼牌，共57张牌。
     */
    private static final int DECK_SIZE = 57;
    /**
     * 类常量，除鬼牌外，牌的花色种类，共4种。
     */
    private static final int COLOR_TYPE = 4;
    /**
     * 类常量，除鬼牌外，牌的数字种类，共13种。
     */
    private static final int NUMBER_TYPE = 13;
    /**
     * 类常量，鬼牌的数量，共5张。
     */
    private static final int GHOST_NUMBER = 5;
    /**
     * 存储整副牌的引用数组
     */
    private Card[] cards;
    /**
     * 每次发牌后，剩余牌的数量，初始为整副牌的数量。
     */
    private int restCardLength = CardDeck.DECK_SIZE;

    public CardDeck() {
        cards = new Card[CardDeck.DECK_SIZE];
        for (int i = 0; i < CardDeck.DECK_SIZE; i++) {
            if (i < CardDeck.DECK_SIZE - CardDeck.GHOST_NUMBER) {
                cards[i] = new Card(i % CardDeck.NUMBER_TYPE + 2, i % CardDeck.COLOR_TYPE + 1);
            } else {
                cards[i] = new Card(0, 0);
            }
        }
    }

    /**
     * 获取整副牌的数量
     *
     * @return 整副牌的数量
     */
    public static int getDeckSize() {
        return CardDeck.DECK_SIZE;
    }

    /**
     * 输出指定数组的牌，数字在前，花色在后。
     *
     * @param cds      数组名的引用
     * @param cardSize 传入数组的大小
     * @param noDark   是否显示为暗牌，开牌前，当前角色无法查看其它角色的底牌
     */
    public static void printCards(Card[] cds, int cardSize, boolean noDark) {

        // 牌的数字表示,索引0为鬼牌显示，索引1为暗牌显示，索引2-14对应2-A
        String[] types = {"Ghost", "Dark", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

        // 牌的花色表示,索引0代表鬼牌显示，1-4为草花、方片、红桃、黑桃
        // String[] colors = {"","Club","DiamMond","Heart","Spade"};
        String[] colors = {"", "♣", "♦", "♥", "♠"};

        // rowNum为每行输出元素格式控制,每行最多输出13张牌
        for (int i = 0, rowNum = 0; i < cardSize; i++) {

            // 判断是否为暗牌，暗牌为其它角色的第一张牌
            if (!noDark && cardSize < 5 && i == 0) {
                System.out.print(types[1]);
            } else {

                // 输出单张牌大小
                System.out.print(types[cds[i].getNumber()]);

                // 格式控制
                System.out.print(" ");

                // 输出单张牌花色
                System.out.print(colors[cds[i].getColor()]);
            }

            // 格式控制
            System.out.print("\t");

            // 判断每行输出是否达最大数量
            if (++rowNum % DataConfig.PRINT_NUM == 0) {
                rowNum = 0;

                // 格式控制
                System.out.print("\n");
            }
        }

        // 格式控制
        System.out.print("\n");
    }

    /**
     * 计算五张牌的牌型权值 首先对五张牌降序排序，规则为先比较数字，再比较花色，鬼牌放在最后； 权值为int（32位）类型，共使用26位，权值的构成，23-26位为牌型，1-22位确定同种牌型的大小。
     *
     * @param cds 传入牌的数组引用
     * @return 返回牌型权值
     */
    public static int getType(Card[] cds) {

        // 牌型是否为同花的标记
        boolean sameColor = true;

        // 五张牌中，其它牌及鬼牌的数量
        int normalNum = cds.length, ghostNum = 0;

        // 牌型判断标记,sameNumber为两两相同牌的配对数量，indexFlag为较大对（条）的索引
        int sameNumber = 0, indexFlag = -1;

        // 总权值及同种牌型权重
        int result = 0, heavy = 0;

        // 小数权值，同种牌型的权重
        // double decimalResult = 0.0;

        // 牌型，枚举类的标记
        CardType resultType = null;

        // 采取选择排序
        for (int i = 0; i < cds.length; i++) {
            int index = i;
            for (int j = i + 1; j < cds.length; j++) {
                if (cds[j].getNumber() > cds[index].getNumber()) {
                    index = j;
                } else if (cds[j].getNumber() == cds[index].getNumber()) {
                    if (cds[j].getColor() > cds[index].getColor()) {
                        index = j;
                    }
                }
            }
            if (index != i) {
                Card tmpCard = cds[i];
                cds[i] = cds[index];
                cds[index] = tmpCard;
            }

            // 判断是否为鬼牌
            if (cds[i].getNumber() == 0) {
                normalNum--;
                ghostNum++;
            }
        }

        ControlTools.printOut("手牌排序后输出如下");
        CardDeck.printCards(cds, cds.length, true);

        // 五鬼牌型预先判断
        if (ghostNum == 5) {
            resultType = CardType.WuGui;
            System.out.println("牌型为:" + resultType);
            result = resultType.ordinal() << 22;
            return result;
        }

        // 判断两两相等牌的数量
        for (int i = 0; i < normalNum; i++) {
            for (int j = i + 1; j < normalNum; j++) {
                if (cds[i].getNumber() == cds[j].getNumber()) {
                    sameNumber++;

                    // 记录最大的相同数字索引
                    if (indexFlag == -1) {
                        indexFlag = i;
                    }
                }
            }

            // 判断是否为同花
            if (cds[i].getColor() != cds[0].getColor()) {
                sameColor = false;
            }
        }

        // indexFlag == -1，表明普通牌中无两两相同的牌，此时索引设置后排序后的第一张牌。
        if (indexFlag == -1) {
            indexFlag = 0;
        }

        // switch条件为两两相同牌的数量，每个case下根据鬼牌数量判断牌型
        // 鬼牌数量越多的概率越小，从概率大的情况开始判断
        switch (sameNumber) {
            case 6:
                if (ghostNum == 0) {
                    resultType = CardType.SiTiao;
                } else if (ghostNum == 1) {
                    resultType = CardType.WuTiao;
                }
                break;
            case 4:
                resultType = CardType.Hulu;
                break;
            case 3:
                if (ghostNum == 0) {
                    resultType = CardType.SanTiao;
                } else if (ghostNum == 1) {
                    resultType = CardType.SiTiao;
                } else if (ghostNum == 2) {
                    resultType = CardType.WuTiao;
                }
                break;
            case 2:
                if (ghostNum == 0) {
                    resultType = CardType.ErDui;
                } else if (ghostNum == 1) {
                    resultType = CardType.Hulu;
                }
                break;
            case 1:
                if (ghostNum == 0) {
                    resultType = CardType.DanDui;
                } else if (ghostNum == 1) {
                    resultType = CardType.SanTiao;
                } else if (ghostNum == 2) {
                    resultType = CardType.SiTiao;
                } else if (ghostNum == 3) {
                    resultType = CardType.WuTiao;
                }
                break;
            case 0:
                // 牌型权值大即概率低的牌型优先级高,优先判断；同花、顺子只在当前分支出现
                if (ghostNum == 4) {
                    resultType = CardType.WuTiao;
                } else if (CardDeck.isShunZi(cds, normalNum) && sameColor) {
                    resultType = CardType.TongHuaShun;
                } else if (ghostNum == 3) {
                    resultType = CardType.SiTiao;
                } else if (sameColor) {
                    resultType = CardType.TongHua;
                } else if (CardDeck.isShunZi(cds, normalNum)) {
                    resultType = CardType.ShunZi;
                } else if (ghostNum == 2) {
                    resultType = CardType.SanTiao;
                } else if (ghostNum == 1) {
                    resultType = CardType.DanDui;
                } else if (ghostNum == 0) {
                    resultType = CardType.SanPai;
                }
                break;
            default:
                break;
        }// end of switch(sameNumber)

        // 根据牌型计算小数权值
        switch (resultType) {
            case SanPai:
            case DanDui:
            case SanTiao:
            case SiTiao:
                // 由牌型位的数字及花色确定权值
                // decimalResult = cds[indexFlag].getNumber() * 0.01 + cds[indexFlag].getColor() * 0.001;
                heavy = (cds[indexFlag].getNumber() << 2) + cds[indexFlag].getColor();
                break;

            case ErDui:
                // 由较大对数字、较小对数字及较大对的高位花色确定权值, 倒数第二位一定为第二对
                // decimalResult = cds[indexFlag].getNumber() * 0.01 + cds[normalNum - 2].getNumber() * 0.0001 +
                // cds[indexFlag].getColor() * 0.00001;
                heavy = (cds[indexFlag].getNumber() << 6) + (cds[normalNum - 2].getNumber() << 2) + cds[indexFlag].getColor();
                break;

            case TongHua:
                // 由每一位的数字以及最大数的花色确定权值
                // for (int i = 1; i <= cds.length; i++)
                // {
                // decimalResult += Math.pow(0.01, i) * cds[i - 1].getNumber();
                // }
                // decimalResult += Math.pow(0.01, cds.length) * 0.1 * cds[0].getColor();
                for (int i = 0; i < cds.length; i++) {
                    heavy = (heavy << 4) + cds[i].getNumber();
                }
                heavy = (heavy << 2) + cds[0].getColor();
                break;

            case ShunZi:
            case TongHuaShun:
                // 由最小位数字及最大位花色确定权值,最大顺子最小位小于等于10
                // 判断是否为A2345的情况
                // if (cds[0].getNumber() == 14 && cds[1].getNumber() <= 5)
                // {
                // decimalResult = 0.01 + cds[1].getColor() * 0.001;
                // }
                // else
                // {
                // decimalResult = ((cds[normalNum - 1].getNumber() > 10) ? 10 : cds[normalNum - 1].getNumber()) * 0.01
                // + cds[0].getColor() * 0.001;
                // }
                if (cds[0].getNumber() == 14 && cds[1].getNumber() <= 5) {
                    heavy = (heavy << 2) + cds[1].getColor();
                } else {
                    heavy = (((cds[normalNum - 1].getNumber() > 10) ? 10 : cds[normalNum - 1].getNumber()) << 2) + cds[0].getColor();
                }
                break;

            case Hulu:
                // 由三条数字、对子数字以及三条最大位花色确定权值
                // decimalResult = (cds[2].getNumber() == cds[0].getNumber()) ? (cds[2].getNumber() * 0.01 +
                // cds[3].getNumber() * 0.0001 + cds[2].getColor() * 0.00001)
                // : (cds[2].getNumber() * 0.01 + cds[1].getNumber() * 0.0001 + cds[2].getColor() * 0.00001);
                heavy = (cds[2].getNumber() == cds[0].getNumber()) ? ((cds[2].getNumber() << 6) + (cds[3].getNumber() << 2) + cds[2].getColor())
                        : ((cds[2].getNumber() << 6) + (cds[1].getNumber() << 2) + cds[2].getColor());
                break;

            case WuTiao:
                // 由最大位数字即可确定
                // decimalResult = cds[0].getNumber() * 0.01;
                heavy = cds[0].getNumber();
                break;
            default:
                break;
        } // end of switch(resultType)

        System.out.println("牌型为:" + resultType);
        result = (resultType.ordinal() << 22) + heavy;
        return result;
    }

    /**
     * 判断牌型是否为顺子
     *
     * @param cds       传入牌的数组引用，长度为5
     * @param normalNum 非鬼牌的数量
     * @return 若为顺子，返回true
     */
    public static boolean isShunZi(Card[] cds, int normalNum) {

        // 判断是否为A2345的情况
        if (cds[0].getNumber() == 14 && cds[1].getNumber() <= 5) {

            // 传入数组为降序排列，A大小表示为14，若存在A，一定处于数组首位，且顺子及同花顺的判断最多有三个鬼（即普通牌至少两张），否则牌型为五条或者五鬼，不会进行到是否为顺子的判断流程
            // A当做1处理
            if (cds[1].getNumber() - 1 <= cds.length - 1) {
                return true;
            }
        } else {
            if (cds[0].getNumber() - cds[normalNum - 1].getNumber() <= cds.length - 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 剩余牌数量重置
     */
    public void resetCardDeck() {
        this.restCardLength = CardDeck.DECK_SIZE;
    }

    /**
     * 获取Card数组的引用
     *
     * @return Card数组的引用
     */
    public Card[] getCards() {
        return this.cards;
    }

    /**
     * 洗牌 从数组第一位开始，随机random一个数字，第一位和随机出的那位交换， 然后依次遍历这个过程到最后一张牌。时间复杂度O(n)，空间复杂度O(1)
     */
    public void shuffle() {

        // 随机位数索引
        int tmpIndex;

        // 临时存储数组当前值
        Card tmpCard;
        for (int i = 0; i < CardDeck.DECK_SIZE; i++) {
            tmpIndex = (int) (Math.random() * CardDeck.DECK_SIZE);
            if (tmpIndex != i) {
                tmpCard = cards[tmpIndex];
                cards[tmpIndex] = cards[i];
                cards[i] = tmpCard;
            }
        }
    }

    /**
     * 发牌 随机random一个数字，当前位即为发出去的牌；剩余牌的数量减1。
     *
     * @return 所发牌的内存地址
     */
    public Card deal() {

        // 随机位数索引
        int tmpIndex = (int) (Math.random() * this.restCardLength);

        // 随机位与数组剩余牌最后一位交换，数组长度减1
        Card tmpCard = cards[tmpIndex];
        cards[tmpIndex] = cards[restCardLength - 1];
        cards[restCardLength - 1] = tmpCard;
        this.restCardLength--;
        return cards[this.restCardLength];
    }
}
