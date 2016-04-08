package handling.login.handler;

import client.MapleClient;
//import handling.AbstractMaplePacketHandler;
import tools.data.LittleEndianAccessor;
//import tools.data.input.SeekableLittleEndianAccessor;
import tools.packet.LoginPacket;

public class ChangePicHandler {
	
	public boolean validateState(MapleClient c) {
		return !c.isLoggedIn();
	}
	
	public static void handlePacket(final LittleEndianAccessor slea, final MapleClient c) {
		final String oldPic = slea.readMapleAsciiString();
    	final String newPic = slea.readMapleAsciiString();
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
    	
    	c.getSession().write(LoginPacket.sendPicResponse(response));
    }

}
