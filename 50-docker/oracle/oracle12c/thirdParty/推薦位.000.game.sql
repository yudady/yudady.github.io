declare
    type t_array is varray (40) of varchar2(50);
    deviceType    t_array := t_array('ALL', 'ALL', 'ALL', 'WEB', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL',
                                     'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL', 'ALL',
                                     'ALL', 'ALL', 'ALL', 'ALL', 'WEB', 'ALL', 'ALL', 'ALL', 'WEB', 'WEB', 'ALL', 'ALL',
                                     'ALL', 'ALL', 'ALL', 'ALL');
    gameId        t_array := t_array('2407', '2472', '2479', '2515', '2520', '2541', '2601', '2604', '2608', '2648',
                                     '2650', '2667', '2670', '2671', '2673', '2679', '2684', '2702', '2785', '2786',
                                     '2810', '2816', '2871', '2882', '2915', '2918', '2921', '2928', '2962', '2967',
                                     '2815', '2570', '2630', '2638', '2727', '2735', '6184', '2896', '2836');
    enGameName    t_array := t_array('Alaskan Fishing', 'Carnaval', 'Cashapillar', 'Dino Might (Betstone)',
                                     'Dolphin Quest', 'EmotiCoins', 'Gold Factory', 'Golden Era', 'Golden Princess',
                                     'Huangdi - The Yellow Emperor', 'Immortal Romance', 'Jungle Jim - El Dorado',
                                     'Jurassic World', 'Karaoke Party', 'Kathmandu', 'King Tusk', 'Ladies Nite',
                                     'Lucky Firecracker', 'Pistoleras', 'Playboy', 'Reel Gems',
                                     'Retro Reels - Extreme Heat', 'Spring Break', 'Sun Quest',
                                     'The Phantom of the Opera', 'The Twisted Circus', 'Thunderstruck II',
                                     'Tomb Raider', 'White Buffalo', 'Wild Scarabs', 'Retro Reels - Diamond Glitz',
                                     'Fortunium', 'Hell''s Grannies', 'Ho Ho Ho', 'Mega Money Rush', 'MOBY DICK',
                                     'Book of Oz: Lock n Spin', 'Tally Ho', 'Santa''s Wild Ride');
    cnGameName    t_array := t_array('阿拉斯加钓鱼', '狂欢节', '虫虫派对', '侏罗纪公园', '海豚探秘', '表情金币', '黄金工厂', '黄金时代', '黄金公主', '轩辕帝传',
                                     '不朽的浪漫', '丛林吉姆-黄金国', '侏罗纪世界', 'K歌乐韵', '加德满都', '大象之王', '女士之夜', '招财鞭炮', '持枪王者',
                                     '花花公子', '宝石迷阵', '热火老虎机', '海滩度假', '探索黄金', '歌剧魅影', '翻转马戏团', '雷神之锤2', '古墓丽影', '白色水牛',
                                     '百搭圣甲虫', '钻石老虎机', '财富之都', '地狱奶奶', '哈哈哈', '巨款大冲击', '白鲸记', 'Oz之书', '纸牌之战', '圣诞狂飙');
    twGameName    t_array := t_array('阿拉斯加垂釣', '嘉年華', '昆蟲派對', '侏羅紀公園', '海豚探秘', '表情金幣', '黃金工廠', '黃金時代', '黃金公主', '軒轅帝傳',
                                     '不朽情緣', '叢林吉姆-黃金國', '侏羅紀世界', 'K歌樂韻', '加德滿都', '大象之王', '冒險叢林', '招財鞭炮', '持槍王者',
                                     '花花公子', '寶石轉軸', '熱火老虎機', '冒險叢林', '俠盜獵車手', '歌劇魅影', '奇妙馬戲團', '雷霆萬鈞2', '百萬人魚', '白色水牛',
                                     '百搭聖甲蟲', '鑽石老虎機', '財富之都', '地獄奶奶', '呵呵呵', '鉅款大衝擊', '白鯨記', 'Oz之書', '泰利呵', '聖誕狂飆');
    gameTheme     t_array := t_array('Theme_Animal', 'Theme_Mix', 'Theme_Animal', 'Theme_Movie', 'Theme_Animal',
                                     'Theme_Mix', 'Theme_Treasure', 'Theme_Mix', 'Theme_Treasure', 'Theme_Myth',
                                     'Theme_Mix', 'Theme_Treasure', 'Theme_Movie', 'Theme_Mix', 'Theme_Mix',
                                     'Theme_Mix', 'Theme_Mix', 'Theme_ChineseStyle', 'Theme_Mix', 'Theme_Mix',
                                     'Theme_Treasure', 'Theme_Fruit', 'Theme_Mix', 'Theme_Fruit', 'Theme_Movie',
                                     'Theme_Animal', 'Theme_Movie', 'Theme_Movie', 'Theme_ChineseStyle',
                                     'Theme_Treasure', 'Theme_Fruit', 'Theme_Treasure', 'Theme_Myth', 'Theme_Myth',
                                     'Theme_Mix', 'Theme_Treasure', 'Theme_Mix', 'Theme_Mix', 'Theme_Mix');
    gameMinBet    t_array := t_array('MinBet_01', 'MinBet_1', 'MinBet_01', 'MinBet_1', 'MinBet_005', 'MinBet_1',
                                     'MinBet_01', 'MinBet_001', 'MinBet_01', 'MinBet_01', 'MinBet_05', 'MinBet_01',
                                     'MinBet_01', 'MinBet_001', 'MinBet_01', 'MinBet_01', 'MinBet_01', 'MinBet_01',
                                     'MinBet_05', 'MinBet_01', 'MinBet_01', 'MinBet_01', 'MinBet_01', 'MinBet_01',
                                     'MinBet_01', 'MinBet_01', 'MinBet_1', 'MinBet_01', 'MinBet_1', 'MinBet_001',
                                     'MinBet_001', 'MinBet_001', 'MinBet_001', 'MinBet_01', 'MinBet_1', 'MinBet_01',
                                     'MinBet_001', 'MinBet_005', 'MinBet_05', 'MinBet_05');
    gameLineCount t_array := t_array('LineCount_40_Line', 'LineCount_1_10_Line', 'LineCount_40_Line',
                                     'LineCount_25_30_Line', 'LineCount_40_Line', 'LineCount_25_30_Line',
                                     'LineCount_40_Line', 'LineCount_12_20_Line', 'LineCount_25_30_Line',
                                     'LineCount_25_30_Line', 'LineCount_40_Line', 'LineCount_25_30_Line',
                                     'LineCount_25_30_Line', 'LineCount_1_10_Line', 'LineCount_1_10_Line',
                                     'LineCount_25_30_Line', 'LineCount_1_10_Line', 'LineCount_40_Line',
                                     'LineCount_25_30_Line', 'LineCount_40_Line', 'LineCount_40_Line',
                                     'LineCount_25_30_Line', 'LineCount_1_10_Line', 'LineCount_1_10_Line',
                                     'LineCount_40_Line', 'LineCount_40_Line', 'LineCount_40_Line',
                                     'LineCount_12_20_Line', 'LineCount_25_30_Line', 'LineCount_40_Line',
                                     'LineCount_25_30_Line', 'LineCount_40_Line', 'LineCount_25_30_Line',
                                     'LineCount_12_20_Line', 'LineCount_1_10_Line', 'LineCount_40_Line',
                                     'LineCount_40_Line', 'LineCount_1_10_Line', 'LineCount_25_30_Line',
                                     'LineCount_1_10_Line');
    SEQ_GID       NUMBER ;

