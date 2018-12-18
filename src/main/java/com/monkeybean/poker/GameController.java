package com.monkeybean.poker;

import java.util.Scanner;

/**
 * 梭哈棋牌游戏，游戏流程控制类
 * 玩家与机器人对战及机器人与机器人对战模拟
 *
 * CreateTime: 2017-03-30
 * Last Modify: 2017-04-07
 */
public class GameController {
    /**
     * 当前已发牌的数量
     */
    private static int haveDealNum = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // 流程控制
        String processControl;

        // 循环控制
        boolean loopFlag = true;
        CardDeck deck = new CardDeck();
        Player player1;
        Player player2;

        System.out.println("--------------梭哈牌局---------------");
        System.out.println("------1.请选择模式：人机对战输入1，机器对战输入2，输入其他字符则结束游戏-------");

        processControl = in.next();
        if (!processControl.equals("1") && !processControl.equals("2")) {

            // 测试玩家手牌权值
//			testPlayerCard();
            testRobot();
            in.close();
            System.out.println("游戏结束");
            return;
        } else if (processControl.equals("1")) {
            player1 = new Player(DataConfig.INIT_GOLD_NUM, false);
            player2 = new Player(DataConfig.INIT_GOLD_NUM, true);
            while (loopFlag) {
                System.out.println("2.是否开启游戏y/n");
                processControl = in.next();
                if (processControl.toLowerCase().equals("y")) {

                    // 判断双方是否有足够金币开始一局游戏
                    if (player1.getGoldNum() < DataConfig.INIT_BET) {
                        System.out.println("3.玩家1金币不足，游戏结束");
                        in.close();
                        return;
                    } else if (player2.getGoldNum() < DataConfig.INIT_BET) {
                        System.out.println("3.玩家2金币不足，游戏结束");
                        in.close();
                        return;
                    }

                    // 洗牌
                    deck.shuffle();
                    System.out.println("洗牌后,整副扑克顺序如下：");
                    CardDeck.printCards(deck.getCards(), CardDeck.getDeckSize(), true);

                    System.out.println("-----发牌-----");

                    // 初始两个角色各发两张牌
                    while (GameController.haveDealNum < 2) {
                        player1.getOneCard(deck.deal());
                        player2.getOneCard(deck.deal());
                        GameController.haveDealNum++;
                    }

                    // 根据玩家选择发牌
                    while (GameController.haveDealNum < 5) {
                        System.out.println("玩家1当前手牌");
                        CardDeck.printCards(player1.getMyCards(), player1.getCurCardNum(), true);

                        System.out.println("玩家2当前手牌");
                        CardDeck.printCards(player2.getMyCards(), player2.getCurCardNum(), false);

                        System.out.println("输入 \"Y\" 继续发牌， 输入 \"R 钱数\"加注，输入\"N\"放弃这轮，对方直接获胜");

                        processControl = in.next();
                        if (processControl.toLowerCase().equals("y")) {
                            player1.getOneCard(deck.deal());
                            player2.getOneCard(deck.deal());
                            GameController.haveDealNum++;
                        } else if (processControl.toLowerCase().equals("r")) {
                            int addNum = Integer.parseInt(in.next());
                            if (DataConfig.betTotal + addNum <= player1.getGoldNum()) {
                                if (DataConfig.betTotal + addNum > player2.getGoldNum()) {
                                    System.out.println("玩家2金币不足，此次加注不生效，按跟注处理");
                                    addNum = 0;
                                }
                            } else {
                                System.out.println("玩家1金币不足，此次加注不生效，按跟注处理");
                                addNum = 0;
                            }

                            DataConfig.betTotal += addNum;
                            player1.getOneCard(deck.deal());
                            player2.getOneCard(deck.deal());
                            GameController.haveDealNum++;
                        } else {
                            System.out.println("3.游戏结束，玩家2获胜");
                            player1.addGoldNum(-DataConfig.betTotal);
                            player2.addGoldNum(DataConfig.betTotal);
                            break;
                        } // end of if(processControl.toLowerCase().equals("y"))
                    } // end of while(GameController.haveDealNum < 5)

                    // 比较两玩家手牌权值大小（当玩家没有中止游戏，即已有五张手牌时进行判断）
                    if (GameController.haveDealNum == 5) {
                        if (CompareCards(player1, player2) > 0) {
                            player1.addGoldNum(DataConfig.betTotal);
                            player2.addGoldNum(-DataConfig.betTotal);
                        } else {
                            player1.addGoldNum(-DataConfig.betTotal);
                            player2.addGoldNum(DataConfig.betTotal);
                        }
                    } // end of if(GameController.haveDealNum == 5)
                } else {
                    System.out.println("3.游戏结束");
                    in.close();
                    return;
                } // end of if(processControl.toLowerCase().equals("y"))

                // 下局游戏开始前输出双方损益
                System.out.println("玩家1：");
                player1.printBenefit();
                System.out.println("玩家2：");
                player2.printBenefit();

                // 流程控制及角色、牌变量重置
                GameController.resetGameValue();
                player1.resetCardNum();
                player2.resetCardNum();
                deck.resetCardDeck();

            } // end of while(loopFlag)
        } else {

            // 已进行的牌局数
            int gameTime = 0;

            // 每局游戏中的选择权，1为选择权在角色1,2为选择权在角色2
            int chooseFlag = 1;

            // 记录每次发牌前的判断结果
            ChooseType cType;

            player1 = new Player(DataConfig.INIT_GOLD_NUM, true);
            player2 = new Player(DataConfig.INIT_GOLD_NUM, true);

            while (loopFlag) {
                if (DataConfig.isRobotTest) {

                    // 测试
                    processControl = "y";
                } else {

                    // 正常运行
                    System.out.println("2.是否进行一次机器人对战y/n");
                    processControl = in.next();
                }
                if (processControl.toLowerCase().equals("y")) {

                    // 判断双方是否有足够金币开始一局游戏
                    if (player1.getGoldNum() < DataConfig.INIT_BET) {
                        System.out.println("3.机器人1金币不足，游戏结束");
                        in.close();
                        return;
                    } else if (player2.getGoldNum() < DataConfig.INIT_BET) {
                        System.out.println("3.机器人2金币不足，游戏结束");
                        in.close();
                        return;
                    }

                    // 牌局次数计数
                    gameTime++;

                    // 测试次数判断
                    if (DataConfig.isRobotTest) {
                        if (gameTime > DataConfig.robotTestNum) {
                            System.out.println("4.机器人对战测试结束");
                            in.close();
                            return;
                        }
                    }
                    System.out.printf("\n第%d局：", gameTime);

                    // 玩家1和玩家2选择权每局轮换一次
                    if (gameTime % 2 == 1) {
                        chooseFlag = 1;
                    } else {
                        chooseFlag = 2;
                    }
                    System.out.printf("选择权在玩家 %d ", chooseFlag);

                    // 洗牌
                    deck.shuffle();
                    System.out.println("洗牌后,整副扑克顺序如下：");
                    CardDeck.printCards(deck.getCards(), CardDeck.getDeckSize(), true);
                    System.out.println("-----发牌-----");

                    // 初始两个角色各发两张牌
                    while (GameController.haveDealNum < 2) {
                        player1.getOneCard(deck.deal());
                        player2.getOneCard(deck.deal());
                        GameController.haveDealNum++;
                    }

                    while (GameController.haveDealNum < 5) {

                        // 选择权在玩家1
                        if (chooseFlag == 1) {
                            cType = Player.robotJudge(player1, player2, haveDealNum);

                            if (cType == ChooseType.FirstWin) {
                                player1.addGoldNum(DataConfig.betTotal);
                                player2.addGoldNum(-DataConfig.betTotal);
                                System.out.println("---玩家2弃牌，玩家1获胜---");
                            } else if (cType == ChooseType.SecondWin) {
                                player1.addGoldNum(-DataConfig.betTotal);
                                player2.addGoldNum(DataConfig.betTotal);
                                System.out.println("---玩家1弃牌，玩家2获胜---");
                            }
                        } else {
                            cType = Player.robotJudge(player2, player1, haveDealNum);
                            if (cType == ChooseType.FirstWin) {
                                player1.addGoldNum(-DataConfig.betTotal);
                                player2.addGoldNum(DataConfig.betTotal);
                                System.out.println("---玩家1弃牌，玩家2获胜---");
                            } else if (cType == ChooseType.SecondWin) {
                                player1.addGoldNum(DataConfig.betTotal);
                                player2.addGoldNum(-DataConfig.betTotal);
                                System.out.println("---玩家2弃牌，玩家1获胜---");
                            }
                        }

                        // 游戏因某个玩家弃牌提前中止
                        if (cType != ChooseType.Deal) {
                            System.out.println("---玩家1手牌---");
                            CardDeck.printCards(player1.getMyCards(), haveDealNum, true);
                            System.out.println("---玩家2手牌---");
                            CardDeck.printCards(player2.getMyCards(), haveDealNum, true);
                            break;
                        }
                        player1.getOneCard(deck.deal());
                        player2.getOneCard(deck.deal());
                        GameController.haveDealNum++;
                        System.out.println("当前手牌数：" + GameController.haveDealNum);

                    }

                    // 比较机器人五张手牌权值大小
                    if (GameController.haveDealNum == 5) {
                        if (CompareCards(player1, player2) > 0) {
                            player1.addGoldNum(DataConfig.betTotal);
                            player2.addGoldNum(-DataConfig.betTotal);
                        } else {
                            player1.addGoldNum(-DataConfig.betTotal);
                            player2.addGoldNum(DataConfig.betTotal);
                        }
                    }

                    // 下局游戏开始前输出双方损益
                    System.out.println("玩家1：");
                    player1.printBenefit();
                    System.out.println("玩家2：");
                    player2.printBenefit();

                    // 流程控制及角色、牌变量重置
                    GameController.resetGameValue();
                    player1.resetCardNum();
                    player2.resetCardNum();
                    deck.resetCardDeck();
                } else {
                    in.close();
                    System.out.println("3.游戏结束");
                    return;
                }
            }
        } // end of if(processControl.charAt(0) != 49 && processControl.charAt(0) != 50)

