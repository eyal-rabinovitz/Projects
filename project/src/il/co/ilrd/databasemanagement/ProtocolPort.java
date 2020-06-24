package il.co.ilrd.databasemanagement;

public enum ProtocolPort {
	PINGPONG_PROTOCOL_PORT(50000),
	CHAT_PROTOCOL_PORT(55555),
	DATABASE_MANAGEMENT_PORT(60000);
	
	private final int PORT;
	
	ProtocolPort(int port) {
		this.PORT = port;
	}
	
	public int getPort() {
		return PORT;
	}
}