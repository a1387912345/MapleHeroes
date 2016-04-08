package handling;

import constants.ServerConfig;
import tools.FileoutputUtil;
import tools.HexTool;

public enum SendPacketOpcode implements WritableIntValueHolder {

	/*
     * General Opcodes
     * Used for general purposes.
     */
    PING((short) 0x12),//11            v146 - 0x12;   v170 - 0x18; v171 - 0x12
    AUTH_RESPONSE((short) 0x17), //15  v146 - 0x16;   v160 - 0x17;   v170 - 0x1D; v171 - 0x17
    /*
     * MapleTalk Opcodes
     */
    UNK_TALK((short) 0x01),
    PING_TALK((short) 0x0D),
    BUDDYCHAT((short) 0x13),
    /*
     * Login Opcodes
     * Used for login packets.
     * Opcode codes are ordered in the order in which they will be sent by the server or received by the client.
     * This will allow for ease in updating future opcodes.
     */
    CLIENT_AUTH((short) 0x2B), 
    CLIENT_RESPONSE((short) 0x2F),
    LOGIN_STATUS((short) 0x00),
    /*
     * World Select Opcodes
     */
    CHANGE_BACKGROUND((short) 0x34),//12B   v146 - 0x999   v170 - 0x31; v171.1 - 0x33; v171.2 - 0x34
    SERVERLIST((short) 0x01), // v146 - 0x09;   v160 - 0x09;    v170 - 0x07; v171 - 0x01
    ENABLE_RECOMMENDED((short) 0x02),//1C v146 - 0x1D  v170 - 0x08; v171 - 0x02
    SEND_RECOMMENDED((short) 0x03),//1D   v146 - 0x1E  v170 - 0x09; v171 - 0x03
    SERVERSTATUS((short) 0x25),  // v146 - 0x04; v170 - 0x02; v171 - 0x25
    /*
     * Character Select Opcodes
     */
    CHARLIST((short) 0x06), // v146 - 0x0A; v170 - 0x0C; v171 - 0x06
    CHAR_NAME_RESPONSE((short) 0x0A), // v146 - 0x0C; v170 - 0x10; v171 - 0x0A
    ADD_NEW_CHAR_ENTRY((short) 0x0B), // v146 - 0x0D; v170 - 0x11; v171 - 0x0B
    DELETE_CHAR_RESPONSE((short) 0x0C), //v146 - 0x0E; v170 - 0x12; v171 - 0x0C
    PIC_RESPONSE((short) 0x18), //v146 - 0x19; v171 - 0x18
    REGISTER_PIC_RESPONSE((short) 0x2A), // v146 - 0x1A; v170 - 0x1F; v171 - 0x2A
    SECONDPW_ERROR((short) 0x32), // v146 - 0x25; v170 - 0x2F; v171.3 - 0x32
    
    SERVER_IP((short) 0x07),// v146 - 0x0B; v170 - 0x0D; v171 - 0x07
    CHANGE_CHANNEL((short) 0x11),//10 v146 - 0x10; v171.3 - 0x11
    CS_USE((short) 0x14), // v146 - 0x13; v170 - 0x1A; v171 - 0x14
    
    // Char Select Opcodes - Not Updated Yet
    SEND_LINK((short) 0x01),
    LOGIN_SECOND((short) 0x02),
    CHANNEL_SELECTED((short) 0x02),
    GENDER_SET((short) 0x04),
    PIN_OPERATION((short) 0x05),
    PIN_ASSIGNED((short) 0x06),
    ALL_CHARLIST((short) 0x07),
    RELOG_RESPONSE((short) 0x17),//16
    PART_TIME((short) 0x1F),//1E
    SPECIAL_CREATION((short) 0x20),//1F
    
