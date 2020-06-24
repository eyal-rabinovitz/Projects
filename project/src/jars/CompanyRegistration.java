package jars;

import java.sql.SQLException;
import org.json.JSONException;
import org.json.JSONObject;

import gatewayserver.CMDFactory;
import gatewayserver.DatabaseManagementInterface;
import gatewayserver.FactoryCommand;
import gatewayserver.FactoryCommandModifier;

public class CompanyRegistration implements FactoryCommandModifier {
	private static final String COMPANY_REGISTRATION = "COMPANY_REGISTRATION";
	private static Double version = 90.0;
 
	@Override
	public void addToFactory() {
		CMDFactory.getFactoryInstance().add(COMPANY_REGISTRATION, (Object obj) -> new CompanyRegistrationProtocol());
	}	
	
	public class CompanyRegistrationProtocol implements FactoryCommand {
		private static final String COMPANY_REGISTRATION_RESPONSE = "CompanyRegistration 80";
		private static final String DATA_KEY_SQL = "sqlCommand";
		
		@Override
		public String run(Object data, DatabaseManagementInterface DbManagemaent) throws JSONException, SQLException {			
			DbManagemaent.createTable(((JSONObject)data).getString(DATA_KEY_SQL));
			
			return COMPANY_REGISTRATION_RESPONSE;
		}
	}

	public static Double getVersion() {
		return version;
	}
	
	public static String getName() {
		return COMPANY_REGISTRATION;
	}
}
 