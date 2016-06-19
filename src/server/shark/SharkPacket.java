/*
 * This file was designed for Titanium.
 * Do not redistribute without explicit permission from the
 * developer(s).
 */
package server.shark;

import java.util.Arrays;

import net.netty.MaplePacketWriter;

public class SharkPacket {

    private final byte[] info;
    private final long timestamp;
    private final boolean outbound;
    private short opcode;
    private boolean invalid = false;

    public SharkPacket(byte[] info, boolean out) {
        this.info = info;
        this.timestamp = System.currentTimeMillis();
        this.outbound = out;
        try {
            this.opcode = (short) (((info[1] & 0xFF) << 8) + (info[0] & 0xFF));
        } catch (ArrayIndexOutOfBoundsException aiobe) {
            opcode = -1;
            this.invalid = true;
        }
    }

    public void dump(MaplePacketWriter mpw) {
        if (invalid) {
            return;
        }

        short size = (short) (info.length - 2); // don't include opcode
        if (outbound) {
            size |= 0x8000;
        }

        mpw.writeLong(timestamp);
        mpw.writeShort(size);
        mpw.writeShort(opcode);
        mpw.write(Arrays.copyOfRange(info, 2, info.length));
    }
}
