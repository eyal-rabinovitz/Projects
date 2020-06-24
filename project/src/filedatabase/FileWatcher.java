package filedatabase;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import static java.nio.file.StandardWatchEventKinds.*;

import il.co.ilrd.observer.*;

public class FileWatcher {
	private Path path;
	private Path fileName;
	private WatchService watcher;
	private WatcherThread watcherThread; 
	private Dispatcher<WatchEvent<?>> dispatcher = new Dispatcher<>();
	private boolean toRun = true;

	public FileWatcher(String path) throws IOException, InterruptedException {
		Objects.requireNonNull(path);
		if(!new File(path).isFile()) {
			throw new IOException();
		}
		
		this.path = Paths.get(path).getParent();
		fileName = Paths.get(path).getFileName();
		watcher = FileSystems.getDefault().newWatchService();
		this.path.register(watcher, ENTRY_DELETE, ENTRY_MODIFY, ENTRY_CREATE);
		watcherThread = new WatcherThread();
		watcherThread.start();
	}

	public void register(Callback<WatchEvent<?>> callback) {
		Objects.requireNonNull(callback);
		dispatcher.register(callback);
	}
	
	public void unregister(Callback<WatchEvent<?>> callback) {
		Objects.requireNonNull(callback);
		dispatcher.unregister(callback);
	}
	
	private void updateAll() {
		dispatcher.updateAll(null);
	}

	public void stopUpdate() throws IOException {
		toRun = false;
		watcher.close();
	}
	
	private class WatcherThread extends Thread {
		private WatchKey key;
		
		@Override
		public void run() {	
			
			while (toRun) {
			    try {
					key = watcher.take();
				} catch (InterruptedException e) {
					System.out.println("InterruptedException");
				} catch (ClosedWatchServiceException e) {
					System.out.println("ClosedWatchServiceException");
				}
			    
			    Path changed;
			    for(WatchEvent<?> event : key.pollEvents()) {
			    	changed = (Path) event.context();

			    	if (changed.endsWith(fileName)) { 
			    		updateAll();
			    		break;
			    	}
			    } 
			    
		        if (!key.reset()) {
		        	System.out.println("Key has been unregistered");
		        	return;
		        }
			 }
		}
	}
}