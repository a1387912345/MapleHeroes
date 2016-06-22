/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.movement;

import java.awt.Point;

import net.netty.MaplePacketWriter;

/**
 *
 * @author Itzik
 */
public class StaticLifeMovement extends AbstractLifeMovement {

    private Point pixelsPerSecond, offset;
    private short foothold, unknown;
    private byte unknown2, wui;

    public StaticLifeMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    public void setOffset(Point wobble) {
        this.offset = wobble;
    }

    public void setFoothold(short foothold) {
        this.foothold = foothold;
    }

    public void setUnknown(short unknown) {
        this.unknown = unknown;
    }

    public short getUnknown() {
        return unknown;
    }
    
    public void setUnknown2(byte unknown2) {
    	this.unknown2 = unknown2;
    }
    public byte getUnknown2() {
    	return unknown2;
    }

    public void setWui(byte wui) {
        this.wui = wui;
    }

    public void defaulted() {
        unknown = 0;
        foothold = 0;
        pixelsPerSecond = new Point(0, 0);
        offset = new Point(0, 0);
        wui = 0;
    }

    @Override
    public void serialize(MaplePacketWriter mlew) {
        mlew.write(getType());
        switch (getType()) {
            case 0:
            case 8:
            case 15:
            case 16:
            case 18:
            case 66:
            case 67:
            case 68:
                mlew.writePos(getPosition());
                mlew.writePos(pixelsPerSecond);
                mlew.writeShort(foothold);
                if (getType() == 15 || getType() == 16) {
                	System.out.println(getType());
                    mlew.writeShort(unknown);
                }
                mlew.writePos(offset);
                break;
            case 55:
            case 65:
            case 82:
            	mlew.writePos(getPosition());
            	mlew.writePos(pixelsPerSecond);
            	mlew.writeShort(foothold);
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
                mlew.writePos(pixelsPerSecond);
                if (getType() == 20 || getType() == 21) {
                    mlew.writeShort(unknown);
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
                mlew.writePos(getPosition());
                mlew.writeShort(foothold);
                break;
            case 14:
            	mlew.writePos(pixelsPerSecond);
            	mlew.writeShort(unknown);
                break;
            case 22:
                mlew.writePos(getPosition());
                mlew.writePos(pixelsPerSecond);
                break;
            case 12:
            	mlew.write(wui);
            	break;
        }
        if (getType() != 12) {
            mlew.write(getMoveAction());
            mlew.writeShort(getDuration());
            mlew.write(getUnknown2());
        } else {
            //lew.write(wui);
        }
    }
}
