/*
 * This file was designed for Titanium.
 * Do not redistribute without explicit permission from the
 * developer(s).
 */
package server.shark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import constants.ServerConstants;
import net.netty.MaplePacketWriter;
import tools.HexTool;

public class SharkLogger {

    private List<SharkPacket> stored = new ArrayList<>();
    private static int sessionID = 1337;
    private final static int SEVENBITS = 0x0000007f;
    private final static int SIGNBIT = 0x00000080;

    public SharkLogger() {
    }

    /**
     * Writes a 7-bit integer. As if I actually know what that is. Credits to
     * Arnold Lamkamp
     *
     * @ CWI Eclipse
     * @param value
     * @param mpw
     */
    private void write7BitInt(int value, MaplePacketWriter mpw) {
        int intValue = value;

        if ((intValue & 0xffffff80) == 0) {
            mpw.write((byte) (intValue & SEVENBITS));
            return;
        }
        mpw.write((byte) ((intValue & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xffffc000) == 0) {
            mpw.write((byte) ((intValue >>> 7) & SEVENBITS));
            return;
        }
        mpw.write((byte) (((intValue >>> 7) & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xffe00000) == 0) {
            mpw.write((byte) ((intValue >>> 14) & SEVENBITS));
            return;
        }
        mpw.write((byte) (((intValue >>> 14) & SEVENBITS) | SIGNBIT));

        if ((intValue & 0xf0000000) == 0) {
            mpw.write((byte) ((intValue >>> 21) & SEVENBITS));
            return;
        }
        mpw.write((byte) (((intValue >>> 21) & SEVENBITS) | SIGNBIT));

        mpw.write((byte) ((intValue >>> 28) & SEVENBITS));
    }

    public void dump() {
        if (!ServerConstants.LOG_SHARK) {
            return;
        }
        MaplePacketWriter mpw = new MaplePacketWriter();
        String localend = "127.0.0.1";
        String remoteend = "8.31.99.140";
        mpw.writeShort(0x2015);
        write7BitInt(localend.length(), mpw);
        mpw.writeAsciiString(localend); // mLocalEndpoint
        mpw.writeShort(7575); // mLocalPort
        write7BitInt(remoteend.length(), mpw);
        mpw.writeAsciiString(remoteend); // mRemoteEndpoint
        mpw.writeShort(6969); // mRemotePort
        mpw.write(8); // mLocale
        mpw.writeShort(ServerConstants.CLIENT_VERSION); // mBuild
        try {
	        for (SharkPacket b : stored) {
	            b.dump(mpw);
	        }
        } catch (Exception e) {
        	
        }

        File toWrite = new File("./sharklogs/MShark" + sessionID++ + ".msb");
        try {
            toWrite.createNewFile();
            try (FileOutputStream fos = new FileOutputStream(toWrite)) {
                fos.write(mpw.getPacket(false));
                fos.flush();
            }
        } catch (IOException ex) {
            System.out.println(ex);
        }

        stored = null;
    }

    public void log(SharkPacket sp) {
        this.stored.add(sp);
    }
}
