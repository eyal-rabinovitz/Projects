package filedatabase;

import java.io.IOException;
import java.nio.file.WatchEvent;

import il.co.ilrd.observer.Callback;

public class Test {
	public static void main(String[] args) throws InterruptedException, IOException {
		System.out.println("Test of filedatabase");
		String backupfilePath = "C:/Users/Eyal/eyal-rabinovitz/filewatcher/testfile/backupfile.txt";
		String filePath = "C:/Users/Eyal/eyal-rabinovitz/filewatcher/testfile/fileforwathcer.txt";
/*
		FileWatcher fileWatcher = new FileWatcher(filePath);
		Callback<WatchEvent<?>> callback = new Callback<>((a) -> System.out.println("Callback update"), 
				(b) -> System.out.println("stop update"));
		fileWatcher.register(callback);
		ChangesAnalyzer observer = new ChangesAnalyzer(filePath, backupfilePath);
		observer.register(fileWatcher);
		
		Thread.sleep(20000);
		
		fileWatcher.stopUpdate();*/
		
		FileBackup fileBackup = new FileBackup(filePath);
		fileBackup.setBackupFile(backupfilePath);
		fileBackup.startFileBackup();
		
		Thread.sleep(20000);

		
	}
}
