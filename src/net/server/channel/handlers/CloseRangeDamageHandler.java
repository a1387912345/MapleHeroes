package net.server.channel.handlers;

import java.lang.ref.WeakReference;

import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.AbstractMaplePacketHandler;
import net.RecvPacketOpcode;
import net.channel.ChannelServer;
import net.channel.handler.AttackInfo;
import net.channel.handler.AttackType;
import net.channel.handler.DamageParse;
import net.channel.handler.PlayerHandler;
import server.MapleStatEffect;
import server.Randomizer;
import server.Timer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball;
import tools.AttackPair;
import tools.Pair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket.AngelicPacket;

public class CloseRangeDamageHandler extends AbstractMaplePacketHandler {
	
	public CloseRangeDamageHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(LittleEndianAccessor lea, MapleClient c) {
		final MapleCharacter chr = c.getPlayer();
		final boolean energy = getRecvOpcode() == RecvPacketOpcode.PASSIVE_ENERGY;
		
		if ((chr == null) || ((energy) && (chr.getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null) && (chr.getBuffedValue(MapleBuffStat.BODY_PRESSURE) == null) && (chr.getBuffedValue(MapleBuffStat.DARK_AURA) == null) && (chr.getBuffedValue(MapleBuffStat.TORNADO) == null) && (chr.getBuffedValue(MapleBuffStat.SUMMON) == null) && (chr.getBuffedValue(MapleBuffStat.RAINING_MINES) == null) && (chr.getBuffedValue(MapleBuffStat.ASURA) == null) && (chr.getBuffedValue(MapleBuffStat.TELEPORT_MASTERY) == null))) {
            return;
        }
        if ((chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        System.out.println(lea.toString());
        AttackInfo attack = DamageParse.parseDmgM(lea, chr);
        if (attack == null) {
            c.getSession().write(CWvsContext.enableActions());
            return;
        }
        final boolean mirror = chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null;
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
        int attackCount = (shield != null) && (shield.getItemId() / 10000 == 134) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;

        String dmg = "";
        for (AttackPair ae : attack.allDamage) {
            for (Pair att : ae.attack) {
                dmg += att.getLeft();
                dmg += ",";
            }
        }
        if (!dmg.isEmpty()) {
//            chr.dropMessage(-1, "Damage: " + dmg);//debug mode
        }
        System.out.println("closeRange debug 0");
        System.out.println(attack.skill);
        if (attack.skill != 0) {
            //chr.dropMessage(-1, "Attack Skill: " + attack.skill);//debug mode
            skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
            if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
                c.getSession().write(CWvsContext.enableActions());
                return;
            }
            System.out.println("closeRange debug 1");
            if (GameConstants.isDemonAvenger(chr.getJob())) {
                int exceedMax = chr.getSkillLevel(31220044) > 0 ? 20 : 20;
             //   chr.showInfo("Info", false, "exceedMax;" + exceedMax);
                if (chr.getExceed() + 1 > exceedMax) {
                    chr.setExceed((short) exceedMax);
                } else {
                    chr.gainExceed((short) 1);
                }
            }
            if (GameConstants.isExceedAttack(skill.getId())) {
                chr.handleExceedAttack(skill.getId());
            }
            switch (attack.skill) {
                case 101001100:
                case 101101100:
                case 101111100:
                case 101121100:
                    chr.zeroChange(false);
                    break;
                case 101001200:
                case 101101200:
                case 101111200:
                case 101121200:
                    chr.zeroChange(true);
                    break;

            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "You may not use that here.");
                                return;
                            }
                        }
                    }
                }
            }

             
            if (GameConstants.isAngelicBuster(chr.getJob())) {
                int Recharge = effect.getOnActive();
                if (Recharge > -1) {
                    if (Randomizer.isSuccess(Recharge)) {
                        c.getSession().write(AngelicPacket.unlockSkill());
                        c.getSession().write(AngelicPacket.showRechargeEffect());
                    } else {
                        if (c.getPlayer().isGM()) {
                            c.getSession().write(AngelicPacket.unlockSkill());
//                    c.getSession().write(AngelicPacket.showRechargeEffect());
                        } else {
                            c.getSession().write(AngelicPacket.lockSkill(attack.skill));
                        }
                    }
                } else {
                    if (c.getPlayer().isGM()) {
                        c.getSession().write(AngelicPacket.unlockSkill());
//                    c.getSession().write(AngelicPacket.showRechargeEffect());
                    } else {
                        c.getSession().write(AngelicPacket.lockSkill(attack.skill));
                    }
                }
            }
            maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0D;
            attackCount = effect.getAttackCount();

            if ((effect.getCooldown(chr) > 0) && (!chr.isGM()) && (!energy)) {
                if (chr.skillisCooling(attack.skill)) {
                    c.getSession().write(CWvsContext.enableActions());
                    return;
                }
                c.getSession().write(CField.skillCooldown(attack.skill, effect.getCooldown(chr)));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
            }
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 1, effect);
        attackCount *= (mirror ? 2 : 1);
        if (!energy) {
            if (((chr.getMapId() == 109060000) || (chr.getMapId() == 109060002) || (chr.getMapId() == 109060004)) && (attack.skill == 0)) {
                MapleSnowball.MapleSnowballs.hitSnowball(chr);
            }

            int numFinisherOrbs = 0;
            Integer comboBuff = chr.getBuffedValue(MapleBuffStat.COMBO);

            if (PlayerHandler.isFinisher(attack.skill) > 0) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff.intValue() - 1;
                }
                if (numFinisherOrbs <= 0) {
                    return;
                }
                chr.handleOrbconsume(PlayerHandler.isFinisher(attack.skill));
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, energy, chr.getLevel(), chr.getStat().passive_mastery(), attack.unk, attack.charge), false);
        }
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final Skill skil2 = skill;
                final int skillLevel2 = skillLevel;
                final int attackCount2 = attackCount;
                final double maxdamage2 = maxdamage;
                final MapleStatEffect eff2 = effect;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                Timer.CloneTimer.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (!clone.isHidden()) {
                            clone.getMap().broadcastMessage(CField.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge));
                        } else {
                            clone.getMap().broadcastGMMessage(clone, CField.closeRangeAttack(clone.getId(), attack2.tbyte, attack2.skill, skillLevel2, attack2.display, attack2.speed, attack2.allDamage, energy, clone.getLevel(), clone.getStat().passive_mastery(), attack2.unk, attack2.charge), false);
                        }
                        DamageParse.applyAttack(attack2, skil2, chr, attackCount2, maxdamage2, eff2, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
                    }
                }, 500 * i + 500);
            }
        }
        int bulletCount = 1;
        switch (attack.skill) {
            case 1201011:
                bulletCount = effect.getAttackCount();
                DamageParse.applyAttack(attack, skill, chr, skillLevel, maxdamage, effect, AttackType.NON_RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.applyAttackMagic(attack, skill, chr, effect, maxdamage);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }
	}

}
