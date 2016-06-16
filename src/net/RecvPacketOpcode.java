package net;

public enum RecvPacketOpcode {

    /*
     * General Opcodes
     * Used for general purposes.
     */
    RSA_KEY(false),
    STRANGE_DATA,
    LOGIN_REDIRECTOR(false, (short) 0x01),
    CRASH_INFO(false, (short) 0x95),    //v146 -  0x2E; v170 - 0xA0; v171 - 0x95
    PONG(false, (short) 0x93),          //v146 - 0x46; v170 - 0x9E; v171 - 0x93
    AUTH_REQUEST(false, (short) 0x86),  //v146 - 0x30; v170 - 0x90; v171 - 0x86
    CLIENT_ERROR(false, (short) 0x85),  //v146 - 0x4A; v170 - 0x8F; v171 - 0x85
    /*
     * MapleTalk Opcodes
     */
    MIGRATE_IN(true, (short) 0x01),
    UNK_IN(true, (short) 0x06),
    PONG_TALK(true, (short) 0x0E),
    TALK_GUILD_INFO(true, (short) 0x10),
    GUILDCHAT(true, (short) 0x13),
    BUDDYCHAT(true, (short) 0x14),
    /*
     * Login Opcodes
     * Used for login packets.
     * Opcode codes are ordered in the order in which they will be received by the server or sent by the client.
     * This will allow for ease in updating future opcodes.
     */
    CLIENT_HELLO(false, (short) 0x67),  // v146 - 0x3F; v160 - 0x42; v170 - 0x67; v171 - 0x67;
    CLIENT_REQUEST(false, (short) 0xA3), // v170 - 0xA3; v171 - 0xA3
    CLIENT_REQUEST2(false, (short) 0xA8), 
    CLIENT_START(false, (short) 0x66),  // v146 - 0x38; v160 - 0x3A, v170 - 0x66
    LOGIN_PASSWORD(false, (short) 0x69), // v146 - 0x40;  v160 - 0x43; v170 - 0x69; v171 - 0x69
    WRONG_PASSWORD(false, (short) 0xA4),//v146 - 0x49; v170 - 0xA4; v171 - 0xA4
    ACCEPT_TOS(true, (short) 0x9A), // v146 - 0x1D; v171.3 - 0x9A
    /*
     * World Select Opcodes
     */
    SERVERLIST_REQUEST(false, (short) 0x9E), // v146 - 0x22  v170 - 0x82; v171 - 0x9E
    REDISPLAY_SERVERLIST(true, (short) 0x75), // v146 - 0x23; v170 - 0x72; v171 - 0x75
    VIEW_SERVERLIST(false, (short) 0x72),  // v146 - 0x21; v170 - 0x72; v171 - 0x72
    SERVERSTATUS_REQUEST(false, (short) 0x99),   // v146 - 0x1D; v170 - 0x7E; v171 - 0x99
    /*
     * Character Select Opcodes
     */
    CHARLIST_REQUEST(false, (short) 0x6A),  // v146 - 0x43; v170 - 0x6A; v171 - 0x6A
    CHECK_CHAR_NAME(true, (short) 0x74), // v146 - 0x28; v170 - 0x74; v171 - 0x74
    CREATE_CHAR(false, (short) 0x7D),  // v146 - 0x45; v170 - 0x87; v171 - 0x7D
    CREATE_SPECIAL_CHAR(true, (short) 0x7D), // v146 - 0x41; v170 - 0x87
    DELETE_CHAR(true, (short) 0x80), // v146 - 0x2C; v170 - 0x8A; v171 - 0x80
    CHAR_SELECT_NO_PIC(false, (short) 0xA2),// v146 - 0x25; v170 - 0x91; v171 - 0xA2
    CHAR_SELECT(true, (short) 0x6B), // v146 - 0x31; v170 - 0x6B; v171 - 0x6B
    VIEW_SELECT_PIC(true, (short) 0x6C), // v146 - 0x35; v170 - 0x6C; v171 - 0x6C  Don't know what this is used for
    VIEW_REGISTER_PIC(true, (short) 0x92), // v146 - 0x32; v170 - 0x91 ??  not correct
    CHANGE_PIC_REQUEST(true, (short) 0xA7),  // v146 - 0x33; v170 - 0xA7
    PLAYER_LOGGEDIN(false, (short) 0x6E), // v146 - 0x27; v170 - 0x6E; v171 - 0x6E
    CHARACTER_CARD(true, (short) 0x8F), // v146 - 0x3C; v171.3 - 0x8F
    CHAR_BURNING(true, (short) 0x20E), //v172.2 - 0x20E
    
