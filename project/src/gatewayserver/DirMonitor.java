package gatewayserver;

import java.io.IOException;
import java.nio.file.Path;

import il.co.ilrd.observer.Callback;

public interface DirMonitor {
	public void register(Callback<Path> callback);
	
	public void unregister(Callback<Path> callback);

	public void stopUpdate() throws IOException;
}