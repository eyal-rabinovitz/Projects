package filedatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.Objects;

import il.co.ilrd.observer.Callback;

public class ChangesAnalyzer {
	
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
		
		callback = new Callback<>(param -> {	compareFiles();	}, null);
		
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