    // Char Select Opcodes - Not Updated Yet
    GUEST_LOGIN(true, (short) 0x16),
    CLIENT_FAILED(false, (short) 0x39),
    PART_TIME_JOB(true, (short) 0x3B), 
    ENABLE_LV50_CHAR(true, (short) 0x3D),
    CREATE_LV50_CHAR(true, (short) 0x3E),
    ENABLE_SPECIAL_CREATION(true, (short) 0x3E),
    CREATE_ULTIMATE(false, (short) 0x999),//46
    AUTH_SECOND_PASSWORD(true, (short) 0x47),
    
    /*
     * Channel Opcodes.
     * Used for in-game packets.
     */
    CHANGE_MAP(true, (short) 0xAC), // v146 - 0x51; v170 - 0xAA; v171 - 0xAC
    CHANGE_CHANNEL(true, (short) 0xAD), // v146 - 0x52; v171 - 0xAD
    ENTER_CASH_SHOP(true, (short) 0x54),// v146 - 0x54; v171.3 - 0xB1
    ENTER_FARM(true, (short) 0x57),
    ENTER_AZWAN(true, (short) 0x4D),
    ENTER_AZWAN_EVENT(true, (short) 0x4A),
    LEAVE_AZWAN(true, (short) 0x4B),
    
    ENTER_PVP(true, (short) 0xB9), // v146 - 0x5C; v171 - 0xB9 ?
    ENTER_PVP_PARTY(true, (short) 0xB9), // v146 - 0x5C; v171 - 0xB9 ?
    LEAVE_PVP(true, (short) 0xBA), // v171 - 0xBA ?
    MOVE_PLAYER(true, (short) 0xBB),  // v146 - 0x5E; v170 - 0xB9; v171 - 0xBB
    CANCEL_CHAIR(true, (short) 0xBC), // v146 - 0x60; v171 - 0xBC
    USE_CHAIR(true, (short) 0xBD), //v146 - 0x61; v171 - 0xBD
    CLOSE_RANGE_ATTACK(true, (short) 0xBF), //v146 - 0x62; v171 - 0xBF
    RANGED_ATTACK(true, (short) 0xC0), // v146 - 0x63; v171 - 0xC0
    MAGIC_ATTACK(true, (short) 0xC1), // v146 - 0x64; v171 - 0xC1
    PASSIVE_ENERGY(true, (short) 0xC2), //v146 - 0x65; v171 - 0xC2 ?
    TAKE_DAMAGE(true, (short) 0xC5), // v146 - 0x68; v171 - 0xC5
    PVP_ATTACK(true, (short) 0xC6), // v146 - 0x69; V171 - 0xC6 ?
    GENERAL_CHAT(true, (short) 0xC7), // v146 - 0x6A; v170 - 0xC5; v171 - 0xC7
    CLOSE_CHALKBOARD(true, (short) 0xC8), // v146 - 0x6B; v170 - 0xC6; v171 - 0xC8 ?
    FACE_EXPRESSION(true, (short) 0xC9), // v146 - 0x6C; v171 - 0xC9
    ANDROID_FACE_EXPRESSION(true, (short) 0xCA), // v146 - 0x6D; v171 - 0xCA
    USE_ITEMEFFECT(true, (short) 0xCB), // v146 - 0x6E; v171 - 0xCB
    WHEEL_OF_FORTUNE(true, (short) 0xCC), // v146 - 0x6F; v171 - 0xCC
    