    /*
     * Channel Opcodes
     * Question marks (?) next to the latest opcode indicates it is an assumed opcode
     * which was updated through adding a difference, and it has not been tested yet.
     */
    INVENTORY_OPERATION((short) 0x42),//25 v146 - 0x26; v171 - 0x41; v171.2 - 0x42 ?
    INVENTORY_GROW((short) 0x43),//26 v146 - 0x27; v171 - 0x42; v171.2 - 0x43 ?
    UPDATE_STATS((short) 0x44),//27 v146 - 0x28; v171 - 0x43; v171.2 - 0x44
    GIVE_BUFF((short) 0x45),//28 v146 - 0x29; v171 - 0x44; v171.2 - 0x45
    CANCEL_BUFF((short) 0x46),//29 v146 - 0x2A; v171 - 0x45; v171.2 - 0x46
    TEMP_STATS((short) 0x47),//2A //v146 - 0x2B; v171 - 0x46; v171.2 - 0x47 ?
    TEMP_STATS_RESET((short) 0x48),//2B v146 - 0x2C; v171 - 047; v171.2 - 0x48 ?
    UPDATE_SKILLS((short) 0x49),//2C //v146 - 0x2D; v171 - 0x48; v171.2 - 0x49
    UPDATE_STOLEN_SKILLS((short) 0x4A),//2D v146 - 0x2E; v171 - 0x49; v171.2 - 0x4A ?
    TARGET_SKILL((short) 0x4B),//2E v146 - 0x2F; v171 - 0x4A; v171.2 - 0x4B ?
    FAME_RESPONSE((short) 0x53),//32 v146 - 0x33; v171.1 - 0x52; v171.2 - 0x53 ?
    SHOW_STATUS_INFO((short) 0x54),//33 // v146 - 0x34; v171.1 - 0x53; v171.2 - 0x54
    FULL_CLIENT_DOWNLOAD((short) 0x55),//34 v146 - 0x35; v171 - 0x54; v171.2 - 0x55 ?
    SHOW_NOTES((short) 0x170), //v146 - 0x36; v170 - 0x71; v171.1 - 0x16F; v171.2 - 0x170
    TROCK_LOCATIONS((short) 0x37),//36
    LIE_DETECTOR((short) 0x38),//37
    REPORT_RESPONSE((short) 0x3A),//39
    REPORT_TIME((short) 0x3B),//3A
    REPORT_STATUS((short) 0x3C),//3B
    UPDATE_MOUNT((short) 0x3E),//3d
    SHOW_QUEST_COMPLETION((short) 0x3F),//3E
    SEND_TITLE_BOX((short) 0x40),//3F
    USE_SKILL_BOOK((short) 0x41),//40
    SP_RESET((short) 0x42),//41
    AP_RESET((short) 0x43),//42
    DISTRIBUTE_ITEM((short) 0x44),//43
    EXPAND_CHARACTER_SLOTS((short) 0x45),//44
    FINISH_GATHER((short) 0x68),//4D // v146 - 0x4B; v171 - 0x67; v171.2 - 0x68
    FINISH_SORT((short) 0x69),//4B //v146 - 0x4C; v171 - 0x68; v171.2 - 0x69
    EXP_POTION((short) 0x43),
    REPORT_RESULT((short) 0x4E),
    TRADE_LIMIT((short) 0x50),
    UPDATE_GENDER((short) 0x51),//50
    BBS_OPERATION((short) 0x52),//51
    CHAR_INFO((short) 0x6C),//v146 - 0x55; v171.3 0x6C
    PARTY_OPERATION((short) 0x6D),//55 v146 - 0x56; v171.3 0x6D
    MEMBER_SEARCH((short) 0x59),//5A
    PARTY_SEARCH((short) 0x5A),//5A
    BOOK_INFO((short) 0x5B),//5A
    CODEX_INFO_RESPONSE((short) 0x5C),//5b,
    EXPEDITION_OPERATION((short) 0x5D),//5c
    BUDDYLIST((short) 0x76),//5d v146 - 0x5E; v171.1 - 0x75; v171.2 - 0x76
    GUILD_OPERATION((short) 0x60),//5F
    ALLIANCE_OPERATION((short) 0x61),//60
    SPAWN_PORTAL((short) 0x62),//61
    MECH_PORTAL((short) 0x63),//62
    ECHO_MESSAGE((short) 0x64),//63
    SERVERMESSAGE((short) 0x7E), //v146 - 0x66; v170 - 0x89; v171.1 - 0x7D; v171.2 - 0x7E
    ITEM_OBTAIN((short) 0x6A),//68
    PIGMI_REWARD((short) 0x6E),//6C
    OWL_OF_MINERVA((short) 0x6F),//6D
    OWL_RESULT((short) 0x70),//6E
    ENGAGE_REQUEST((short) 0x72),//6F
    ENGAGE_RESULT((short) 0x73),//70
    WEDDING_GIFT((short) 0x74),//71
    WEDDING_MAP_TRANSFER((short) 0x75),//72
    USE_CASH_PET_FOOD((short) 0x76),//74
    YELLOW_CHAT((short) 0x77),//75
    SHOP_DISCOUNT((short) 0x79),//76
    CATCH_MOB((short) 0x7A),//77
    MAKE_PLAYER_NPC((short) 0x7B),//78
    PLAYER_NPC((short) 0x7C),//79
    DISABLE_NPC((short) 0x7D),//7A
    GET_CARD((short) 0x7E),//7B
    CARD_UNK((short) 0x7F),//new143
    CARD_SET((short) 0x80),//7D
    BOOK_STATS((short) 0x81),//7E
    UPDATE_CODEX((short) 0x82),//7F
    CARD_DROPS((short) 0x83),//80
    FAMILIAR_INFO((short) 0x84),//81
    CHANGE_HOUR((short) 0x86),//83
    RESET_MINIMAP((short) 0x87),//84
    CONSULT_UPDATE((short) 0x88),//85
    CLASS_UPDATE((short) 0x89),//86
    WEB_BOARD_UPDATE((short) 0x8A),//87
    SESSION_VALUE((short) 0x8A),//88
    PARTY_VALUE((short) 0x8B),//89
    MAP_VALUE((short) 0x8C),//8A
    EXP_BONUS((short) 0x8D),//8C
    POTION_BONUS((short) 0x8E),//8D
    SEND_PEDIGREE((short) 0x90),//8E
    OPEN_FAMILY((short) 0x92),//143
    FAMILY_MESSAGE((short) 0x8E),
    FAMILY_INVITE((short) 0x999),
    FAMILY_JUNIOR((short) 0x93),//90
    SENIOR_MESSAGE((short) 0x94),//91
    FAMILY((short) 0x95),//94
    REP_INCREASE((short) 0x96),//95
    EVOLVING_ACTION((short) 0x98),
    FAMILY_LOGGEDIN((short) 0x99),//96
    FAMILY_BUFF((short) 0x9A),//97
    FAMILY_USE_REQUEST((short) 0x9B),//98
    LEVEL_UPDATE((short) 0xD7),//99 v146 - 0x9C; v171 - 0xD6; v171.2 - 0xD7
    MARRIAGE_UPDATE((short) 0x9D),//9A
    JOB_UPDATE((short) 0x43),//9B v146 - 0x9E; v171 - 0x42; v171.2 - 0x43
    MAPLE_TV_MSG((short) 0x8D),
    AVATAR_MEGA_RESULT((short) 0x107),//FF
    AVATAR_MEGA((short) 0x108),//100
    AVATAR_MEGA_REMOVE((short) 0x109),//101
    POPUP2((short) 0x9D),
    CANCEL_NAME_CHANGE((short) 0x9E),
    CANCEL_WORLD_TRANSFER((short) 0x9F),
    CLOSE_HIRED_MERCHANT((short) 0xA3),//A0
    GM_POLICE((short) 0xA4),//A1
    TREASURE_BOX((short) 0xA5),//A2
    NEW_YEAR_CARD((short) 0xA6),//A3
    RANDOM_MORPH((short) 0xA7),//A4
    CANCEL_NAME_CHANGE_2((short) 0xA9),//A6
    SLOT_UPDATE((short) 0x5C),//A7 //v146 - 0xAC; v170 - 0x5B; v171 - 0x5B; v171.2 - 0x5C
    FOLLOW_REQUEST((short) 0xAD),//A8
    TOP_MSG((short) 0xB3),//A9 v146 - 0xAE; v171 - 0xB2; v171.2 - 0xB3
    MID_MSG((short) 0xB2),//AB
    CLEAR_MID_MSG((short) 0xB3),//AC
    SPECIAL_MSG((short) 0xB4),//AD
    MAPLE_ADMIN_MSG((short) 0xB4),//AE
    CAKE_VS_PIE_MSG((short) 0xB4),//AF
    GM_STORY_BOARD((short) 0xB5),//B0
    INVENTORY_FULL((short) 0xB6),//B1
    UPDATE_JAGUAR((short) 0xB7),//B1
    YOUR_INFORMATION((short) 0xB9),//B2
    FIND_FRIEND((short) 0xBA),//B3
    VISITOR((short) 0xBB),//B4
    PINKBEAN_CHOCO((short) 0xBC),//B5
    PAM_SONG((short) 0xBD),//B6
    AUTO_CC_MSG((short) 0xBE),//b7
    DISALLOW_DELIVERY_QUEST((short) 0xC2),//bb
    ULTIMATE_EXPLORER((short) 0xC3),//BC
    SPECIAL_STAT((short) 0xC2), //also profession_info //BD v146 - 0xC4; v171.1 - 0xC1; v171.2 - 0xC2
    UPDATE_IMP_TIME((short) 0xC5),//BE
    ITEM_POT((short) 0xC6),//BF
    MULUNG_MESSAGE((short) 0xC9),//C2
    GIVE_CHARACTER_SKILL((short) 0xCA),//C3
    MULUNG_DOJO_RANKING((short) 0xCF),//C8
    UPDATE_INNER_ABILITY((short) 0xD4),//CD
    EQUIP_STOLEN_SKILL((short) 0xD5),//CE
    REPLACE_SKILLS((short) 0xD5),//CE
    INNER_ABILITY_MSG((short) 0xD6),//CF
    ENABLE_INNER_ABILITY((short) 0xD7),//D0
    DISABLE_INNER_ABILITY((short) 0xD8),//D1
    UPDATE_HONOUR((short) 0xD9),//D2
    AZWAN_UNKNOWN((short) 0xDA),//D3 //probably circulator shit?
    AZWAN_RESULT((short) 0xDB),//D4
    AZWAN_KILLED((short) 0xDC),//D5
    CIRCULATOR_ON_LEVEL((short) 0xDD),//D6
    SILENT_CRUSADE_MSG((short) 0xDE),//D7
    SILENT_CRUSADE_SHOP((short) 0xDF),//D8
    CASSANDRAS_COLLECTION((short) 0xEA),//new v145
    SET_OBJECT_STATE((short) 0xEF),//E8
    POPUP((short) 0xF0),//E9
    MINIMAP_ARROW((short) 0xF4),//ED
    UNLOCK_CHARGE_SKILL((short) 0xFA),//F2
    LOCK_CHARGE_SKILL((short) 0xFB),//F3
    CANDY_RANKING((short) 0xFF),//F8
    ATTENDANCE((short) 0x10A),//102
    MESSENGER_OPEN((short) 0x10B),//103
    EVENT_CROWN((short) 0x113),//105 v146 - 0x10D; v171.1 - 0x112; v171.2 - 0x113
    MAGIC_WHEEL((short) 0x125),//109
    REWARD((short) 0x52),//10B  //v146 - 0x126; v170 - 0x52
    SKILL_MACRO((short) 0x127),//10C //127 Outdated?
    WARP_TO_MAP((short) 0x19E), // v146 - 0x128; v170 - 0x19B; v171.1 - 0x19D; v171.2 - 0x19E
    FARM_OPEN((short) 0x999),//10E
    CS_OPEN((short) 0x12B),//110
    REMOVE_BG_LAYER((short) 0x12E),//111
    SET_MAP_OBJECT_VISIBLE((short) 0x12F),//112
    RESET_SCREEN((short) 0x12C),//12E?
    MAP_BLOCKED((short) 0x12D),//12F?
    SERVER_BLOCKED((short) 0x130),//etc
    PARTY_BLOCKED((short) 0x131),//etc
    SHOW_EQUIP_EFFECT((short) 0x132),//etc
    MULTICHAT((short) 0x1A6),    //v146 - 0x134; v171.3 - 0x1A6
    WHISPER((short) 0x1A8),      //v146 - 0x138; v171.3 - 0x1A8
    SPOUSE_CHAT((short) 0x1A9),  //v146 - 0x139; v171.3 - 0x1A9
    BOSS_ENV((short) 0x1AA),     //v146 - 0x13A; v171 - 0x1A9; v171.2 - 0x1AA
    MOVE_ENV((short) 0x1AB),     //v146 - 0x13B; v171 - 0x1AA; v171.2 - 0x1AB ?
    UPDATE_ENV((short) 0x1AD),   //v146 - 0x13C; v171 - 0x1AB; v171.2 - 0x1AC ?
    MAP_EFFECT((short) 0x1AE),   //v146 - 0x13E; v171 - 0x1AD; v171.2 - 0x1AE ?
    CASH_SONG((short) 0x14B),//v145 Guess
    GM_EFFECT((short) 0x141),//v145 , No d/c, but is it right?
    OX_QUIZ((short) 0x142),//v145 Guess
    GMEVENT_INSTRUCTIONS((short) 0x143),//v145, Confirmed!
    CLOCK((short) 0x144),//v145 Confirmed!
    BOAT_MOVE((short) 0x145),//v145 Guess
    BOAT_STATE((short) 0x147),//v145 Guess
    STOP_CLOCK((short) 0x148),//12D
    ARIANT_SCOREBOARD((short) 0x14A),//12F
    PYRAMID_UPDATE((short) 0x14E),//131
    PYRAMID_RESULT((short) 0x14F),//132
    QUICK_SLOT((short) 0x1BA),//134  //v146 - 0x153; v170 - 0x1B4; v171 - 0x1B9; v171.2 - 0x1BA
    SMART_MOB_NOTICE((short) 0x158),
    MOVE_PLATFORM((short) 0x156),//135
    PYRAMID_KILL_COUNT((short) 0x154),//137,
    PVP_INFO((short) 0x157),//136
    DIRECTION_STATUS((short) 0x17D), // v146 - 0x159;  v171 - 0x17C; v171.2 - 0x17D
    GAIN_FORCE((short) 0x15A),//CONFIRMED
    INTRUSION((short) 0x162),
    DIFFERENT_IP((short) 0x164),
    ACHIEVEMENT_RATIO((short) 0x159),//13B
    QUICK_MOVE((short) 0x1D1),// v146 - 0x15C; v171 - 0x1D0; v171.2 - 0x1D1
    SPAWN_OBTACLE_ATOM((short) 0x15D),
    SPAWN_PLAYER((short) 0x1EE), // v146 - 0x167; v170 - 0x1EB; v171 - 0x1ED; v171.2 - 0x1EE
    REMOVE_PLAYER_FROM_MAP((short) 0x1EF), // v146 - 0x168; ; v171.2 - 0x1EF
    CHATTEXT((short) 0x1F0), //v146 - 0x169; v171 - 0x1EF; v171.3 - 0x1F0
    CHATTEXT_1((short) 0x16A),//170
    CHALKBOARD((short) 0x16B),//171
    UPDATE_CHAR_BOX((short) 0x16C),//149
    SHOW_CONSUME_EFFECT((short) 0x16D),//14a
    SHOW_SCROLL_EFFECT((short) 0x16E),//14b
    SHOW_MAGNIFYING_EFFECT((short) 0x170),//14c
    SHOW_POTENTIAL_RESET((short) 0x171),//14d
    SHOW_FIREWORKS_EFFECT((short) 0x172),//14e
    SHOW_NEBULITE_EFFECT((short) 0x172),//14f
    SHOW_FUSION_EFFECT((short) 0x174),//150
    PVP_ATTACK((short) 0x140),
    PVP_MIST((short) 0x141),
    PVP_COOL((short) 0x142),
    TESLA_TRIANGLE((short) 0x999),//17E need the right v145 one plox..
    FOLLOW_EFFECT((short) 0x15D),
    SHOW_PQ_REWARD((short) 0x15F),
    CRAFT_EFFECT((short) 0x182),//15F
    CRAFT_COMPLETE((short) 0x183),//160
    HARVESTED((short) 0x185),//161
    PLAYER_DAMAGED((short) 0x165),
    NETT_PYRAMID((short) 0x166),
    SET_PHASE((short) 0x167),
    PAMS_SONG((short) 0x168),
    SPAWN_PET((short) 0x192),//+2
    SPAWN_PET_2((short) 0x194),//+2
    MOVE_PET((short) 0x195),//+2
    PET_CHAT((short) 0x196),//+2
    PET_NAMECHANGE((short) 0x197),//+2
    PET_EXCEPTION_LIST((short) 0x198),//+2
    PET_COLOR((short) 0x199),//+2
    PET_SIZE((short) 0x19A),//+2
    PET_COMMAND((short) 0x19B),//+2
    DRAGON_SPAWN((short) 0x19C),//+2
    INNER_ABILITY_RESET_MSG((short) 0x175),//+2
    DRAGON_MOVE((short) 0x19D),//+2
    DRAGON_REMOVE((short) 0x19E),//+2
    ANDROID_SPAWN((short) 0x19F),       //v146 - 0x19F; v171.3 - 0x235
    ANDROID_MOVE((short) 0x1A0),        //v146 - 0x1A0; v171.3 - 0x236
    ANDROID_EMOTION((short) 0x1A1),     //v146 - 0x1A1; v171.3 - 0x237
    ANDROID_UPDATE((short) 0x1A2),      //v146 - 0x1A2; v171.3 - 0x238
    ANDROID_DEACTIVATED((short) 0x1A3), //v146 - 0x1A3; v171.3 - 0x239
    SPAWN_FAMILIAR((short) 0x1AA),//+2
    MOVE_FAMILIAR((short) 0x1AB),//+2
    TOUCH_FAMILIAR((short) 0x1AC),//+2
    ATTACK_FAMILIAR((short) 0x1AD),//+2
    RENAME_FAMILIAR((short) 0x1AE),//+2
    SPAWN_FAMILIAR_2((short) 0x1AF),//+2
    UPDATE_FAMILIAR((short) 0x1B0),//+2
    HAKU_CHANGE_1((short) 0x1A4),//+2
    HAKU_CHANGE_0((short) 0x1A7),//+2
    HAKU_MOVE((short) 0x1B2),//+2
    HAKU_UNK((short) 0x1B3),//+2
    HAKU_CHANGE((short) 0x1B4),//+2
    SPAWN_HAKU((short) 0x1B7),//+2
    
