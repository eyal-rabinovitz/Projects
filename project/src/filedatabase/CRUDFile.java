package filedatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class CRUDFile implements CRUD<Integer, String> {
	private File file;

	CRUDFile(String fileName) {
		file = new File(fileName);
	}
	
	@Override
	public Integer create(String data) {
		Objects.requireNonNull(data);
		Integer numOfLines = 0;
		
		try(FileWriter fileWriter = new FileWriter(file, true)) {
			System.out.println("writing  " + data);
			fileWriter.append("\n" + data);
			numOfLines = (int) Files.lines(Paths.get(file.getPath())).count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return numOfLines;
	}

	@Override
	public String read(Integer key) {
		BufferedReader watchedFileReader = null;
		try {
			watchedFileReader = new BufferedReader(new FileReader(file));
			for(Integer i = 0; i < key; ++i) {
				watchedFileReader.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return watchedFileReader.toString();
	}

	@Override
	public void update(Integer key, String newData) {
	}

	@Override
	public void delete(Integer key) {
	}

}
