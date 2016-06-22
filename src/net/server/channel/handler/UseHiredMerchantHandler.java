package net.server.channel.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import client.MapleClient;
import client.character.MapleCharacter;
import database.DatabaseConnection;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.PlayerShopPacket;
import net.world.World;

public class UseHiredMerchantHandler extends MaplePacketHandler {

	public UseHiredMerchantHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public void handlePacket(MaplePacketReader inPacket, MapleClient client, MapleCharacter chr) {
		final boolean packet = true;
		
		if (client.getCharacter().getMap() != null && client.getCharacter().getMap().allowPersonalShop()) {
            final byte state = checkExistance(client.getCharacter().getAccountID(), client.getCharacter().getID());

            switch (state) {
                case 1:
                    client.getCharacter().dropMessage(1, "Please claim your items from Fredrick first.");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(client.getCharacter().getAccountID(), client.getCharacter().getID());
                    if (!merch) {
                        if (client.getChannelServer().isShutdown()) {
                            client.getCharacter().dropMessage(1, "The server is about to shut down.");
                            return;
                        }
                        if (packet) {
                            client.sendPacket(PlayerShopPacket.sendTitleBox());
                        }
                        return;
                    } else {
                        client.getCharacter().dropMessage(1, "Please close the existing store and try again.");
                    }
                    break;
                default:
                    client.getCharacter().dropMessage(1, "An unknown error occured.");
                    break;
            }
        } else {
            client.close();
        }
	}

	private static byte checkExistance(final int accid, final int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ps.close();
                        rs.close();
                        return 1;
                    }
                }
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }
}