    USE_TITLE(true, (short) 0x73), // v146 - 0x72; 
    ANGELIC_CHANGE(true, (short) 0x73),//71
    CHANGE_CODEX_SET(true, (short) 0x7A),//79
    CODEX_UNK(true, (short) 0x7B),
    MONSTER_BOOK_DROPS(true, (short) 0x7F),//7C  v146 - 0x7D
    NPC_TALK(true, (short) 0xD8),       //v146 - 0x7F; v171 - 0xD8
    NPC_TALK_MORE(true, (short) 0xDA),  //v146 - 0x81; v171 - 0xDA
    NPC_SHOP(true, (short) 0xDB),       //v146 - 0x82; v171 - 0xDB
    STORAGE_OPERATION(true, (short) 0xDC),//v171 - 0xDC
    USE_HIRED_MERCHANT(true, (short) 0x85),//84
    MERCH_ITEM_STORE(true, (short) 0x87),//85
    PACKAGE_OPERATION(true, (short) 0x7F),//87
    CANCEL_MECH(true, (short) 0x89),//87
    HOLLY(true, (short) 0xF0),
    OWL(true, (short) 0x8C),//8A
    OWL_WARP(true, (short) 0x8D),//8A
    USE_HOLY_FOUNTAIN(true, (short) 0xE4),                 //v173.1 - 0xE4
    ITEM_GATHER(true, (short) 0xEA),                       //v171 - 0xEA
    ITEM_SORT(true, (short) 0xEB),            //v146 - 0x92; v171 - 0xEB
    ITEM_MOVE(true, (short) 0xEC),            //v146 - 0x94; v171 - 0xEC
    MOVE_BAG(true, (short) 0xED),             //v146 - 0x95; v172.2 - 0xED ?
    SWITCH_BAG(true, (short) 0xEE),                        //v172.2 - 0xEE ?
    USE_ITEM(true, (short) 0xF1),             //v146 - 0x98; v172.2 - 0xF1
    CANCEL_ITEM_EFFECT(true, (short) 0xF2),   //v146 - 0x99; v172.2 - 0xF2
    USE_SUMMON_BAG(true, (short) 0xF4),       //v146 - 0x9B; v171.3 - 0xF4
    USE_PET_FOOD(true, (short) 0xF5),         //v146 - 0x9C; v171.3 - 0xF5
    USE_MOUNT_FOOD(true, (short) 0xF6),       //v146 - 0x9D; v171.3 - 0xF6
    USE_SCRIPTED_NPC_ITEM(true, (short) 0xF7),//v146 - 0x9E; v171.3 - 0xF7
    USE_RECIPE(true, (short) 0xF8),           //v146 - 0x9F; v172.2 - 0xF8 ?
    USE_NEBULITE(true, (short) 0xF9),         //v146 - 0xA0; v172.2 - 0xF9 ?
    USE_ALIEN_SOCKET(true, (short) 0xFA),     //v146 - 0xA1; v172.2 - 0xFA ?
    USE_ALIEN_SOCKET_RESPONSE(true, (short) 0xFB),//v146 - 0xA2; v172.2 - 0xFB ?
    USE_NEBULITE_FUSION(true, (short) 0xFC),  //v146 - 0xA3; v172.2 - 0xFC ?
    USE_CASH_ITEM(true, (short) 0xFE),        //v146 - 0xA4; v172.1 - 0xFE
    USE_CATCH_ITEM(true, (short) 0x100),      //v146 - 0xA6; v172.2 - 0x100 ?
    USE_SKILL_BOOK(true, (short) 0x105),      //v146 - 0xAB; v172.2 - 0x105 ?
    USE_EXP_POTION(true, (short) 0x108),      //v146 - 0xAE; v172.2 - 0x108 ?
    TOT_GUIDE(true, (short) 0xB5),
    USE_OWL_MINERVA(true, (short) 0x109),     //v146 - 0xBC; v172.2 - 0x109 ?
    USE_TELE_ROCK(true, (short) 0x10A),       //v146 - 0xBD; v172.2 - 0x10A ?
    USE_RETURN_SCROLL(true, (short) 0x10C),  //v146 - 0xBF; v171.3 - 0x10C
    USE_UPGRADE_SCROLL(true, (short) 0x10D), //v146 - 0xC0; v172.2 - 0x10D ?
    USE_FLAG_SCROLL(true, (short) 0x10E),    //v146 - 0xC1; v172.2 - 0x10E ?
    USE_EQUIP_SCROLL(true, (short) 0x10F),   //v146 - 0xC2; v172.2 - 0x10F ?
    USE_POTENTIAL_SCROLL(true, (short) 0x114),//v146 - 0xC6; v172.2 - 0x114
    USE_BONUS_POTENTIAL(true, (short) 0x115), //v146 - 0xC8; v172.2 - 0x115 ?
    USE_ABYSS_SCROLL(true, (short) 0x114),   //v146 - 0xC7; v172.2 - 0x114 ?
    USE_CARVED_SEAL(true, (short) 0xCA),//C5 //v146 - 0xC9?
    USE_BAG(true, (short) 0xCA),             // v146 - 0xC9; v172.2
    USE_SOUL_ENCHANTER(true, (short) 0x118),              //v172.2 - 0x118
    USE_CRAFTED_CUBE(true, (short) 0xCA),    //v146 - 0xCA; v172.2 - 0xF9 ?
    USE_MAGNIFY_GLASS(true, (short) 0x11E),  //v146 - 0xCD; v171.3 - 0x11E
    
    
    DISTRIBUTE_AP(true, (short) 0x125),      //v146 - 0x126; v171 - 0x125
    AUTO_ASSIGN_AP(true, (short) 0x126),     //v146 - 0x127; v171 - 0x126
    HEAL_OVER_TIME(true, (short) 0x128),     //v146 - 0xD2; v171 - 0x128
    LINK_SKILL(true, (short) 0xD4),//Confirmed
    IDK_1(true, (short) 0x49),
    IDK_2(true, (short) 0x70),
    IDK_3(true, (short) 0x71),
    IDK_4(true, (short) 0xE2),
    