    /*
     * Other Player Opcodes
     * These opcodes displays other players' character animations, effects, equips, and etc. 
     */
    MOVE_PLAYER((short) 0x259),          //v146 - 0x1BA; v171 - 0x258; v171.2 - 0x259
    CLOSE_RANGE_ATTACK((short) 0x25A),   //v146 - 0x1BC; v171.2 - 0x25A
    RANGED_ATTACK((short) 0x25B),        //v146 - 0x1BD; v171.2 - 0x25B
    MAGIC_ATTACK((short) 0x25C),         //v146 - 0x1BE; v171.2 - 0x25C
    ENERGY_ATTACK((short) 0x25D),        //v146 - 0x1BF; v171.2 - 0x25D ?
    SKILL_EFFECT((short) 0x25E),         //v146 - 0x1C0; v171.2 - 0x25E ?
    MOVE_ATTACK((short) 0x25F),          //v146 - 0x1C1; v171.2 - 0x25F ?
    CANCEL_SKILL_EFFECT((short) 0x260),  //v146 - 0x1C2; v171.2 - 0x260 ?
    DAMAGE_PLAYER((short) 0x261),        //v146 - 0x1C3; v171.3 - 0x261
    FACIAL_EXPRESSION((short) 0x262),    //v146 - 0x1C4; v171.3 - 0x262
    SHOW_EFFECT((short) 0x264),          //v146 - 0x1C6; v171.3 - 0x264 ?
    SHOW_TITLE((short) 0x266),           //v146 - 0x1C8; v171.3 - 0x266 ?
    ANGELIC_CHANGE((short) 0x267),       //v146 - 0x1C9; v171.3 - 0x267 ?
    SHOW_CHAIR((short) 0x26B),           //v146 - 0x1CC; v171.3 - 0x26B
    UPDATE_CHAR_LOOK((short) 0x26C),     //v146 - 0x1CD; v171.3 - 0x26C
    SHOW_FOREIGN_EFFECT((short) 0x26D),  //v146 - 0x1CE; v171.3 - 0x26D
    GIVE_FOREIGN_BUFF((short) 0x26E),    //v146 - 0x1CF; v171.3 - 0x26E ?
    CANCEL_FOREIGN_BUFF((short) 0x26F),  //v146 - 0x1D0; v171.3 - 0x26F ?
    UPDATE_PARTYMEMBER_HP((short) 0x270),//v146 - 0x1D1; v171.3 - 0x270 ? 
    LOAD_GUILD_NAME((short) 0x271),      //v146 - 0x1D2; v171.3 - 0x271 ?
    LOAD_GUILD_ICON((short) 0x272),      //v146 - 0x1D3; v171.3 - 0x272 ?
    LOAD_TEAM((short) 0x273),            //v146 - 0x1D4; v171.3 - 0x273 ?
    
