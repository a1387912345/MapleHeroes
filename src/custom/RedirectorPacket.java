/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package custom;

import net.SendPacketOpcode;
import net.netty.MaplePacketWriter;

/**
 *
 * @author Itzik
 */
public class RedirectorPacket {

    public static byte[] redirectorCommand(String command) {
        MaplePacketWriter mpw = new MaplePacketWriter(SendPacketOpcode.REDIRECTOR_COMMAND);
		mpw.writeMapleAsciiString(command);

        return mpw.getPacket();
    }
}