    DISTRIBUTE_SP(true, (short) 0x12B),    //v146 - 0xD5; v171 - 0x12B
    SPECIAL_MOVE(true, (short) 0x12C),     //v146 - 0xD6; v171 - 0x12C
    CANCEL_BUFF(true, (short) 0x12D),      //v146 - 0xD7; v171 - 0x12D
    SKILL_EFFECT(true, (short) 0x12E),     //v146 - 0xD8; v171 - 0x12E ?
    MESO_DROP(true, (short) 0x12F),        //v146 - 0xD9; v171 - 0x12F 
    GIVE_FAME(true, (short) 0x130),        //v146 - 0xDA; v171 - 0x130 ?
    CHAR_INFO_REQUEST(true, (short) 0x132),//v146 - 0xDC; v171 - 0x132
    SPAWN_PET(true, (short) 0x133),        //v146 - 0xDD; v171 - 0x133
    GET_BOOK_INFO(true, (short) 0x135),    //v146 - 0xDF; v171 - 0x135 ?
    
    USE_FAMILIAR(true, (short) 0x2D4),     //v146 - 0xE0; v171.3 - 0x2C6; v172.2 - 0x2D4
    SPAWN_FAMILIAR(true, (short) 0x2D5),   //v146 - 0xE1; v171.3 - 0x2C7; v172.2 - 0x2D5
    RENAME_FAMILIAR(true, (short) 0x2C8),  //v146 - 0xE2; v171.3 - 0x2C8
    PET_BUFF(true, (short) 0xE3),//E0
    CANCEL_DEBUFF(true, (short) 0xE4),//E1
    SPECIAL_PORTAL(true, (short) 0x136),        // v146 - 0xE5; v171.3 - 0x136; v172.1 - 0x136
    USE_INNER_PORTAL(true, (short) 0x137),      // v146 - 0xE6; v171.3 - 0x137
    TELEPORT_ROCK_ADD_MAP(true, (short) 0x138), // v146 - 0xE7; v171.3 - 0x138?
    LIE_DETECTOR(true, (short) 0x139),          // v146 - 0xE8; v171.3 - 0x139?
    LIE_DETECTOR_SKILL(true, (short) 0x13A),    // v146 - 0xE9; v171.3 - 0x13A?
    LIE_DETECTOR_RESPONSE(true, (short) 0x13B), // v146 - 0xEA; v171.3 - 0x13B?
    REPORT(true, (short) 0x13E),                // v146 - 0xEC; v171.3 - 0x13E?
    QUEST_ACTION(true, (short) 0x13F),          // v146 - 0xED; v171.3 - 0x13F
    REISSUE_MEDAL(true, (short) 0x140),         // v146 - 0xEE; v171.3 - 0x140
    BUFF_RESPONSE(true, (short) 0x141),         // v146 - 0xEF; v171.3 - 0x141
    SKILL_MACRO(true, (short) 0x147),           // v146 - 0xF5; v171.3 - 0x147
    SPECIAL_STAT(false, (short) 0x157),         // v146 - 0x10C; v171 - 0x157; v172.1 - 0x157
    REWARD_ITEM(true, (short) 0xF7),//F2
    ITEM_MAKER(true, (short) 0x999),
    REPAIR_ALL(true, (short) 0xFE),//C7
    REPAIR(true, (short) 0xFF),//C8
    SOLOMON(true, (short) 0xC9),
    GACH_EXP(true, (short) 0xCA),
    FOLLOW_REQUEST(true, (short) 0xFD),
    PQ_REWARD(true, (short) 0xFE),
    FOLLOW_REPLY(true, (short) 0x101),
    AUTO_FOLLOW_REPLY(true, (short) 0x999),
    USE_TREASURE_CHEST(true, (short) 0x999),
    PROFESSION_INFO(true, (short) 0x102),
    USE_POT(true, (short) 0x999),//D6
    CLEAR_POT(true, (short) 0xD7),
    FEED_POT(true, (short) 0xD8),
    CURE_POT(true, (short) 0xD9),
    REWARD_POT(true, (short) 0xDA),
    AZWAN_REVIVE(true, (short) 0xDB),
    ZERO_TAG(true, (short) 0x123),
    USE_COSMETIC(true, (short) 0x124),
    INNER_CIRCULATOR(true, (short) 0x11A),
    PVP_RESPAWN(true, (short) 0xC0), //v146 - 0xCF
    GAIN_FORCE(true, (short) 0x1FFF),
    ADMIN_CHAT(true, (short) 0x174),         //v146 - 0x126; v171.3 - 0x174?
    PARTYCHAT(true, (short) 0x175),          //v146 - 0x127; v171.3 - 0x175
    COMMAND(true, (short) 0x177),            //v146 - 0x129; v171.3 - 0x177
    SPOUSE_CHAT(true, (short) 0x12A),//122
    MESSENGER(true, (short) 0x12A),//123 v146 - 0x12B
    PLAYER_INTERACTION(true, (short) 0x179), //v146 - 0x12C; v171.3 - 0x179
    PARTY_OPERATION(true, (short) 0x17A),    //v146 - 0x12D; v171.3 - 0x17A
    PARTY_REQUEST(true, (short) 0x17B),      //v146 - 0x12E; v171.3 - 0x17B
    ALLOW_PARTY_INVITE(true, (short) 0x17C), //v146 - 0x12F; v171.3 - 0x17C
    EXPEDITION_OPERATION(true, (short) 0x130),//128
    EXPEDITION_LISTING(true, (short) 0x131),//129
    GUILD_OPERATION(true, (short) 0x180),    //v146 - 0x132; v172.1 - 0x180
    GUILD_INVITATION(true, (short) 0x181),   //v146 - 0x133; v172.2 - 0x181              
    GUILD_APPLICATION_REQUEST(true, (short) 0x182),        //v172.2 - 0x182
    GUILD_CANCEL_APPLICATION(true, (short) 0x183),         //v172.2 - 0x183
    GUILD_ACCEPT_APPLICATION(true, (short) 0x184),         //v172.2 - 0x184
    GUILD_DENY_APPLICATION(true, (short) 0x185),           //v172.2 - 0x185
    ADMIN_COMMAND(true, (short) 0x134),//12C
    ADMIN_LOG(true, (short) 0x135),//12D
    BUDDYLIST_MODIFY(true, (short) 0x18A), //12E v146 - 0x137; v171.3 - 0x18A
    NOTE_ACTION(true, (short) 0x999),//127
    USE_MYSTIC_DOOR(true, (short) 0x190),   //v146 - 0x13A; v171.3 - 0x190
    USE_MECH_DOOR(true, (short) 0x192),     //v146 - 0x13B; v171.3 - 0x192
    CHANGE_KEYMAP(true, (short) 0x194),     //v146 - 0x13D; v171.3 - 0x194
    RPS_GAME(true, (short) 0x135),
    RING_ACTION(true, (short) 0x137), // v146 - 0x136; 
    WEDDING_ACTION(true, (short) 0x137),
    ALLIANCE_OPERATION(true, (short) 0x13B),
    ALLIANCE_REQUEST(true, (short) 0x13C),
    REQUEST_FAMILY(true, (short) 0x999),//13D
    OPEN_FAMILY(true, (short) 0x13E),//13E
    FAMILY_OPERATION(true, (short) 0x13E), // v146 - 0x13F
    DELETE_JUNIOR(true, (short) 0x140),
    DELETE_SENIOR(true, (short) 0x141),
    ACCEPT_FAMILY(true, (short) 0x142),
    USE_FAMILY(true, (short) 0x143),
    FAMILY_PRECEPT(true, (short) 0x144),
    FAMILY_SUMMON(true, (short) 0x145),
    BBS_OPERATION(true, (short) 0x150),//10B
    SOLOMON_EXP(true, (short) 0x151),//10C
    NEW_YEAR_CARD(true, (short) 0x11E),
    XMAS_SURPRISE(true, (short) 0x111),
    TWIN_DRAGON_EGG(true, (short) 0x112),
    ARAN_COMBO(true, (short) 0x15D),//0x152
    REMOVE_ARAN_COMBO(true, (short) 0x15E),
    TRANSFORM_PLAYER(true, (short) 0x999),
    CYGNUS_SUMMON(true, (short) 0x999),
    CRAFT_DONE(true, (short) 0x162),//157
    CRAFT_EFFECT(true, (short) 0x163),//158
    CRAFT_MAKE(true, (short) 0x164),//159
    CHANGE_ROOM_CHANNEL(true, (short) 0x169),//15D
    EVENT_CARD(true, (short) 0x15F),
    CHOOSE_SKILL(true, (short) 0x175),//0x16B
    SKILL_SWIPE(true, (short) 0x176), //0x16C
    VIEW_SKILLS(true, (short) 0x177),//0x16D  
    CANCEL_OUT_SWIPE(true, (short) 0x16F),
    YOUR_INFORMATION(true, (short) 0x16E),//163
    FIND_FRIEND(true, (short) 0x16F),//164
    PINKBEAN_CHOCO_OPEN(true, (short) 0x170),//165
    PINKBEAN_CHOCO_SUMMON(true, (short) 0x171),//166
    CASSANDRAS_COLLECTION(true, (short) 0x178),//new v145
    BUY_SILENT_CRUSADE(true, (short) 0x128),
    BUDDY_ADD(true, (short) 0x1A2),
    MOVE_PET(true, (short) 0x213),    //v146 - 0x1B8; v171.3 - 0x212; v172.1 - 0x213
    PET_CHAT(true, (short) 0x214),    //v146 - 0x1B9; v171.3 - 0x213; v172.1 - 0x214?
    PET_COMMAND(true, (short) 0x215), //v146 - 0x1BA; v171.3 - 0x214; v172.1 - 0x215?
    PET_LOOT(true, (short) 0x216),    //v146 - 0x1BB; v171.3 - 0x215; v172.1 - 0x216?
    PET_AUTO_POT(true, (short) 0x1BC),//1AC
    PET_IGNORE(true, (short) 0x1BD),  
    MOVE_HAKU(true, (short) 0x21D),   //v146 - 0x1C1; v172.2 - 0x21D
    CHANGE_HAKU(true, (short) 0x21E), //v146 - 0x1C2; v172.2 - 0x21E
    //HAKU_1D8(true, (short) 0x1D8),//test
    //HAKU_1D9(true, (short) 0x1D9),//test
    MOVE_SUMMON(true, (short) 0x224),//1b8 v146 - 0x1C8; v171.3 - 0x21C; v172.1 - 0x224
    SUMMON_ATTACK(true, (short) 0x225),//1B9 v146 - 0x1C9; v171.3 - 0x21D; v172.1 - 0x225
    DAMAGE_SUMMON(true, (short) 0x21E),//1BA v146 - 0x1CA; v171.3 - 0x21E ?
    SUB_SUMMON(true, (short) 0x21F),//1BB v146 - 0x1CB; v171.3 - 0x21F ?
    REMOVE_SUMMON(true, (short) 0x220),//1BC v146 - 0x1CC; v171.3 - 0x220 ?
    PVP_SUMMON(true, (short) 0x1CE),//1BE
    MOVE_DRAGON(true, (short) 0x1D0),//1C0
    USE_ITEM_QUEST(true, (short) 0x1D2),//1C4
    MOVE_ANDROID(true, (short) 0x1D4),//1C5
    UPDATE_QUEST(true, (short) 0x2CF), //v146 - 0x1D5; v172.2 - 0x2CF ?
    QUEST_ITEM(true, (short) 0x1D6),//1C8
    MOVE_FAMILIAR(true, (short) 0x30B),// v146 - 0x1DC; v171.3 - 0x307; v172.2 - 0x30B
    TOUCH_FAMILIAR(true, (short) 0x1DD),//1CD
    ATTACK_FAMILIAR(true, (short) 0x30C),//v146 - 0x1DE; v171.3 - 0x308; v172.2 - 0x30C
    REVEAL_FAMILIAR(true, (short) 0x308),//v146 - 0x1DF; v171.3 - 0x30A
    QUICK_SLOT(true, (short) 0x1D7),
    PAM_SONG(true, (short) 0x1D8),
    MOVE_LIFE(true, (short) 0x314),//1EC v146 - 0x20B; v171 - 0x310; v172.1 - 0x314
    AUTO_AGGRO(true, (short) 0x315), // v172.1 - 0x315
    FRIENDLY_DAMAGE(true, (short) 0x999),//1ef
    MONSTER_BOMB(true, (short) 0x1F0),
    HYPNOTIZE_DMG(true, (short) 0x1F1),
    MOB_BOMB(true, (short) 0x1F5),
    MOB_NODE(true, (short) 0x1F6),
    DISPLAY_NODE(true, (short) 0x1F7),
    MONSTER_CARNIVAL(true, (short) 0x1F8),
    NPC_ACTION(true, (short) 0x32E),//203 v146 - 0x222; v171 - 0x329; v172.1 - 0x32D; v173.1 - 0x32E
    ITEM_PICKUP(true, (short) 0x333),//208 v146 - 0x22A; v171 - 0x32E; v172.1 - 0x332; v173.1 - 0x333
    DAMAGE_REACTOR(true, (short) 0x336),//v146 - 0x22D; v171 - 0x331; v172.1 - 0x335; v173.1 - 0x336
    TOUCH_REACTOR(true, (short) 0x22E),//v145 Confirmed
    CLICK_REACTOR(true, (short) 0x22E),//v145 Confirmed v146 - 0x22F
    MAKE_EXTRACTOR(true, (short) 0x22E),//v145 Confirmed v146 - 0x22F
    RECEIVE_GIFT_EFFECT(true, (short) 0x2F5),//new v145
    UPDATE_ENV(true, (short) 0x17E),
    SNOWBALL(true, (short) 0x239),//0x182
    LEFT_KNOCK_BACK(true, (short) 0x183),
    CANDY_RANKING(true, (short) 0x185),//
    START_EVOLUTION(true, (short) 0x186),
    COCONUT(true, (short) 0x999),
    SHIP_OBJECT(true, (short) 0x999),
    PARTY_SEARCH_START(true, (short) 0x34F), //v146 - 0x197; v171.3 - 0x34F
    PARTY_SEARCH_STOP(true, (short) 0x198),
    START_HARVEST(true, (short) 0x251),//22F
    STOP_HARVEST(true, (short) 0x252),//230
    QUICK_MOVE(true, (short) 0x252),
    CS_UPDATE(true, (short) 0x28D),//v145 was 28A
    BUY_CS_ITEM(true, (short) 0x28E),//v145
    COUPON_CODE(true, (short) 0x28F),//v145
    CASH_CATEGORY(true, (short) 0x294),//v145
    PLACE_FARM_OBJECT(false, (short) 0x278),
    FARM_SHOP_BUY(false, (short) 0x27D),
    FARM_COMPLETE_QUEST(false, (short) 0x281),
    CREATE_FARM(false, (short) 0x282),
    HARVEST_FARM_BUILDING(false, (short) 0x283),
    USE_FARM_ITEM(false, (short) 0x284),
    RENAME_MONSTER(false, (short) 0x999),//0x294
    NURTURE_MONSTER(false, (short) 0x999), //Surprise box?
    CS_SURPRISE(false, (short) 0x295),
    LEAVE_FARM(false, (short) 0x299),
    FARM_CHECK_QUEST(false, (short) 0x29D),
    FARM_FIRST_ENTRY(false, (short) 0x2A7),
    GOLDEN_HAMMER(true, (short) 0x2A4),//1BB
    VICIOUS_HAMMER(true, (short) 0x1BD),
    PYRAMID_BUY_ITEM(true, (short) 0x999),
    CLASS_COMPETITION(true, (short) 0x999),
    MAGIC_WHEEL(true, (short) 0x2EE),
    REWARD(true, (short) 0x2EF),//0x2EC 
    BLACK_FRIDAY(true, (short) 0x2BE),
    UPDATE_RED_LEAF(true, (short) 0x29C),
    //Not Placed:
    