    SHOW_HARVEST((short) 0x2BC),//1AE
    PVP_HP((short) 0x1D7),//1B0
    CANCEL_CHAIR((short) 0x215),//1BC v146 - 0x1E6; v171 - 0x214; v171.2 - 0x215
    DIRECTION_FACIAL_EXPRESSION((short) 0x287),//1BD v146 - 0x1E7; v171 - 0x286; v171.2 - 0x287 ?
    MOVE_SCREEN((short) 0x288),//1BE v146 - 0x1E8; v171 - 0x287; v171.2 - 0x288 ?
    SHOW_SPECIAL_EFFECT((short) 0x289),//1BF  v146 - 0x1E9; v171 - 0x288; v171.2 - 0x289
    CURRENT_MAP_WARP((short) 0x28A),//1C0 v146 - 0x1EA; v171 - 0x289; v171.2 - 0x28A ?
    MESOBAG_SUCCESS((short) 0x1EC),//1C2
    MESOBAG_FAILURE((short) 0x1EA),//1C3
    R_MESOBAG_SUCCESS((short) 0x1EB),//1C4
    R_MESOBAG_FAILURE((short) 0x1EC),//1C5
    MAP_FADE((short) 0x1F0),//1C6
    MAP_FADE_FORCE((short) 0x1F1),//1C7
    UPDATE_QUEST_INFO((short) 0x28E),//1C8 v146 - 0x1F2; v171.3 - 0x28E ?
    HP_DECREASE((short) 0x1F0),//1C9
    PLAYER_HINT((short) 0x1F2),//1CB
    PLAY_EVENT_SOUND((short) 0x1F3),//1CC
    PLAY_MINIGAME_SOUND((short) 0x1F4),//1CD
    MAKER_SKILL((short) 0x1F5),//1CE
    OPEN_UI((short) 0x298),//1D1 v146 - 0x1FB; v171 - 0x297; v171.2 - 0x298 ?
    OPEN_UI_OPTION((short) 0x29A),//1D3 v146 - 0x1FD; v171 - 0x299; v171.2 - 0x29A ?
    INTRO_LOCK((short) 0x29B),//1D4 v146 - 0x1FE; v171 - 0x29A; v171.2 - 0x29B
    INTRO_ENABLE_UI((short) 0x29C), // v146 - 0x1FF; v171 - 0x29B; v171.2 - 0x29C
    INTRO_DISABLE_UI((short) 0x29D),//1D6 v146 - 0x200; v171 - 0x29C; v171.2 - 0x29D ?
    SUMMON_HINT((short) 0x29E),//1D7 v146 - 0x201; v171 - 0x29D; v171.2 - 0x29E ?
    SUMMON_HINT_MSG((short) 0x29F),//1D8 v146 - 0x202; v171 - 0x29E; v171.2 - 0x29F ?
    ARAN_COMBO((short) 0x2A0),//1D9 v146 - 0x203; v171 - 0x29F; v171.2 - 0x2A0 ?
    ARAN_COMBO_RECHARGE((short) 0x2A1),//1DA v146 - 0x204; v171 - 0x2A0; v171.2 - 0x2A1 ?
    RANDOM_EMOTION((short) 0x205),//1DB
    RADIO_SCHEDULE((short) 0x206),//1DC
    OPEN_SKILL_GUIDE((short) 0x207),//1DD
    GAME_MSG((short) 0x209),//1DF
    GAME_MESSAGE((short) 0x2A8),//1E0 v146 - 0x20A; v171.3 - 0x2A8
    BUFF_ZONE_EFFECT((short) 0x20C),//1E2
    GO_CASHSHOP_SN((short) 0x20D),//1E3
    DAMAGE_METER((short) 0x20E),//1E4
    TIME_BOMB_ATTACK((short) 0x20F),//1E5
    FOLLOW_MOVE((short) 0x210),//1E6
    FOLLOW_MSG((short) 0x211),//1E7
    AP_SP_EVENT((short) 0x215),//1E9
    QUEST_GUIDE_NPC((short) 0x214),//1EA
    REGISTER_FAMILIAR((short) 0x218),//1F1
    FAMILIAR_MESSAGE((short) 0x219),//1F2
    CREATE_ULTIMATE((short) 0x21C),//1F3
    HARVEST_MESSAGE((short) 0x21E),//1F5
    SHOW_MAP_NAME((short) 0x999),
    OPEN_BAG((short) 0x21D),//18B
    DRAGON_BLINK((short) 0x21E),//18C
    PVP_ICEGAGE((short) 0x219),//18D
    DIRECTION_INFO((short) 0x2B6),//18E v146 - 0x223; v171 - 02B5; v171.2 - 0x2B6
    REISSUE_MEDAL((short) 0x222),//18F
    PLAY_MOVIE((short) 0x227),//1FD
    CAKE_VS_PIE((short) 0x225),//1FE
    PHANTOM_CARD((short) 0x229),//1FF
    LUMINOUS_COMBO((short) 0x22A),//202
    MOVE_SCREEN_X((short) 0x199),//199
    MOVE_SCREEN_DOWN((short) 0x19A),//19A
    CAKE_PIE_INSTRUMENTS((short) 0x19B),//
    COOLDOWN((short) 0x269),//was 263
    SPAWN_SUMMON((short) 0x26B),//+5
    REMOVE_SUMMON((short) 0x26C),//+5 // v146 - 0x26C; v171.3 - 0x34D ?
    MOVE_SUMMON((short) 0x26D),//+5
    SUMMON_ATTACK((short) 0x26E),//+5
    PVP_SUMMON((short) 0x26F),//+5
    SUMMON_SKILL((short) 0x270),//+5
    SUMMON_SKILL_2((short) 0x271),//+5
    SUMMON_DELAY((short) 0x272),//+5
    DAMAGE_SUMMON((short) 0x273),//+5
    
