package il.co.ilrd.jars;

import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

import il.co.ilrd.gatewayserver.CMDFactory;
import il.co.ilrd.gatewayserver.DatabaseManagementInterface;
import il.co.ilrd.gatewayserver.FactoryCommand;
import il.co.ilrd.gatewayserver.FactoryCommandModifier;

public class ProductRegistration implements FactoryCommandModifier {
	private static final String PRODUCT_REGISTRATION = "PRODUCT_REGISTRATION";
	private static double version = 0;

	@Override
	public void addToFactory() {
		CMDFactory<FactoryCommand, String, Object> cmdFactory = CMDFactory.getFactoryInstance();
		cmdFactory.add(PRODUCT_REGISTRATION, (Object obj) -> new ProductRegistrationProtocol());
	}	
	
	public class ProductRegistrationProtocol implements FactoryCommand {
		private static final String PRODUCT_REGISTRATION_PROTOCOL_RESPONSE = "ProductRegistrationProtocol V";
		private static final String SQL_COMMAND = "sqlCommand";
		
		@Override
		public String run(Object data, DatabaseManagementInterface DbManagemaent) throws JSONException, SQLException {
			DbManagemaent.createRow(((JSONObject)data).getString(SQL_COMMAND));
		
			return PRODUCT_REGISTRATION_PROTOCOL_RESPONSE;
		}
	}
	
	public static double getVersion() {
		return version;
	}
}