    DISTRIBUTE_HYPER(true, (short) 0x1CA), // v146 - 0x171; v172.2 0x1CA
    RESET_HYPER(true, (short) 0x172),//
    DRESSUP_TIME(true, (short) 0x17F),
    DF_COMBO(true, (short) 0x10F),
    BUTTON_PRESSED(true, (short) 0x23B),//1D3 v146 - 0x1E3; v171 - 0x22F; v172.1 - 0x23B (Useless..)
    OS_INFORMATION(true, (short) 0x1E6),//1D6
    LUCKY_LOGOUT(true, (short) 0x2B6),
    MESSENGER_RANKING(true, (short) 0x1DD);
	
	private short opcode;
    private final boolean CheckState;

    private RecvPacketOpcode() {
        this.CheckState = true;
    }

    private RecvPacketOpcode(final boolean CheckState) {
        this.CheckState = CheckState;
    }

    private RecvPacketOpcode(final boolean CheckState, short opcode) {
        this.CheckState = CheckState;
        this.opcode = opcode;
    }
    
    public int getOpcode() {
		return opcode;
	}
    
    public void setOpcode(short opcode) {
		this.opcode = opcode;
	}
    
    @SuppressWarnings("incomplete-switch")
	public static boolean isSpam(RecvPacketOpcode header) {
        switch (header) {
            case AUTH_REQUEST:
            case MOVE_LIFE:
            case MOVE_PLAYER:
            //case SPECIAL_MOVE:
            case MOVE_ANDROID:
            case MOVE_DRAGON:
            case MOVE_SUMMON:
            case MOVE_FAMILIAR:
            case MOVE_PET:
            case QUEST_ACTION:
            case HEAL_OVER_TIME:
            case CHANGE_KEYMAP:
            case USE_INNER_PORTAL:
            case MOVE_HAKU:
            //case TAKE_DAMAGE:
            case FRIENDLY_DAMAGE:
           // case CLOSE_RANGE_ATTACK: //todo code zero
          //  case RANGED_ATTACK: //todo code zero
            case ARAN_COMBO:
            case SPECIAL_STAT:
            case DISTRIBUTE_HYPER:
            case RESET_HYPER: 
            case NPC_ACTION:
            case ANGELIC_CHANGE: 
          //  case QUEST_ACTION
//            case DRESSUP_TIME:
            case BUTTON_PRESSED: 
                return true;
        }
        return false;
    }
	
}