    SPAWN_MONSTER((short) 0x35B),//23D v146 - 0x277; v171 - 0x35A; v171.2 - 0x35B
    KILL_MONSTER((short) 0x35C),//23E v146 - 0x278; v171 - 0x35B; v171.2 - 0x35C
    SPAWN_MONSTER_CONTROL((short) 0x35D),//23F v146 - 0x279; v171 - 0x35C; v171.2 - 0x35D
    MOVE_MONSTER((short) 0x361),//241 v146 - 0x27B; v171 - 0x360; v171.2 - 0x361
    MOVE_MONSTER_RESPONSE((short) 0x362),//242 v171 - 0x361; v171.2 - 0x362
    APPLY_MONSTER_STATUS((short) 0x364),//244 v146 - 0x27E; v171 - 0x363; v171.2 - 0x364 ?
    CANCEL_MONSTER_STATUS((short) 0x365),//245 v146 - 0x27F; v171 - 0x364; v171.2 - 0x365 ?
    DAMAGE_MONSTER((short) 0x36C),//248 v146 - 0x282; v171 - 0x36B; v171.2 - 0x36C ?
    SKILL_EFFECT_MOB((short) 0x36D),//249 v146 - 0x283; v171 - 36C; v171.2 - 0x36D ?
    TELE_MONSTER((short) 0x999), //todo sniff
    MONSTER_SKILL((short) 0x369), //todo sniff
    MONSTER_CRC_CHANGE((short) 0x36A),//24B
    SHOW_MONSTER_HP((short) 0x36C),//24C v146 - 0x286; v171 - 0x36B; v171.2 - 0x36C
    SHOW_MAGNET((short) 0x287),//24D
    ITEM_EFFECT_MOB((short) 0x288),//24E
    CATCH_MONSTER((short) 0x289),//24F
    MONSTER_PROPERTIES((short) 0x1B9),
    REMOVE_TALK_MONSTER((short) 0x1BA),
    TALK_MONSTER((short) 0x28A),
    CYGNUS_ATTACK((short) 0x28F),
    MONSTER_RESIST((short) 0x290),//
    MOB_REACTION((short) 0x291), // v146 - 0x291; v171 - 0x38E; v171.2 -  ?
    MOB_TO_MOB_DAMAGE((short) 0x1C6),
    AZWAN_MOB_TO_MOB_DAMAGE((short) 0x1C9),
    AZWAN_SPAWN_MONSTER((short) 0x22C),//1CA /0x22b?
    AZWAN_KILL_MONSTER((short) 0x22D),//1CB
    AZWAN_SPAWN_MONSTER_CONTROL((short) 0x999),//1CC
    