begin


    for i in 1..gameId.count
        loop

            dbms_output.put_line(i || '  ' ||
                                 gameId(i) || '  ' || enGameName(i) || '  ' || cnGameName(i) || '  ' ||
                                 twGameName(i) ||
                                 '  ' ||
                                 gameTheme(i));

            ----------------------------------
            ----------------------------------
            ----------------------------------


            select CORE.seq_lott_group_series.nextval into SEQ_GID from dual;

            INSERT INTO CORE.LOTT_GROUP_SERIES (GID, GROUP_ID, GROUP_VALUE, GROUP_TYPE, REMARK, PARAM_ONE, PARAM_TWO,
                                                CREATE_TIME, UPDATE_TIME, DEVICE_TYPE)
            VALUES (SEQ_GID, 10, gameId(i), 3, gameId(i), null, null, sysdate, sysdate, deviceType(i));

            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameSwitch', '0', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameSwitch', null, 2, 1, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameSwitchVip', '0', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameSwitchVip', null, 2, 2, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameName_en-US', enGameName(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameName_en-US', null, 2, 3, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameName_zh-CN', cnGameName(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameName_zh-CN', null, 2, 4, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameName_zh-TW', twGameName(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameName_zh-TW', null, 2, 5, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'ExternalGameName', gameId(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'ExternalGameName', null, 2, 6, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'SupportLanguage', 'en-US,zh-CN,zh-TW', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'SupportLanguage', null, 2, 7, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameInformation', ' ', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameInformation', null, 2, 8, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameTheme', gameTheme(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameTheme', null, 2, 9, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameMinBet', gameMinBet(i), 'text', null, null, null, null, null,
                    null, null, null, null, null, 'GameMinBet', null, 2, 10,
                    sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'GameLineCount', gameLineCount(i), 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'GameLineCount', null, 2, 11, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'IsNew', '0', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'IsNew', null, 2, 12, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'AndroidSwitch', '0', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'AndroidSwitch', null, 2, 13, sysdate, sysdate, 10, SEQ_GID);
            INSERT INTO CORE.LOTT_PARA_ALL (GID, GROUPNAMES, SUBGROUPNAMES, PARAMNAMES, PARAMVALUES, COLUMNTYPES,
                                            MAKERS,
                                            MAKETIMES, CHECKERS, CHECKTIMES, READROLES, CHECKROLES, WRITEROLES,
                                            PARAMVALUES_MODIFY, APPLYERS, APPLYTIMES, COMMENTS, GROUPNAMESEN,
                                            PARAM_STATE,
                                            ORDER_ID, CREATE_TIME, UPDATE_TIME, TGI_ID, CASUAL_GAME_ID)
            VALUES (CORE.seq_lott_para_all.nextval, SEQ_GID || '_LotteryParameterSetting', null,
                    'IOSSwitch', '0', 'text',
                    null, null, null, null, null, null, null, null, null, null,
                    'IOSSwitch', null, 2, 14, sysdate, sysdate, 10, SEQ_GID);

        end loop;
end;


commit;






