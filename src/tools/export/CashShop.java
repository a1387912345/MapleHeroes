/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import net.netty.MaplePacketReader;
import tools.HexTool;
import tools.data.ByteArrayByteStream;

/**
 *
 * @author Itzik
 */
public class CashShop {

    public static void main(String[] args) {
        Properties data = new Properties();
        InputStreamReader is;
        try {
            is = new FileReader("CashShop.txt");
            data.load(is);
            is.close();
        } catch (IOException ex) {
            System.out.println("Failed to load CashShop.txt");
        }
        dumpPackages(data);
        dumpCategories(data);
        dumpMenuItems(data);
        System.out.println("Action Complete,\r\nData Location: /CashShop");
    }

    public static void dumpPackages(Properties data) {
        byte[] hexdata = HexTool.getByteArrayFromHexString(data.getProperty("packages"));
        final MaplePacketReader inPacket = new MaplePacketReader(new ByteArrayByteStream((byte[]) hexdata));
        StringBuilder sb = new StringBuilder();
        sb.append("int[][][] packages = {\r\n");
        int length = inPacket.readInt();
        for (int i = 0; i < length; i++) {
            sb.append("{{");
            sb.append(inPacket.readInt());
            sb.append("}, {");
            int snlength = inPacket.readInt();
            for (int l = 0; l < snlength; l++) {
                sb.append(inPacket.readInt());
                if (snlength - l != 1) {
                    sb.append(", ");
                }
            }
            sb.append("}}");
            if (length - i != 1) {
                sb.append(",");
            }
            sb.append("\r\n");
        }
        sb.append("};");
        System.out.println("Packages:\r\n" + sb.toString());
        try {
            File outfile = new File("CashShop");
            outfile.mkdir();
            FileOutputStream out = new FileOutputStream(outfile + "/packages.txt", false);
            out.write(sb.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Failed to save data into a file");
        }
    }

    public static void dumpCategories(Properties data) {
        byte[] hexdata = HexTool.getByteArrayFromHexString(data.getProperty("categories"));
        final MaplePacketReader inPacket = new MaplePacketReader(new ByteArrayByteStream((byte[]) hexdata));
        StringBuilder sb = new StringBuilder();
        sb.append("/* Dumping data for table `cashshop_categories` */");
        int category, parent, flag, sold;
        String name;
        try {
            inPacket.skip(inPacket.readByte() == 3 ? 1 : 3);
            int length = inPacket.readByte();
            for (int i = 0; i < length; i++) {
                category = inPacket.readInt();
                name = inPacket.readMapleAsciiString();
                parent = inPacket.readInt();
                flag = inPacket.readInt();
                sold = inPacket.readInt();
                sb.append("\r\nINSERT INTO cashshop_categories (`categoryid`, `name`, `parent`, `flag`, `sold`) ");
                sb.append("VALUES (").append(category).append(", '").append(name).append("', ").append(parent);
                sb.append(", ").append(flag).append(", ").append(sold).append(");");
            }
        } catch (Exception ex) {
            System.out.println("Failed to read categories.\r\n" + ex);
            return;
        }
        try {
            File outfile = new File("CashShop");
            outfile.mkdir();
            FileOutputStream out = new FileOutputStream(outfile + "/categories.sql", false);
            out.write(sb.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Failed to save data into a file");
        }
    }

    public static void dumpMenuItems(Properties data) {
        byte[] hexdata;
        MaplePacketReader inPacket;
        StringBuilder sb = new StringBuilder();
        sb.append("/* Dumping data for table `cashshop_menuitems` */");
        int category, subcategory, parent, sn, itemid, flag = 0, price, discountPrice, quantity, expire, gender, likes;
        String image;
        try {
            for (int menu = 1; menu <= 4; menu++) {
                String menuStr = "menuitems" + menu;
                hexdata = HexTool.getByteArrayFromHexString(data.getProperty(menuStr));
                inPacket = new MaplePacketReader(new ByteArrayByteStream((byte[]) hexdata));
                byte a = inPacket.readByte();
                inPacket.skip(a == 4 || a == 5 || a == 6 || a == 8 ? 1 : 3);
                int length = inPacket.readByte();
                for (int i = 0; i < length; i++) {
                    category = inPacket.readInt();
                    subcategory = inPacket.readInt();
                    parent = inPacket.readInt();
                    image = inPacket.readMapleAsciiString();
                    sn = inPacket.readInt();
                    itemid = inPacket.readInt();
                    inPacket.skip(4 * 4);
                    price = inPacket.readInt();
                    inPacket.skip(8 * 4);
                    discountPrice = inPacket.readInt();
                    inPacket.skip(4);
                    quantity = inPacket.readInt();
                    expire = inPacket.readInt();
                    inPacket.skip(1 * 5);
                    gender = inPacket.readInt();
                    likes = inPacket.readInt();
                    inPacket.skip(4 * 4);
                    for (int p = 0; p < inPacket.readInt(); p++) {
                        inPacket.skip(4 * 9);
                    }
                    sb.append("INSERT INTO cashshop_menuitems (`category`, `subcategory`, `parent`, `image`, ");
                    sb.append("`sn`, `itemid`, `flag`, `price`, `discountPrice`, `quantity`, `expire`, `gender`, `likes`) ");
                    sb.append("VALUES ('").append(category).append("', '").append(subcategory).append("', '");
                    sb.append(parent).append("', '").append(image).append("', '").append(sn).append("', '");
                    sb.append(itemid).append("', '").append(flag).append("', '").append(price).append("', '");
                    sb.append(discountPrice).append("', '").append(quantity).append("', '");
                    sb.append(expire).append("', '").append(gender).append("', '").append(likes).append("');\r\n");
//                    sb.append("\r\nINSERT INTO cashshop_menuitems (`category`, `subcategory`, `parent`, `image`, `sn`, `itemid`, `flag`, `price`, `discountPrice`, `quantity`, `expire`, `gender`, `likes`) ");
//                    sb.append("VALUES (").append(category).append(", ").append(subcategory).append(", ");
//                    sb.append(parent).append(", '").append(image).append("', ").append(sn).append(", ");
//                    sb.append(itemid).append(", ").append(flag).append(", ").append(price).append(", ");
//                    sb.append(discountPrice).append(", ").append(quantity).append(", ").append(expire);
//                    sb.append(", ").append(gender).append(", ").append(likes).append(");");
                }
            }
        } catch (Exception ex) {
            System.out.println("Failed to read items.\r\n" + ex);
            return;
        }
        try {
            File outfile = new File("CashShop");
            outfile.mkdir();
            FileOutputStream out = new FileOutputStream(outfile + "/menuitems.sql", false);
            out.write(sb.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Failed to save data into a file");
        }
    }
}