        in.close();
    }

    /**
     * 比较两玩家手牌大小
     *
     * @param player1 玩家1
     * @param player2 玩家2
     * @return player1的手牌大于player2的手牌则返回1，否则返回-1
     */
    public static int CompareCards(Player player1, Player player2) {
        System.out.println("玩家1：");
        int cardValue1 = CardDeck.getType(player1.getMyCards());
        ControlTools.printOut("手牌权值为：" + cardValue1);

        System.out.println("玩家2：");
        int cardValue2 = CardDeck.getType(player2.getMyCards());
        ControlTools.printOut("手牌权值为：" + cardValue2);
        if (cardValue1 > cardValue2) {
            System.out.println("---玩家1获胜----");
            return 1;
        } else {
            System.out.println("---玩家2获胜---");
            return -1;
        }
    }

    /**
     * 测试函数，输出给定牌权值
     */
    public static void testPlayerCard() {
        Player testPlayer = new Player(DataConfig.INIT_GOLD_NUM, false);
        testPlayer.getOneCard(new Card(14, 1));
        testPlayer.getOneCard(new Card(10, 1));
        testPlayer.getOneCard(new Card(0, 0));
        testPlayer.getOneCard(new Card(11, 4));
        testPlayer.getOneCard(new Card(13, 3));

        // 同花顺
        // for (int i = 0; i < 5; i++)
        // {
        // testPlayer.getOneCard(new Card(i + 2, 1));
        // }

        System.out.println("测试玩家当前手牌");
        CardDeck.printCards(testPlayer.getMyCards(), testPlayer.getCurCardNum(), true);
        System.out.println("测试玩家当前手牌权值");
        System.out.println(CardDeck.getType(testPlayer.getMyCards()));
    }

    /**
     * 测试函数，输出机器人判断策略
     */
    public static void testRobot() {
        Player robot1 = new Player(DataConfig.INIT_GOLD_NUM, true);
        Player robot2 = new Player(DataConfig.INIT_GOLD_NUM, true);

        robot1.getOneCard(new Card(9, 2));
        robot1.getOneCard(new Card(10, 1));
        robot1.getOneCard(new Card(5, 3));
        robot1.getOneCard(new Card(11, 1));

        robot2.getOneCard(new Card(0, 0));
        robot2.getOneCard(new Card(10, 3));
        robot2.getOneCard(new Card(8, 2));
        robot2.getOneCard(new Card(12, 1));

        ChooseType cType = Player.robotJudge(robot1, robot2, 3);
        System.out.println(cType);
    }

    /**
     * 每局牌局开始前，控制变量重置
     */
    public static void resetGameValue() {
        GameController.haveDealNum = 0;
        DataConfig.betTotal = DataConfig.INIT_BET;
    }
}
