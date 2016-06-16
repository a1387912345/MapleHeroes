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
public class CashShopCategoryItem {

    public static void main(String[] args) {
        Properties data = new Properties();
        InputStreamReader is;
        try {
            is = new FileReader("CashShopItems.txt");
            data.load(is);
            is.close();
        } catch (IOException ex) {
            System.out.println("Failed to load CashShop.txt");
        }
        StringBuilder all = new StringBuilder();
        for (Object property : data.keySet()) {
            byte[] hexdata = HexTool.getByteArrayFromHexString(data.getProperty(String.valueOf(property)));
            final MaplePacketReader inPacket = new MaplePacketReader(new ByteArrayByteStream((byte[]) hexdata));
            StringBuilder sb = new StringBuilder();
            sb.append("/* Dumping data for table `cashshop_items` */\r\n");
            try {
                if (inPacket.readByte() != 0xB) {
                    continue;
                }
//                if (inPacket.readByte() != 1) {
//                    continue;
//                }
                inPacket.readByte();
                int length = inPacket.readByte();
                for (int i = 0; i < length; i++) {
                    int category = inPacket.readInt(); //1000000
                    int subcategory = inPacket.readInt();
                    int parent = inPacket.readInt();
                    String image = inPacket.readMapleAsciiString();
                    int sn = inPacket.readInt();
                    if (all.toString().contains(String.valueOf(sn))) {
                        continue;
                    }
                    int itemId = inPacket.readInt();
                    inPacket.readInt();
                    int flag = inPacket.readInt();
                    int pack = inPacket.readInt();
                    int starterpack = inPacket.readInt();
                    int price = inPacket.readInt();
                    inPacket.readLong();
                    inPacket.readLong();
                    inPacket.readLong();
                    inPacket.readLong();
                    int discountPrice = inPacket.readInt();
                    inPacket.readInt();
                    int quantity = inPacket.readInt();
                    int expire = inPacket.readInt();
                    inPacket.skip(5);
                    int gender = inPacket.readInt();
                    int likes = inPacket.readInt();
                    inPacket.readInt();
                    inPacket.readMapleAsciiString();
                    inPacket.readShort();
                    inPacket.readInt();
                    inPacket.readInt();
                    if (pack == 0) {
                        inPacket.readInt();
                    } else {
                        int packsize = inPacket.readInt();
                        for (int ii = 0; ii < packsize; ii++) {
                            inPacket.readInt(); //should be pack item sn
                            inPacket.readInt();
                            inPacket.readInt();//1
                            inPacket.readInt(); //pack item usual price
                            inPacket.readInt(); //pack item discounted price
                            inPacket.readInt();
                            inPacket.readInt();
                            inPacket.readInt();
                            inPacket.readInt();
                        }
                    }
//                    int flag = 0;
                    sb.append("INSERT INTO cashshop_items (`category`, `subcategory`, `parent`, `image`, ");
                    sb.append("`sn`, `itemId`, `flag`, `price`, `discountPrice`, `quantity`, `expire`, `gender`, `likes`) ");
                    sb.append("VALUES ('").append(category).append("', '").append(subcategory).append("', '");
                    sb.append(parent).append("', '").append(image).append("', '").append(sn).append("', '");
                    sb.append(itemId).append("', '").append(flag).append("', '").append(price).append("', '");
                    sb.append(discountPrice).append("', '").append(quantity).append("', '");
                    sb.append(expire).append("', '").append(gender).append("', '").append(likes).append("');\r\n");
                }
            } catch (Exception ex) {
                System.out.println("Failed to read items. property " + property/* + "\r\n" + ex*/);
                continue;
            }
            try {
                File outfile = new File("CashShop/items");
                outfile.mkdirs();
                FileOutputStream out = new FileOutputStream(outfile + "/category_" + property + ".sql", false);
                out.write(sb.toString().getBytes());
                sb.append("\r\n\r\n");
                all.append(sb);
            } catch (IOException ex) {
                System.out.println("Failed to save data into a file");
            }
        }
        try {
            File outfile = new File("CashShop/items");
            outfile.mkdirs();
            FileOutputStream out = new FileOutputStream(outfile + "/category_all.sql", false);
            out.write(all.toString().getBytes());
        } catch (IOException ex) {
            System.out.println("Failed to save data into a file");
        }
        System.out.println("done");
    }
}
