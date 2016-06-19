package net.server.cashshop;

import database.DatabaseConnection;
import net.netty.MaplePacketWriter;
import net.packet.PacketHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import tools.HexTool;

public class CSMenuItem {

    private static final List<CSMenuItem> pictureItems = new LinkedList<>();

    public static void loadFromDb() {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (ResultSet rs = con.prepareStatement("SELECT * FROM cs_picture").executeQuery()) {
                while (rs.next()) {
                    pictureItems.add(new CSMenuItem(
                            rs.getInt("category"),
                            rs.getInt("subcategory"),
                            rs.getInt("parent"),
                            rs.getString("image"),
                            rs.getInt("sn"),
                            rs.getInt("itemid"),
                            rs.getByte("flag"),
                            rs.getInt("originalPrice"),
                            rs.getInt("salePrice"),
                            rs.getInt("quantity"),
                            rs.getInt("duration"),
                            rs.getInt("likes")));
                }
            }
        } catch (SQLException ex) {        }

    }
    private int c, sc, p, i, sn, id, op, sp, qty, dur, likes;
    private final String img;
    private final byte flag;

    private CSMenuItem(int c, int sc, int p, String img, int sn, int id, byte flag, int op, int sp, int qty, int dur, int likes) {
        this.c = c;
        this.sc = sc;
        this.p = p;
        this.img = img;
        this.sn = sn;
        this.id = id;
        this.flag = flag;
        this.op = op;
        this.sp = sp;
        this.qty = qty;
        this.dur = dur;
        this.likes = likes;
    }

    public static void writeData(CSMenuItem csmi, MaplePacketWriter mpw) {
        mpw.writeInt(csmi.c);
        mpw.writeInt(csmi.sc);
        mpw.writeInt(csmi.p);
        mpw.writeMapleAsciiString(csmi.img); // TODO add check if cat != 4 write empty string
        mpw.writeInt(csmi.sn);
        mpw.writeInt(csmi.id);
        mpw.writeInt(1);
        mpw.writeInt(csmi.flag);
        mpw.writeInt(0);
        mpw.writeInt(0); // this one changes
        mpw.writeInt(csmi.op);
        mpw.write(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        mpw.writeLong(PacketHelper.MAX_TIME);
        mpw.write(HexTool.getByteArrayFromHexString("00 80 22 D6 94 EF C4 01")); // 1/1/2005
        mpw.writeLong(PacketHelper.MAX_TIME);
        mpw.writeInt(csmi.sp);
        mpw.writeInt(0);
        mpw.writeInt(csmi.qty);
        mpw.writeInt(csmi.dur);
        mpw.write(HexTool.getByteArrayFromHexString("01 00 01 00 01 00 00 00 01 00 02 00 00 00")); // flags maybe
        mpw.writeInt(csmi.likes);
        mpw.writeZeroBytes(20);
    }
}
