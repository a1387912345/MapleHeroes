/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.server.channel.handler.deprecated;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import client.character.MapleCharacter;
import net.netty.MaplePacketReader;
import server.maps.AnimatedMapleMapObject;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;
import server.movement.StaticLifeMovement;

public class MovementParse {

    //1 = player, 2 = mob, 3 = pet, 4 = summon, 5 = dragon
    public static List<LifeMovementFragment> parseMovement(final MaplePacketReader inPacket, final int kind, final Point startPos) {
        return parseMovement(inPacket, kind, startPos, null);
    }

    public static List<LifeMovementFragment> parseMovement(final MaplePacketReader inPacket, final int kind, final Point startPos, final MapleCharacter chr) {
        final List<LifeMovementFragment> res = new ArrayList<>();
        final byte numCommands = inPacket.readByte();
        short xposition = 0, yposition = 0, xvelocity = 0, yvelocity = 0, foothold = 0, xoffset = 0, yoffset = 0, unknown = 0, duration = 0;
        byte moveAction = 0, unknown2 = 0, wui = 0;

        for (byte i = 0; i < numCommands; i++) {
            final byte command = inPacket.readByte();
            switch (command) {
	            case 0:
	            case 8:
	            case 15:
	            case 16:
	            case 18:
	            case 66:
	            case 67:
	            case 68:
                    xposition = inPacket.readShort();
                    yposition = inPacket.readShort();
                    xvelocity = inPacket.readShort();
                    yvelocity = inPacket.readShort();
                    foothold = inPacket.readShort();
                    if (command == 15 || command == 16) {
                    	unknown = inPacket.readShort();
                    }
                    xoffset = inPacket.readShort();
                    yoffset = inPacket.readShort();
                    break;
	            case 55:
	            case 65:
	            case 82:
	            	xposition = inPacket.readShort();
                    yposition = inPacket.readShort();
                    xvelocity = inPacket.readShort();
                    yvelocity = inPacket.readShort();
                    foothold = inPacket.readShort();
	            	break;
	            case 1:
	            case 2:
	            case 17:
	            case 20:
	            case 21:
	            case 23:
	            case 61:
	            case 62:
	            case 63:
	            case 64:
	            	if(startPos != null) {
		            	xposition = (short) startPos.getX();
		            	yposition = (short) startPos.getY();
	            	}
	            	xvelocity = inPacket.readShort();
                    yvelocity = inPacket.readShort();
                    if (command == 20 || command == 21) {
                        unknown = inPacket.readShort();
                    }
                    break;
	            case 28:
	            case 29:
	            case 30:
	            case 31:
	            case 32:
	            case 33:
	            case 34:
	            case 35:
	            case 36:
	            case 37:
	            case 38:
	            case 39:
	            case 40:
	            case 41:
	            case 42:
	            case 43:
	            case 44:
	            case 45:
	            case 46:
	            case 47:
	            case 48:
	            case 49:
	            case 50:
	            case 56:
	            case 57:
	            case 58:
	            case 59:
	            case 69:
	            case 70:
	            case 71:
	            case 73:
	            case 78:
	            case 80:
	            	if(startPos != null) {
		            	xposition = (short) startPos.getX();
		            	yposition = (short) startPos.getY();
	            	}
                    break;
	            case 3:
	            case 4:
	            case 5:
	            case 6:
	            case 7:
	            case 9:
	            case 10:
	            case 11:
	            case 13:
	            case 25:
	            case 26:
	            case 51:
	            case 52:
	            case 53:
	            case 60:
	            case 75:
	            case 76:
	            case 77:
	            case 79: 
	            	xposition = inPacket.readShort();
                    yposition = inPacket.readShort();
                    foothold = inPacket.readShort();
                    break;
                case 14:
                	if(startPos != null) {
		            	xposition = (short) startPos.getX();
		            	yposition = (short) startPos.getY();
	            	}
                	xvelocity = inPacket.readShort();
                    yvelocity = inPacket.readShort();
                    unknown = inPacket.readShort();
                    break;
                case 22:
                	xposition = inPacket.readShort();
                    yposition = inPacket.readShort();
                    xvelocity = inPacket.readShort();
                    yvelocity = inPacket.readShort();
                    break;
                case 12: {
                	if(startPos != null) {
		            	xposition = (short) startPos.getX();
		            	yposition = (short) startPos.getY();
	            	}
                    wui = inPacket.readByte();
                    break;
                }
                default:
                   // if (chr.isGM()) {
                        //chr.showInfo("Movement", false, "Failed to read movement type " + command);
                    //}
//                    System.out.println("Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + mpr.toString(true));
                    //FileoutputUtil.log(FileoutputUtil.Movement_Log, "Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + mpr.toString(true) + "\r\n");
                    //return null;
            }
            
            if (command != 12) {
		        moveAction = inPacket.readByte(); // stance
		        duration = inPacket.readShort();
		        unknown2 = inPacket.readByte(); // unknown new byte
            }
		
	        final StaticLifeMovement mov = new StaticLifeMovement(command, new Point(xposition, yposition), duration, moveAction);
	        mov.setFoothold(foothold);
	        mov.setUnknown(unknown);
	        mov.setUnknown2(unknown2);
	        mov.setPixelsPerSecond(new Point(xvelocity, yvelocity));
	        mov.setOffset(new Point(xoffset, yoffset));
	
	        if (command == 12) {
	        	mov.setWui(wui);
	        }
	        res.add(mov);    
        }
        if (numCommands != res.size()) {
            return null; // Probably hack
        }
        return res;
    }