    SPAWN_NPC((short) 0x39C),//268 //v146 - 0x2A2; v171 - 0x39B; v171.2 - 0x39C
    REMOVE_NPC((short) 0x39D),//269 //v146 - 0x2A3; v171 - 0x39C; v171.2 - 0x39D?
    SPAWN_NPC_REQUEST_CONTROLLER((short) 0x39F),//26B //v146 - 0x29F; v171 - 0x39E; v171.2 - 0x39F
    NPC_ACTION((short) 0x3A0),//26C v146 - 0x2A0; v171 - 0x39F; v171.2 - 0x3A0
    NPC_TOGGLE_VISIBLE((short) 0x2A1),//26D
    INITIAL_QUIZ((short) 0x2A3),//26F
    NPC_UPDATE_LIMITED_INFO((short) 0x2A4),//270
    NPC_SET_SPECIAL_ACTION((short) 0x2A5),//271
    NPC_SCRIPTABLE((short) 0x2A6),//272
    RED_LEAF_HIGH((short) 0x2A7),//273
    SPAWN_HIRED_MERCHANT((short) 0x2B1),//277
    DESTROY_HIRED_MERCHANT((short) 0x2B2),//278
    UPDATE_HIRED_MERCHANT((short) 0x2B3),//279
    DROP_ITEM_FROM_MAPOBJECT((short) 0x3B4),//27A v146 - 0x2B4; v171 - 0x3B3; v171.2 - 0x3B4?
    REMOVE_ITEM_FROM_MAP((short) 0x3B6),//27C v146 - 0x2B6; v171 - 0x3B5; v171.2 - 0x3B6
    SPAWN_KITE_ERROR((short) 0x2B7),//27D
    SPAWN_KITE((short) 0x2B8),
    DESTROY_KITE((short) 0x2B9),
    SPAWN_MIST((short) 0x2BA),//v145, seems right..
    REMOVE_MIST((short) 0x2BB),
    SPAWN_DOOR((short) 0x2BC),//v145, WRONG! 0x2B6
    REMOVE_DOOR((short) 0x2BD),
    MECH_DOOR_SPAWN((short) 0x2BE),//v145, confirmed.
    MECH_DOOR_REMOVE((short) 0x2BF),//v145, confirmed.
    REACTOR_HIT((short) 0x3C4),//2C0 v171 - 0x3C3; v171.2 - 0x3C4
    REACTOR_MOVE((short) 0x3C5),//2C1
    REACTOR_SPAWN((short) 0x3C6), // v146 - 0x2C2; v170 - 0x398; v171 - 0x3C5; v171.2 - 0x3C6
    REACTOR_DESTROY((short) 0x3CA),//2C4 v171 - 0x3C9; v171.2 - 0x3CA
    SPAWN_EXTRACTOR((short) 0x3C8),//2C5
    REMOVE_EXTRACTOR((short) 0x3C9),//2C6
    ROLL_SNOWBALL((short) 0x2C7),//2C7
    HIT_SNOWBALL((short) 0x2D0),//28E0x218
    SNOWBALL_MESSAGE((short) 0x2D1),//28F
    LEFT_KNOCK_BACK((short) 0x2C4),//D1
    HIT_COCONUT((short) 0x2C5),//D2
    COCONUT_SCORE((short) 0x2C6),//D3
    MOVE_HEALER((short) 0x2C7),//D4
    PULLEY_STATE((short) 0x2C8),//294
    MONSTER_CARNIVAL_START((short) 0x2C9),//295
    MONSTER_CARNIVAL_OBTAINED_CP((short) 0x2CA),//296
    MONSTER_CARNIVAL_STATS((short) 0x2CB),////297
    MONSTER_CARNIVAL_SUMMON((short) 0x2CD),//299
    MONSTER_CARNIVAL_MESSAGE((short) 0x2CE),//29A
    MONSTER_CARNIVAL_DIED((short) 0x2CF),//29B
    MONSTER_CARNIVAL_LEAVE((short) 0x2D0),//29C
    MONSTER_CARNIVAL_RESULT((short) 0x2D1),//29D
    MONSTER_CARNIVAL_RANKING((short) 0x2D2),//29E
    ARIANT_SCORE_UPDATE((short) 0x300),
    SHEEP_RANCH_INFO((short) 0x301),
    SHEEP_RANCH_CLOTHES((short) 0x999),//0x302
    WITCH_TOWER((short) 0x999),//0x303
    EXPEDITION_CHALLENGE((short) 0x999),//0x304
    ZAKUM_SHRINE((short) 0x305),
    CHAOS_ZAKUM_SHRINE((short) 0x306),
    PVP_TYPE((short) 0x307),
    PVP_TRANSFORM((short) 0x308),
    PVP_DETAILS((short) 0x309),
    PVP_ENABLED((short) 0x30A),
    PVP_SCORE((short) 0x30B),
    PVP_RESULT((short) 0x30C),
    PVP_TEAM((short) 0x30D),
    PVP_SCOREBOARD((short) 0x30E),
    PVP_POINTS((short) 0x310),
    PVP_KILLED((short) 0x311),
    PVP_MODE((short) 0x312),
    PVP_ICEKNIGHT((short) 0x313),//
    HORNTAIL_SHRINE((short) 0x2E1),
    CAPTURE_FLAGS((short) 0x2E2),
    CAPTURE_POSITION((short) 0x2E3),
    CAPTURE_RESET((short) 0x2E4),
    PINK_ZAKUM_SHRINE((short) 0x2E5),
    NPC_TALK((short) 0x4B4),//0x2E6 ;v146 - 0x33D; v171 - 0x4B3; v171.2 - 0x4B4
    OPEN_NPC_SHOP((short) 0x4B5),//2E7; v146 - 0x33E; v171 - 0x4B4; v171.2 - 0x4B5
    CONFIRM_SHOP_TRANSACTION((short) 0x4B6),//2E8 v171.3 - 0x4B6
    OPEN_STORAGE((short) 0x34A),//2F1
    MERCH_ITEM_MSG((short) 0x34B),//2F2
    MERCH_ITEM_STORE((short) 0x34C),//2F3
    RPS_GAME((short) 0x34D),//2F4
    MESSENGER((short) 0x34E),////2F5
    PLAYER_INTERACTION((short) 0x34F),//2F6 v146 - 0x34F; v171.3 - 0x4D6
    VICIOUS_HAMMER((short) 0x2F4),
    LOGOUT_GIFT((short) 0x2FB),
    TOURNAMENT((short) 0x236),
    TOURNAMENT_MATCH_TABLE((short) 0x237),
    TOURNAMENT_SET_PRIZE((short) 0x238),
    TOURNAMENT_UEW((short) 0x239),
    TOURNAMENT_CHARACTERS((short) 0x23A),
    SEALED_BOX((short) 0x23C),
    WEDDING_PROGRESS((short) 0x236),
    WEDDING_CEREMONY_END((short) 0x237),
    PACKAGE_OPERATION((short) 0x353),//v143
    CS_CHARGE_CASH((short) 0x2CA),
    CS_EXP_PURCHASE((short) 0x23B),
    GIFT_RESULT((short) 0x23C),
    CHANGE_NAME_CHECK((short) 0x23D),
    CHANGE_NAME_RESPONSE((short) 0x23E),
    CS_UPDATE((short) 0x35B),//355
    CS_OPERATION((short) 0x35C),//356
    CS_MESO_UPDATE((short) 0x35F),//359
    //0x314 int itemid int sn
    CASH_SHOP((short) 0x372),//v145 confirmed
    CASH_SHOP_UPDATE((short) 0x373),//v145 seems OK
    GACHAPON_STAMPS((short) 0x253),
    FREE_CASH_ITEM((short) 0x254),
    CS_SURPRISE((short) 0x255),
    XMAS_SURPRISE((short) 0x256),
    ONE_A_DAY((short) 0x258),
    NX_SPEND_GIFT((short) 0x25A),
    RECEIVE_GIFT((short) 0x25A),//new v145
    KEYMAP((short) 0x537), //v146 - 0x37C; v170 - 0x514; v171.1 - 0x517; v171.2 - 0x537
    PET_AUTO_HP((short) 0x377),//321
    PET_AUTO_MP((short) 0x378),//322
    PET_AUTO_CURE((short) 0x379),//323
    START_TV((short) 0x324),
    REMOVE_TV((short) 0x325),
    ENABLE_TV((short) 0x326),
    GM_ERROR((short) 0x26D),
    ALIEN_SOCKET_CREATOR((short) 0x341),
    GOLDEN_HAMMER((short) 0x279),
    BATTLE_RECORD_DAMAGE_INFO((short) 0x27A),
    CALCULATE_REQUEST_RESULT((short) 0x27B),
    BOOSTER_PACK((short) 0x999),
    BOOSTER_FAMILIAR((short) 0x999),
    BLOCK_PORTAL((short) 0x999),
    NPC_CONFIRM((short) 0x999),
    RSA_KEY((short) 0x999),
    LOGIN_AUTH((short) 0x999),
    PET_FLAG_CHANGE((short) 0x999),
    BUFF_BAR((short) 0x999),
    GAME_POLL_REPLY((short) 0x999),
    GAME_POLL_QUESTION((short) 0x999),
    ENGLISH_QUIZ((short) 0x999),
    FISHING_BOARD_UPDATE((short) 0x999),
    BOAT_EFFECT((short) 0x999),
    FISHING_CAUGHT((short) 0x999),
    SIDEKICK_OPERATION((short) 0x999),
    FARM_PACKET1((short) 0x35C),
    FARM_ITEM_PURCHASED((short) 0x35D),
    FARM_ITEM_GAIN((short) 0x358),
    HARVEST_WARU((short) 0x35A),
    FARM_MONSTER_GAIN((short) 0x35B),
    FARM_INFO((short) 0x368),
    FARM_MONSTER_INFO((short) 0x369),
    FARM_QUEST_DATA((short) 0x36A),
    FARM_QUEST_INFO((short) 0x36B),
    FARM_MESSAGE((short) 0x36C),//36C
    UPDATE_MONSTER((short) 0x36D),
    AESTHETIC_POINT((short) 0x36E),
    UPDATE_WARU((short) 0x36F),
    FARM_EXP((short) 0x374),
    FARM_PACKET4((short) 0x375),
    QUEST_ALERT((short) 0x377),
    FARM_PACKET8((short) 0x378),
    FARM_FRIENDS_BUDDY_REQUEST((short) 0x37B),
    FARM_FRIENDS((short) 0x37C),
    FARM_USER_INFO((short) 0x388),
    FARM_AVATAR((short) 0x38A),
    FRIEND_INFO((short) 0x38D),
    FARM_RANKING((short) 0x38F), //+69
    SPAWN_FARM_MONSTER1((short) 0x393),
    SPAWN_FARM_MONSTER2((short) 0x394),
    RENAME_MONSTER((short) 0x395),
    STRENGTHEN_UI((short) 0x408),//39D
    //Unplaced:
    MAPLE_POINT((short) 0x999),//E6 //v146 - 0xED; v170 - 0x534; v171 - nope
    DEATH_COUNT((short) 0x206),

