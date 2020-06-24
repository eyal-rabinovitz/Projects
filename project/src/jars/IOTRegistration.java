package jars;

import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

import gatewayserver.CMDFactory;
import gatewayserver.DatabaseManagementInterface;
import gatewayserver.FactoryCommand;
import gatewayserver.FactoryCommandModifier;

public class IOTRegistration implements FactoryCommandModifier {
	private static final String IOT_USER_REGISTRATION = "IOT_USER_REGISTRATION";
	private static double version = 0;

	@Override
	public void addToFactory() {
		CMDFactory<FactoryCommand, String, Object> cmdFactory = CMDFactory.getFactoryInstance();
		cmdFactory.add(IOT_USER_REGISTRATION, (Object obj) -> new IOTRegistrationProtocol());
	}	
	
	public class IOTRegistrationProtocol implements FactoryCommand {
		private static final String IOT_REGISTRATION_PROTOCOL_RESPONSE = "IOTRegistrationProtocol V";
		private static final String SQL_COMMAND = "sqlCommand";
		
		@Override
		public String run(Object data, DatabaseManagementInterface DbManagemaent) throws JSONException, SQLException {
			DbManagemaent.createRow(((JSONObject)data).getString(SQL_COMMAND));
					
			return IOT_REGISTRATION_PROTOCOL_RESPONSE;
		}
	}
	
	public static double getVersion() {
		return version;
	}
}