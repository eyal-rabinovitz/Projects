package il.co.ilrd.filedatabase;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;

import il.co.ilrd.observer.Callback;
import il.co.ilrd.observer.Dispatcher;

public class FileBackup {
	
	private FileWatcher fileWatcher;
	private ChangesAnalyzer changesAnalyzer;
	private String backupFilePath;
	private String watchedFilePath;
	private boolean isRunning = false;

	public FileBackup(String watchedFilePath) throws IOException {
		checkIfPathValid(watchedFilePath);
		this.watchedFilePath = watchedFilePath;
	}

	public void stopFileBackup() throws IOException {
		if(isRunning) {
			fileWatcher.stopUpdate();
			isRunning = false;
		}
	}
	
	public void startFileBackup() throws IOException, InterruptedException {
		if(null == backupFilePath) {
			setBackupFile(getDefaultBackupFilePath()); 
		}
		
		if(!isRunning) {
			isRunning = true;
			fileWatcher = new FileWatcher(watchedFilePath);
			changesAnalyzer = new ChangesAnalyzer(watchedFilePath, backupFilePath);
			changesAnalyzer.register(fileWatcher);
		}
	}
	
	public void setBackupFile(String backupFilePath) throws IOException {
		if (isRunning) {
			throw new AccessDeniedException("cannot set backup file while running");
		}
		
		checkIfPathValid(backupFilePath);
		this.backupFilePath = backupFilePath;
		File backupFile = new File(backupFilePath);
		if(!backupFile.exists()) {
			backupFile.createNewFile();
		}
	}

	private String getDefaultBackupFilePath() {
		return Paths.get(watchedFilePath).getParent() + "\\backupfile.txt";
	}
	
	private void checkIfPathValid(String watchedFile) throws FileNotFoundException {
		Objects.requireNonNull(watchedFile, "path should be valid");
		File fileToCheck = new File(watchedFile);
		if (!fileToCheck.isFile()) {
			throw new FileNotFoundException();
		}
	}
	
	private class ChangesAnalyzer {
		
		private File watchedFile;
		private File backupFile;
		private Callback<WatchEvent<?>> callback;
		private CRUDFile crudFile;

		public ChangesAnalyzer(String watchedFile, String backupFile) throws IOException {
			Objects.requireNonNull(watchedFile, backupFile);
			this.watchedFile = new File(watchedFile);
			this.backupFile = new File(backupFile);
			
			checkIfFilesAreValid();
			
			crudFile = new CRUDFile(backupFile); 
		}
		
		public void register(FileWatcher fileWatcher) {
			Objects.requireNonNull(fileWatcher);		
			callback = new Callback<>(param -> { compareFiles(); }, null);
			fileWatcher.register(callback);
		}
		
		private void compareFiles() {
			try {
				BufferedReader watchedBuffer = new BufferedReader(new FileReader(watchedFile));
				BufferedReader backupBuffer = new BufferedReader(new FileReader(backupFile));
				String lineBackup = backupBuffer.readLine();
				String lineWatched = watchedBuffer.readLine();
				
				while(null != lineWatched) {
					if ((lineBackup == null) || (!lineBackup.equals(lineWatched))) {
						crudFile.create(lineWatched);
						break;
					}
					lineBackup = backupBuffer.readLine();
					lineWatched = watchedBuffer.readLine();
				}
				
				watchedBuffer.close();
				backupBuffer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		
		private void checkIfFilesAreValid() throws FileNotFoundException {
			if(!watchedFile.isFile() || !backupFile.isFile()) {
				throw new FileNotFoundException();
			}
		}
	}
	
	private class FileWatcher {
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
}