    REDIRECTOR_COMMAND((short) 0x1337), 
    
    SHOW_DAMAGE_SKIN((short) 0xDA);//:v

    private short code = -2;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public short getValue() {
        return getValue(true);
    }

    public short getValue(boolean show) {
        if (show && ServerConfig.logPackets && !isSpamHeader(this)) {
            String tab = "";
            for (int i = 4; i > Integer.valueOf(this.name().length() / 8); i--) {
                tab += "\t";
            }
            System.out.println("[Send]\t" + this.name() + tab + "|\t" + this.code + "\t|\t" + HexTool.getOpcodeToString(this.code)/* + "\r\nCaller: " + Thread.currentThread().getStackTrace()[2] */);
            FileoutputUtil.log("PacketLog.txt", "\r\n\r\n[Send]\t" + this.name() + tab + "|\t" + this.code + "\t|\t" + HexTool.getOpcodeToString(this.code) + "\r\n\r\n");
        }
        return code;
    }

    private SendPacketOpcode(short code) {
        this.code = code;
    }

    public String getType(short code) {
        String type = null;
        if (code >= 0 && code < 0xE || code >= 0x17 && code < 0x21) {
            type = "CLogin";
        } else if (code >= 0xE && code < 0x17) {
            type = "LoginSecure";
        } else if (code >= 0x21 && code < 0xCB) {
            type = "CWvsContext";
        } else if (code >= 0xD2) {
            type = "CField";
        }
        return type;
    }

