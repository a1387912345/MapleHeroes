package net.login.handler;

import client.MapleCharacter;
import client.MapleClient;
import net.MaplePacketHandler;
import net.RecvPacketOpcode;
import net.netty.MaplePacketReader;
import net.packet.LoginPacket;

public class ChangePicHandler extends MaplePacketHandler {
	public ChangePicHandler(RecvPacketOpcode recv) {
		super(recv);
	}

	@Override
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}
	
	@Override
	public void handlePacket(final MaplePacketReader inPacket, final MapleClient c, MapleCharacter chr) {
		final String oldPic = inPacket.readMapleAsciiString();
    	final String newPic = inPacket.readMapleAsciiString();
    	int response = 6; // Couldn't process the request - Will never end as 6, but precautionary.
    	
    	if(c.getSecondPassword().equals(oldPic)) {
    		if(!oldPic.equals(newPic) && newPic.length() >= 6 && newPic.length() <= 16) {
    			c.setSecondPassword(newPic);
    			c.updateSecondPassword();
    			response = 0; // Response 0: Your PIC was successfully activated.
	    	} else {
	    		response = 95; // Response 95: Please choose a different PIC. The PIC you entered has been used too recently.
	    	}
    	} else {
    		response = 20; // Response 14: You have entered an incorrect PIC. Please try again.
    	}
    	
    	c.sendPacket(LoginPacket.sendPicResponse(response));
    }

}
