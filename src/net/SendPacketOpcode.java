package net;

public enum SendPacketOpcode implements WritableIntValueHolder {

	PING((short) 0x12),
	AUTH_RESPONSE((short) 0x17),
	MIGRATE_RESPONSE((short) 0x01),
	PING_TALK((short) 0x0D),
	UNK_RESPONSE((short) 0x0F),
	GUILDCHAT((short) 0x12),
	BUDDYCHAT((short) 0x13),
	LOGIN_AUTH_RESPONSE1((short) 0x34),
	LOGIN_AUTH_RESPONSE2((short) 0x2E),
	LOGIN_AUTH_RESPONSE3((short) 0x36),
	CLIENT_AUTH((short) 0x24),
	CLIENT_RESPONSE((short) 0x2F),
	LOGIN_STATUS((short) 0x00),
	TOS((short) 0x28),
	CHANGE_BACKGROUND((short) 0x36),
	SERVERLIST((short) 0x01),
	ENABLE_RECOMMENDED((short) 0x02),
	SEND_RECOMMENDED((short) 0x03),
	SERVERSTATUS((short) 0x26),
	CHARLIST((short) 0x06),
	CHAR_NAME_RESPONSE((short) 0x0A),
	ADD_NEW_CHAR_ENTRY((short) 0x0B),
	DELETE_CHAR_RESPONSE((short) 0x0C),
	PIC_RESPONSE((short) 0x18),
	REGISTER_PIC_RESPONSE((short) 0x2B),
	SECONDPW_ERROR((short) 0x34),
	SERVER_IP((short) 0x07),
	CHANGE_CHANNEL((short) 0x11),
	CS_USE((short) 0x14),
	PART_TIME((short) 0x1D),
	CHAR_BURNING_RESPONSE((short) 0x15A),
	SEND_LINK((short) 0x01),
	LOGIN_SECOND((short) 0x02),
	CHANNEL_SELECTED((short) 0x02),
	GENDER_SET((short) 0x04),
	PIN_OPERATION((short) 0x05),
	PIN_ASSIGNED((short) 0x06),
	ALL_CHARLIST((short) 0x07),
	RELOG_RESPONSE((short) 0x17),
	SPECIAL_CREATION((short) 0x20),
	INVENTORY_OPERATION((short) 0x47),
	INVENTORY_GROW((short) 0x48),
	UPDATE_STATS((short) 0x49),
	GIVE_BUFF((short) 0x4A),
	CANCEL_BUFF((short) 0x4B),
	TEMP_STATS((short) 0x4C),
	TEMP_STATS_RESET((short) 0x4D),
	UPDATE_SKILLS((short) 0x4E),
	UPDATE_STOLEN_SKILLS((short) 0x4F),
	FALL_DAMAGE((short) 0x50),
	PERSONAL_SHOP_BUY_CHECK((short) 0x51),
	MESO_PICKUP((short) 0x52),
	TARGET_SKILL((short) 0x55),
	FAME_RESPONSE((short) 0x58),
	SHOW_STATUS_INFO((short) 0x59),
	SHOW_NOTES((short) 0x5A),
	TELEPORT_ROCK_LOCATIONS((short) 0x5B),
	LIE_DETECTOR((short) 0x5C),
	LIE_DETECTOR_BOMB((short) 0x5D),
	REPORT_RESPONSE((short) 0x5F),
	REPORT_TIME((short) 0x60),
	REPORT_STATUS((short) 0x61),
	STAR_PLANET_USER_COUNT((short) 0x62),
	UPDATE_MOUNT((short) 0x63),
	SHOW_QUEST_COMPLETION((short) 0x64),
	SEND_TITLE_BOX((short) 0x65),
	USE_SKILL_BOOK((short) 0x66),
	SP_RESET((short) 0x67),
	AP_RESET((short) 0x68),
	EXP_POTION((short) 0x69),
	DISTRIBUTE_ITEM((short) 0x6A),
	EXPAND_CHARACTER_SLOTS((short) 0x6B),
	FINISH_GATHER((short) 0x6D),
	FINISH_SORT((short) 0x6E),
	REPORT_RESULT((short) 0x51),
	TRADE_LIMIT((short) 0x53),
	UPDATE_GENDER((short) 0x54),
	BOOK_INFO((short) 0x5E),
	CODEX_INFO_RESPONSE((short) 0x5F),
	FULL_CLIENT_DOWNLOAD((short) 0x5A),
	CHAR_INFO((short) 0x71),
	PARTY_OPERATION((short) 0x72),
	MEMBER_SEARCH((short) 0x73),
	URUS_PARTY_MEMBER_SEARCH((short) 0x74),
	PARTY_SEARCH((short) 0x75),
	URUS_PARTY_RESULT((short) 0x76),
	INTRUSION_FRIEND_SEARCH((short) 0x77),
	INTRUSION_LOBBY_SEARCH((short) 0x78),
	EXPEDITION_OPERATION((short) 0x7A),
	BUDDYLIST((short) 0x7B),
	STAR_BUDDYLIST((short) 0x7C),
	ACCOUNT_BUDDYLIST((short) 0x7D),
	GUILD_REQUEST((short) 0x7E),
	GUILD_OPERATION((short) 0x7F),
	ALLIANCE_OPERATION((short) 0x80),
	SPAWN_PORTAL((short) 0x81),
	SERVERMESSAGE((short) 0x82),
	MECH_PORTAL((short) 0x63),
	ECHO_MESSAGE((short) 0x64),
	ITEM_OBTAIN((short) 0x84),
	PIGMI_REWARD((short) 0x85),
	OWL_OF_MINERVA((short) 0x86),
	OWL_RESULT((short) 0x87),
	ENGAGE_REQUEST((short) 0x8A),
	ENGAGE_RESULT((short) 0x8B),
	WEDDING_GIFT((short) 0x8C),
	WEDDING_MAP_TRANSFER((short) 0x8D),
	USE_CASH_PET_FOOD((short) 0x8E),
	CASH_PET_PICKUP((short) 0x8F),
	CASH_PET_SKILL((short) 0x90),
	CASH_PET_UPDATE_LOOK((short) 0x91),
	CASH_PET_DYE((short) 0x92),
	YELLOW_CHAT((short) 0x93),
	SHOP_DISCOUNT((short) 0x94),
	CATCH_MOB((short) 0x95),
	MAKE_PLAYER_NPC((short) 0x96),
	PLAYER_NPC((short) 0x97),
	DISABLE_NPC((short) 0x99),
	MONSTER_BOOK_SET_CARD((short) 0x9A),
	MONSTER_BOOK_SET_COVER((short) 0x9B),
	BOOK_STATS((short) 0x81),
	UPDATE_CODEX((short) 0x82),
	CARD_DROPS((short) 0x187),
	FAMILIAR_INFO((short) 0x84),
	CHANGE_HOUR((short) 0x9C),
	TOGGLE_MINIMAP((short) 0x9D),
	CONSULT_UPDATE((short) 0x9E),
	CLASS_UPDATE((short) 0x9F),
	WEB_BOARD_UPDATE((short) 0xA0),
	SESSION_VALUE((short) 0xA1),
	PARTY_VALUE((short) 0xA2),
	MAP_SET_VALUE((short) 0xA3),
	MAP_VALUE((short) 0xA4),
	BONUS_EXP((short) 0xA5),
	POTION_BONUS((short) 0x8E),
	SEND_PEDIGREE((short) 0xA6),
	OPEN_FAMILY((short) 0xA7),
	FAMILY_MESSAGE((short) 0xA8),
	FAMILY_INVITE((short) 0xA9),
	FAMILY_INVITE_RESPONSE((short) 0xAA),
	FAMILY((short) 0xAB),
	SENIOR_MESSAGE((short) 0xAC),
	REP_INCREASE((short) 0xAD),
	EVOLVING_ACTION((short) 0x98),
	FAMILY_LOGGEDIN((short) 0xAE),
	FAMILY_BUFF((short) 0xAF),
	FAMILY_USE_REQUEST((short) 0xB0),
	LEVEL_UPDATE((short) 0xB1),
	MARRIAGE_UPDATE((short) 0xB2),
	JOB_UPDATE((short) 0xB3),
	BUY_EQUIP_EXT((short) 0xB3),
	MAPLE_TV_MSG((short) 0x8D),
	AVATAR_MEGA_RESULT((short) 0x10D),
	AVATAR_MEGA((short) 0x10E),
	AVATAR_MEGA_REMOVE((short) 0x10F),
	POPUP2((short) 0x9D),
	CANCEL_NAME_CHANGE((short) 0x9E),
	CANCEL_WORLD_TRANSFER((short) 0x9F),
	CLOSE_HIRED_MERCHANT((short) 0xA3),
	GM_POLICE((short) 0xA4),
	TREASURE_BOX((short) 0xA5),
	NEW_YEAR_CARD((short) 0xA6),
	RANDOM_MORPH((short) 0xA7),
	CANCEL_NAME_CHANGE_2((short) 0xA9),
	FOLLOW_REQUEST((short) 0xB5),
	TOP_MSG((short) 0xB7),
	MID_MSG((short) 0xB9),
	CLEAR_MID_MSG((short) 0xBA),
	SPECIAL_MSG((short) 0xBB),
	MAPLE_ADMIN_MSG((short) 0xBC),
	GREEN_TOP_MSG((short) 0xBD),
	CAKE_VS_PIE_MSG((short) 0xB4),
	GM_STORY_BOARD((short) 0xB5),
	INVENTORY_FULL((short) 0xB6),
	SLOT_UPDATE((short) 0x999),
	WILD_HUNTER_INFO((short) 0xC0),
	ZERO_INFO((short) 0xC1),
	ZERO_WP((short) 0xC2),
	ZERO_SUB_HP((short) 0xC3),
	YOUR_INFORMATION((short) 0xB9),
	FIND_FRIEND((short) 0xBA),
	VISITOR((short) 0xBB),
	PINKBEAN_CHOCO((short) 0xBC),
	PAM_SONG((short) 0xBD),
	AUTO_CC_MSG((short) 0xBE),
	DISALLOW_DELIVERY_QUEST((short) 0xC5),
	ULTIMATE_EXPLORER((short) 0xC6),
	SPECIAL_STAT((short) 0xC6),
	UPDATE_IMP_TIME((short) 0xC7),
	ITEM_POT((short) 0xC8),
	MULUNG_MESSAGE((short) 0xC9),
	GIVE_CHARACTER_SKILL((short) 0xCC),
	MULUNG_DOJO_RANKING((short) 0xD1),
	UPDATE_INNER_ABILITY((short) 0xD4),
	EQUIP_STOLEN_SKILL((short) 0xD2),
	REPLACE_SKILLS((short) 0xD5),
	INNER_ABILITY_MSG((short) 0xD6),
	ENABLE_INNER_ABILITY((short) 0xD6),
	DISABLE_INNER_ABILITY((short) 0xD7),
	UPDATE_HONOUR((short) 0xDB),
	AZWAN_UNKNOWN((short) 0xDA),
	AZWAN_RESULT((short) 0xDB),
	AZWAN_KILLED((short) 0xDC),
	CIRCULATOR_ON_LEVEL((short) 0xDD),
	SILENT_CRUSADE_MSG((short) 0xDE),
	SILENT_CRUSADE_SHOP((short) 0xDF),
	CASSANDRAS_COLLECTION((short) 0xEA),
	MAPLE_POINT((short) 0xF1),
	SET_OBJECT_STATE((short) 0xEF),
	POPUP((short) 0xF0),
	MINIMAP_ARROW((short) 0xF4),
	UNLOCK_CHARGE_SKILL((short) 0xFA),
	LOCK_CHARGE_SKILL((short) 0xFB),
	CANDY_RANKING((short) 0xFF),
	GUILD_SEARCH_RESULT((short) 0x106),
	ATTENDANCE((short) 0x10A),
	EVENT_LIST((short) 0x114),
	MESSENGER_OPEN((short) 0x115),
	EVENT_CROWN((short) 0x118),
	MAGIC_WHEEL((short) 0x125),
	BBS_OPERATION((short) 0x180),
	REWARD((short) 0x1A3),
	SKILL_MACRO((short) 0x1AB),
	EXPAND_PENDANT_SLOTS((short) 0x12B),
	WARP_TO_MAP((short) 0x1AC),
	FARM_OPEN((short) 0x1A0),
	CS_OPEN((short) 0x1A1),
	REMOVE_BG_LAYER((short) 0x12E),
	SET_MAP_OBJECT_VISIBLE((short) 0x12F),
	RESET_SCREEN((short) 0x12C),
	MAP_BLOCKED((short) 0x12D),
	SERVER_BLOCKED((short) 0x1B1),
	PARTY_BLOCKED((short) 0x131),
	SHOW_EQUIP_EFFECT((short) 0x132),
	MULTICHAT((short) 0x1B4),
	WHISPER((short) 0x1B6),
	SPOUSE_CHAT((short) 0x1B7),
	BOSS_ENV((short) 0x1B8),
	MOVE_ENV((short) 0x1BA),
	UPDATE_ENV((short) 0x1BB),
	MAP_EFFECT((short) 0x1B4),
	CASH_SONG((short) 0x14B),
	GM_EFFECT((short) 0x141),
	OX_QUIZ((short) 0x142),
	GMEVENT_INSTRUCTIONS((short) 0x143),
	CLOCK((short) 0x1C5),
	BOAT_MOVE((short) 0x1C6),
	BOAT_STATE((short) 0x1C7),
	STOP_CLOCK((short) 0x1C8),
	ARIANT_SCOREBOARD((short) 0x14A),
	PYRAMID_UPDATE((short) 0x14E),
	PYRAMID_RESULT((short) 0x14F),
	QUICK_SLOT((short) 0x999),
	SMART_MOB_NOTICE((short) 0x158),
	MOVE_PLATFORM((short) 0x156),
	PYRAMID_KILL_COUNT((short) 0x154),
	PVP_INFO((short) 0x157),
	DIRECTION_STATUS((short) 0x1DC),
	GAIN_FORCE((short) 0x1DD),
	INTRUSION((short) 0x162),
	ACHIEVEMENT_RATIO((short) 0x159),
	QUICK_MOVE((short) 0x1DF),
	DIFFERENT_IP((short) 0x1DD),
	SPAWN_OBTACLE_ATOM((short) 0x15D),
	SPAWN_PLAYER((short) 0x204),
	REMOVE_PLAYER_FROM_MAP((short) 0x205),
	CHATTEXT((short) 0x206),
	CHALKBOARD((short) 0x16B),
	MINI_ROOM_BALLOON((short) 0x16C),
	SHOW_CONSUME_EFFECT((short) 0x16D),
	SHOW_SCROLL_EFFECT((short) 0x1F5),
	SHOW_SOUL_EFFECT((short) 0x1F7),
	SHOW_MAGNIFYING_EFFECT((short) 0x20E),
	SHOW_POTENTIAL_RESET((short) 0x20F),
	SHOW_FIREWORKS_EFFECT((short) 0x172),
	SHOW_NEBULITE_EFFECT((short) 0x266),
	SHOW_FUSION_EFFECT((short) 0x267),
	PVP_ATTACK((short) 0x140),
	PVP_MIST((short) 0x141),
	PVP_COOL((short) 0x142),
	TESLA_TRIANGLE((short) 0x999),
	FOLLOW_CHARACTER((short) 0x21C),
	SHOW_PQ_REWARD((short) 0x15F),
	CRAFT_EFFECT((short) 0x21E),
	CRAFT_COMPLETE((short) 0x21F),
	CRAFT_SKILL_EFFECT((short) 0x220),
	HARVESTED((short) 0x221),
	SET_DAMAGE_SKIN((short) 0x228),
	SET_PREMIUM_DAMAGE_SKIN((short) 0x229),
	SET_SOUL_EFFECT((short) 0x22A),
	CANCEL_CHAIR((short) 0x22B),
	PLAYER_DAMAGED((short) 0x165),
	NETT_PYRAMID((short) 0x166),
	SET_PHASE((short) 0x167),
	PAMS_SONG((short) 0x168),
	SPAWN_PET((short) 0x243),
	MOVE_PET((short) 0x244),
	PET_ACTION((short) 0x245),
	PET_CHAT((short) 0x196),
	PET_NAMECHANGE((short) 0x197),
	PET_EXCEPTION_LIST((short) 0x22C),
	PET_COLOR((short) 0x199),
	PET_SIZE((short) 0x19A),
	PET_COMMAND((short) 0x231),
	DRAGON_SPAWN((short) 0x24E),
	INNER_ABILITY_RESET_MSG((short) 0x175),
	DRAGON_MOVE((short) 0x24F),
	DRAGON_REMOVE((short) 0x251),
	ANDROID_SPAWN((short) 0x252),
	ANDROID_MOVE((short) 0x253),
	ANDROID_EMOTION((short) 0x254),
	ANDROID_UPDATE((short) 0x255),
	ANDROID_DEACTIVATED((short) 0x256),
	SPAWN_FAMILIAR((short) 0x26E),
	MOVE_FAMILIAR((short) 0x1AB),
	TOUCH_FAMILIAR((short) 0x1AC),
	ATTACK_FAMILIAR((short) 0x1AD),
	RENAME_FAMILIAR((short) 0x2F3),
	RESPAWN_FAMILIAR((short) 0x273),
	UPDATE_FAMILIAR((short) 0x274),
	HAKU_CHANGE_1((short) 0x1A4),
	HAKU_TRANSFORM_EFFECT((short) 0x1A7),
	HAKU_MOVE((short) 0x242),
	HAKU_UNK((short) 0x1B3),
	HAKU_CHANGE((short) 0x1B4),
	SPAWN_HAKU((short) 0x1B7),
	MOVE_PLAYER((short) 0x276),
	CLOSE_RANGE_ATTACK((short) 0x277),
	RANGED_ATTACK((short) 0x278),
	MAGIC_ATTACK((short) 0x279),
	ENERGY_ATTACK((short) 0x27A),
	SKILL_EFFECT((short) 0x27B),
	MOVE_ATTACK((short) 0x27C),
	CANCEL_SKILL_EFFECT((short) 0x27D),
	DAMAGE_PLAYER((short) 0x27E),
	FACIAL_EXPRESSION((short) 0x27F),
	SHOW_EFFECT((short) 0x280),
	SHOW_TITLE((short) 0x282),
	ANGELIC_CHANGE((short) 0x283),
	CASH_EFFECT((short) 0x284),
	SHOW_CHAIR((short) 0x288),
	UPDATE_CHAR_LOOK((short) 0x289),
	SHOW_FOREIGN_EFFECT((short) 0x28A),
	GIVE_FOREIGN_BUFF((short) 0x28B),
	CANCEL_FOREIGN_BUFF((short) 0x28C),
	UPDATE_PARTYMEMBER_HP((short) 0x28D),
	LOAD_GUILD_NAME((short) 0x28E),
	LOAD_GUILD_ICON((short) 0x28F),
	LOAD_TEAM((short) 0x290),
	SHOW_HARVEST((short) 0x291),
	PVP_HP((short) 0x1D7),
	MOVE_SCREEN((short) 0x288),
	DIRECTION_FACIAL_EXPRESSION((short) 0x288),
	SHOW_SPECIAL_EFFECT((short) 0x2AC),
	CURRENT_MAP_WARP((short) 0x28B),
	MESOBAG_SUCCESS((short) 0x28D),
	MESOBAG_FAILURE((short) 0x28E),
	UPDATE_QUEST_INFO((short) 0x2B1),
	R_MESOBAG_SUCCESS((short) 0x1EB),
	R_MESOBAG_FAILURE((short) 0x1EC),
	MAP_FADE((short) 0x1F0),
	MAP_FADE_FORCE((short) 0x1F1),
	HP_DECREASE((short) 0x1F0),
	PLAYER_HINT((short) 0x1F2),
	PLAY_EVENT_SOUND((short) 0x1F3),
	PLAY_MINIGAME_SOUND((short) 0x1F4),
	MAKER_SKILL((short) 0x1F5),
	OPEN_UI((short) 0x2BA),
	OPEN_UI_OPTION((short) 0x2BD),
	INTRO_LOCK((short) 0x2BE),
	INTRO_ENABLE_UI((short) 0x2BF),
	INTRO_DISABLE_UI((short) 0x2C0),
	SUMMON_HINT((short) 0x29E),
	SUMMON_HINT_MSG((short) 0x29F),
	ARAN_COMBO((short) 0x2A0),
	ARAN_COMBO_RECHARGE((short) 0x2A1),
	RANDOM_EMOTION((short) 0x205),
	RADIO_SCHEDULE((short) 0x206),
	OPEN_SKILL_GUIDE((short) 0x207),
	GAME_MSG((short) 0x209),
	GAME_MESSAGE((short) 0x2CD),
	BUFF_ZONE_EFFECT((short) 0x20C),
	GO_CASHSHOP_SN((short) 0x20D),
	DAMAGE_METER((short) 0x20E),
	TIME_BOMB_ATTACK((short) 0x20F),
	FOLLOW_MOVE((short) 0x2D2),
	FOLLOW_MSG((short) 0x2D3),
	AP_SP_EVENT((short) 0x215),
	QUEST_GUIDE_NPC((short) 0x214),
	REGISTER_FAMILIAR((short) 0x35B),
	FAMILIAR_MESSAGE((short) 0x219),
	CREATE_ULTIMATE((short) 0x21C),
	HARVEST_MESSAGE((short) 0x2D6),
	RUNE_ACTION((short) 0x2D7),
	SHOW_MAP_NAME((short) 0x999),
	OPEN_BAG((short) 0x21D),
	DRAGON_BLINK((short) 0x21E),
	PVP_ICEGAGE((short) 0x219),
	DIRECTION_INFO((short) 0x2DB),
	REISSUE_MEDAL((short) 0x2DC),
	PLAY_MOVIE((short) 0x2BB),
	CAKE_VS_PIE((short) 0x225),
	PHANTOM_CARD((short) 0x2E1),
	LUMINOUS_COMBO((short) 0x22A),
	MOVE_SCREEN_X((short) 0x199),
	MOVE_SCREEN_DOWN((short) 0x19A),
	CAKE_PIE_INSTRUMENTS((short) 0x19B),
	REVIVE_UI((short) 0x2F3),
	SHOW_FARM_PIC((short) 0x2FB),
	COOLDOWN((short) 0x333),
	SPAWN_SUMMON((short) 0x379),
	REMOVE_SUMMON((short) 0x37A),
	MOVE_SUMMON((short) 0x37B),
	SUMMON_ATTACK((short) 0x37C),
	SUMMON_PVP_ATTACK((short) 0x37D),
	SUMMON_SET_REFERENCE((short) 0x37E),
	SUMMON_SKILL((short) 0x37F),
	SUMMON_SKILL_PVP((short) 0x380),
	SUMMON_UPDATE_HP((short) 0x381),
	SUMMON_ATTACK_DONE((short) 0x382),
	SUMMON_ATTACK_RESIST((short) 0x383),
	SUMMON_CHANGE_ACTION((short) 0x384),
	SUMMON_ASSIST_ATTACK_REQUEST((short) 0x385),
	SUMMON_ATTACK_ACTIVE((short) 0x386),
	DAMAGE_SUMMON((short) 0x387),
	SPAWN_MONSTER((short) 0x389),
	KILL_MONSTER((short) 0x38A),
	SPAWN_MONSTER_CONTROL((short) 0x38B),
	MOVE_MONSTER((short) 0x38F),
	MOVE_MONSTER_RESPONSE((short) 0x390),
	APPLY_MONSTER_STATUS((short) 0x392),
	CANCEL_MONSTER_STATUS((short) 0x393),
	DAMAGE_MONSTER((short) 0x36A),
	SKILL_EFFECT_MOB((short) 0x395),
	TELE_MONSTER((short) 0x999),
	MONSTER_SKILL((short) 0x38F),
	MONSTER_CRC_CHANGE((short) 0x399),
	SHOW_MONSTER_HP((short) 0x39B),
	SHOW_MAGNET((short) 0x287),
	ITEM_EFFECT_MOB((short) 0x288),
	CATCH_MONSTER((short) 0x289),
	MONSTER_PROPERTIES((short) 0x1B9),
	REMOVE_TALK_MONSTER((short) 0x1BA),
	TALK_MONSTER((short) 0x39D),
	CYGNUS_ATTACK((short) 0x28F),
	MONSTER_RESIST((short) 0x290),
	MOB_REACTION((short) 0x3C9),
	MOB_TO_MOB_DAMAGE((short) 0x1C6),
	AZWAN_MOB_TO_MOB_DAMAGE((short) 0x1C9),
	AZWAN_SPAWN_MONSTER((short) 0x22C),
	AZWAN_KILL_MONSTER((short) 0x22D),
	AZWAN_SPAWN_MONSTER_CONTROL((short) 0x999),
	SPAWN_NPC((short) 0x3D5),
	REMOVE_NPC((short) 0x3D6),
	NPC_UNKNOWN((short) 0x3D7),
	SPAWN_NPC_REQUEST_CONTROLLER((short) 0x3D8),
	NPC_ACTION((short) 0x3D9),
	NPC_UPDATE_LIMITED_INFO((short) 0x3DA),
	NPC_SET_FORCE_MOVE((short) 0x3DD),
	NPC_TOGGLE_VISIBLE((short) 0x3E1),
	INITIAL_QUIZ((short) 0x2A3),
	NPC_RESET_SPECIAL_ACTION((short) 0x3E4),
	NPC_SET_SPECIAL_ACTION((short) 0x3E8),
	NPC_SET_SCRIPT((short) 0x3E9),
	RED_LEAF_HIGH((short) 0x2A7),
	SPAWN_HIRED_MERCHANT((short) 0x3EB),
	DESTROY_HIRED_MERCHANT((short) 0x3EC),
	UPDATE_HIRED_MERCHANT((short) 0x3ED),
	DROP_ITEM_FROM_MAPOBJECT((short) 0x3EE),
	REMOVE_ITEM_FROM_MAP((short) 0x3F0),
	SPAWN_KITE_ERROR((short) 0x3F1),
	SPAWN_KITE((short) 0x3F2),
	DESTROY_KITE((short) 0x3F3),
	SPAWN_MIST((short) 0x3F4),
	REMOVE_MIST((short) 0x3F6),
	SPAWN_MYSTIC_DOOR((short) 0x3F7),
	REMOVE_MYSTIC_DOOR((short) 0x3F8),
	SPAWN_MECH_DOOR((short) 0x3FC),
	USE_MECH_DOOR((short) 0x3FD),
	REMOVE_MECH_DOOR((short) 0x3FE),
	REACTOR_HIT((short) 0x3FF),
	REACTOR_MOVE((short) 0x3FA),
	REACTOR_SPAWN((short) 0x401),
	REACTOR_DESTROY((short) 0x405),
	SPAWN_EXTRACTOR((short) 0x3C8),
	REMOVE_EXTRACTOR((short) 0x3C9),
	ROLL_SNOWBALL((short) 0x2C7),
	HIT_SNOWBALL((short) 0x2D0),
	SNOWBALL_MESSAGE((short) 0x2D1),
	LEFT_KNOCK_BACK((short) 0x2C4),
	HIT_COCONUT((short) 0x2C5),
	COCONUT_SCORE((short) 0x2C6),
	MOVE_HEALER((short) 0x2C7),
	PULLEY_STATE((short) 0x2C8),
	MONSTER_CARNIVAL_START((short) 0x2C9),
	MONSTER_CARNIVAL_OBTAINED_CP((short) 0x2CA),
	MONSTER_CARNIVAL_STATS((short) 0x2CB),
	MONSTER_CARNIVAL_SUMMON((short) 0x2CD),
	MONSTER_CARNIVAL_MESSAGE((short) 0x2CE),
	MONSTER_CARNIVAL_DIED((short) 0x2CF),
	MONSTER_CARNIVAL_LEAVE((short) 0x2D0),
	MONSTER_CARNIVAL_RESULT((short) 0x2D1),
	MONSTER_CARNIVAL_RANKING((short) 0x2D2),
	ARIANT_SCORE_UPDATE((short) 0x300),
	SHEEP_RANCH_INFO((short) 0x301),
	SHEEP_RANCH_CLOTHES((short) 0x999),
	WITCH_TOWER((short) 0x999),
	EXPEDITION_CHALLENGE((short) 0x999),
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
	PVP_ICEKNIGHT((short) 0x313),
	HORNTAIL_SHRINE((short) 0x2E1),
	CAPTURE_FLAGS((short) 0x2E2),
	CAPTURE_POSITION((short) 0x2E3),
	CAPTURE_RESET((short) 0x2E4),
	PINK_ZAKUM_SHRINE((short) 0x2E5),
	NPC_TALK((short) 0x4FE),
	OPEN_NPC_SHOP((short) 0x4FF),
	CONFIRM_SHOP_TRANSACTION((short) 0x500),
	OPEN_STORAGE((short) 0x519),
	MERCH_ITEM_MSG((short) 0x4D0),
	MERCH_ITEM_STORE((short) 0x4D1),
	MESSENGER((short) 0x51F),
	RPS_GAME((short) 0x4D2),
	PLAYER_INTERACTION((short) 0x520),
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
	PACKAGE_OPERATION((short) 0x353),
	CS_CHARGE_CASH((short) 0x2CA),
	CS_EXP_PURCHASE((short) 0x23B),
	GIFT_RESULT((short) 0x23C),
	CHANGE_NAME_CHECK((short) 0x23D),
	CHANGE_NAME_RESPONSE((short) 0x23E),
	CS_UPDATE((short) 0x534),
	CS_OPERATION((short) 0x535),
	CS_MESO_UPDATE((short) 0x35F),
	CASH_SHOP((short) 0x54C),
	CASH_SHOP_UPDATE((short) 0x373),
	GACHAPON_STAMPS((short) 0x253),
	FREE_CASH_ITEM((short) 0x254),
	CS_SURPRISE((short) 0x255),
	XMAS_SURPRISE((short) 0x256),
	ONE_A_DAY((short) 0x258),
	NX_SPEND_GIFT((short) 0x25A),
	RECEIVE_GIFT((short) 0x25A),
	KEYMAP((short) 0x587),
	PET_AUTO_HP((short) 0x377),
	PET_AUTO_MP((short) 0x378),
	PET_AUTO_CURE((short) 0x379),
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
	FARM_ITEM_GAIN((short) 0x5CE),
	HARVEST_WARU((short) 0x35A),
	FARM_MONSTER_GAIN((short) 0x35B),
	FARM_INFO((short) 0x5DE),
	FARM_MONSTER_INFO((short) 0x369),
	FARM_QUEST_DATA((short) 0x36A),
	FARM_QUEST_INFO((short) 0x5E1),
	FARM_MESSAGE((short) 0x36C),
	UPDATE_MONSTER((short) 0x36D),
	AESTHETIC_POINT((short) 0x36E),
	UPDATE_WARU((short) 0x36F),
	FARM_EXP((short) 0x5EA),
	FARM_PACKET4((short) 0x375),
	QUEST_ALERT((short) 0x5ED),
	FARM_PACKET8((short) 0x378),
	FARM_FRIENDS_BUDDY_REQUEST((short) 0x37B),
	FARM_FRIENDS((short) 0x37C),
	FARM_USER_INFO((short) 0x388),
	FARM_AVATAR((short) 0x5FE),
	FRIEND_INFO((short) 0x38D),
	FARM_RANKING((short) 0x38F),
	SPAWN_FARM_MONSTER1((short) 0x393),
	SPAWN_FARM_MONSTER2((short) 0x394),
	RENAME_MONSTER((short) 0x395),
	STRENGTHEN_UI((short) 0x408),
	DEATH_COUNT((short) 0x2CF),
	REDIRECTOR_COMMAND((short) 0x1337),
	FARM_NAME_RESPONSE((short) 0x5EB);

    private short opcode;

    @Override
    public void setOpcode(short code) {
        this.opcode = code;
    }

    @Override
    public short getOpcode() {
        return opcode;
    }
    
    private SendPacketOpcode(short code) {
        this.opcode = code;
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
            if (opcode.getOpcode() == value) {
                return opcode.name();
            }
        }
        return "UNKNOWN";
    }

    @SuppressWarnings("incomplete-switch")
	public static boolean isSpam(SendPacketOpcode opcode) {
        switch (opcode) {
            case AUTH_RESPONSE:
            case PING:
            case LOGIN_AUTH_RESPONSE1:
			case LOGIN_AUTH_RESPONSE2:
			case LOGIN_AUTH_RESPONSE3:
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
            //case SHOW_SPECIAL_EFFECT:
            case DROP_ITEM_FROM_MAPOBJECT:
            case REMOVE_ITEM_FROM_MAP:
            //case UPDATE_PARTYMEMBER_HP:
            case DAMAGE_PLAYER:
            case SHOW_MONSTER_HP:
            case CLOSE_RANGE_ATTACK:
            case RANGED_ATTACK:
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