    /*public static List<LifeMovementFragment> parseMovement2(final LittleEndianAccessor mpr, final int kind, MapleCharacter chr) {
        final List<LifeMovementFragment> res = new ArrayList<>();
        final byte numCommands = mpr.readByte();

        for (byte i = 0; i < numCommands; i++) {
            final byte command = mpr.readByte();
            switch (command) {
                case -1:
                case 12:
                case 14:
                case 15:
                case 67:
                case 68: //Vertical Grapple
                {
                    int read;
                    if (command < 0) {
                        read = MovementTypes.Move_Map_Neg[command * -1];
                    } else {
                        read = MovementTypes.Move_Map_Pos[command];
                    }

                    final UnknownMovement mm = new UnknownMovement(command, mpr.read(read));
                    res.add(mm);
                    break;
                }
                case 0:
                case 7:
                case 16:
                case 44:
                case 45:
                case 46: {
                    final short xpos = (short) mpr.readShort();
                    final short ypos = mpr.readShort();
                    final short xwobble = mpr.readShort();
                    final short ywobble = mpr.readShort();
                    final short unk = mpr.readShort();
                    short fh = 0, xoffset = 0, yoffset = 0;

                    if (command != 44) {
                        xoffset = mpr.readShort();
                        yoffset = mpr.readShort();
                    }
                    final byte newstate = mpr.readByte();
                    final short duration = mpr.readShort();

                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration, newstate);
                    alm.setUnk(unk);
                    alm.setFh(fh);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    alm.setOffset(new Point(xoffset, yoffset));

                    res.add(alm);
                    break;
                }
                case 1:
                case 2:
                case 18:
                case 19:
                case 21:
                case 40:
                case 41:
                case 42:
                case 43: {
                    final short xmod = (short) mpr.readShort();
                    final short ymod = mpr.readShort();
                    short unk = 0;
                    if (command == 18 || command == 19) {
                        unk = mpr.readShort();
                    }
                    final byte newstate = mpr.readByte();
                    final short duration = mpr.readShort();

                    final RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xmod, ymod), duration, newstate);
                    rlm.setUnk(unk);
                    res.add(rlm);
                    break;
                }
                case 17: // special?...final charge aran
                case 22: // idk
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case -106: {
                    final byte newstate = mpr.readByte();
                    final short unk = mpr.readShort();

                    final GroundMovement am = new GroundMovement(command, new Point(0, 0), unk, newstate);

                    res.add(am);
                    break;
                }
                case 3:
                case 4:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 13: {
                    final short xpos = (short) mpr.readShort();
                    final short ypos = mpr.readShort();
                    final short fh = mpr.readShort();
                    final byte newstate = mpr.readByte();
                    final short duration = mpr.readShort();

                    final TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), duration, newstate);
                    tm.setFh(fh);

                    res.add(tm);
                    break;
                }
                case 20: {
                    final short xpos = mpr.readShort();
                    final short ypos = mpr.readShort();
                    final short xoffset = mpr.readShort();
                    final short yoffset = mpr.readShort();
                    final byte newstate = mpr.readByte();
                    final short duration = mpr.readShort();

                    final BounceMovement bm = new BounceMovement(command, new Point(xpos, ypos), duration, newstate);
                    bm.setOffset(new Point(xoffset, yoffset));

                    res.add(bm);
                    break;
                }
                case 11: { // Update Equip or Dash
                    res.add(new ChangeEquipSpecialAwesome(command, mpr.readByte()));
                    break;
                }
                default:
//                    System.out.println("Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + mpr.toString(true));
                    FileoutputUtil.log(FileoutputUtil.Movement_Log, "Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + mpr.toString(true));
                    return null;
            }
        }
        if (chr != null) {
            String movStr = ";";
            for (LifeMovementFragment mov : res) {
                if (mov instanceof LifeMovement) {
                    movStr += ((LifeMovement) mov).getType() + ";";
                }
            }
        }
        if (numCommands != res.size()) {
            return null; // Probably hack
        }
        return res;
    }*/

    public static void updatePosition(final List<LifeMovementFragment> movement, final AnimatedMapleMapObject target, final int yoffset) {
        if (movement == null) {
            return;
        }
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof StaticLifeMovement) {
                    final Point position = ((LifeMovement) move).getPosition();
                    position.y += yoffset;
                    target.setPosition(position);
                }
                target.setStance(((LifeMovement) move).getMoveAction());
            }
        }
    }
}
