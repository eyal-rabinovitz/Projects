package il.co.ilrd.jars;

import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

import il.co.ilrd.gatewayserver.CMDFactory;
import il.co.ilrd.gatewayserver.DatabaseManagementInterface;
import il.co.ilrd.gatewayserver.FactoryCommand;
import il.co.ilrd.gatewayserver.FactoryCommandModifier;

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