    public static String getOpcodeName(int value) {
        for (SendPacketOpcode opcode : SendPacketOpcode.values()) {
            if (opcode.getValue(false) == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

    public boolean isSpamHeader(SendPacketOpcode opcode) {
        switch (opcode) {
            case AUTH_RESPONSE:
            case SERVERLIST:
            case UPDATE_STATS:
            case MOVE_PLAYER:
            case SPAWN_NPC:
            case SPAWN_NPC_REQUEST_CONTROLLER:
            case REMOVE_NPC:
            case MOVE_MONSTER:
            case MOVE_MONSTER_RESPONSE:
            case SPAWN_MONSTER:
            case SPAWN_MONSTER_CONTROL:
            case HAKU_MOVE:
            case DRAGON_MOVE:
            //case MOVE_SUMMON:
            // case MOVE_FAMILIAR:
            
             case ANDROID_MOVE:
             case INVENTORY_OPERATION:
            case MOVE_PET:
            case SHOW_SPECIAL_EFFECT:
            case DROP_ITEM_FROM_MAPOBJECT:
            case REMOVE_ITEM_FROM_MAP:
            //case UPDATE_PARTYMEMBER_HP:
            case DAMAGE_PLAYER:
            case SHOW_MONSTER_HP:
            case CLOSE_RANGE_ATTACK:
       //     case RANGED_ATTACK:
            //case ARAN_COMBO:
            case REMOVE_BG_LAYER:
            case SPECIAL_STAT:
            case TOP_MSG:
            case NPC_ACTION:
//            case ANGELIC_CHANGE:
            case MONSTER_SKILL:
            case UPDATE_CHAR_LOOK:
            case KILL_MONSTER:
                return true;
        }
        return false;
    }
}
