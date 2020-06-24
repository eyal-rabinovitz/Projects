package jars;

import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

import gatewayserver.CMDFactory;
import gatewayserver.DatabaseManagementInterface;
import gatewayserver.FactoryCommand;
import gatewayserver.FactoryCommandModifier;

public class IOTUpadate implements FactoryCommandModifier {
	private static final String IOT_UPDATE = "IOT_UPDATE";
	private static double version = 0;

	@Override
	public void addToFactory() {
		CMDFactory<FactoryCommand, String, Object> cmdFactory = CMDFactory.getFactoryInstance();
		cmdFactory.add(IOT_UPDATE, (Object obj) -> new IOTUpadateProtocol());
	}	
	
	public class IOTUpadateProtocol implements FactoryCommand {
		private static final String IOT_UPADATE_PROTOCOL_RESPONSE = "IOTUpadateProtocol V";
		private static final String RAW_DATA = "rawData";
		
		@Override
		public String run(Object data, DatabaseManagementInterface DbManagemaent) throws JSONException, SQLException {
			DbManagemaent.createIOTEvent(((JSONObject)data).getString(RAW_DATA));
			
			return IOT_UPADATE_PROTOCOL_RESPONSE;
		}
	}
	
	public static double getVersion() {
		return version;
	}
}