package assignment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.FileOutputStream;

public class contactsAssignment {
	public enum propertyKeyEnum {
		FIRST_NAME_KEY("86B7", "First name : "),
		LAST_NAME_KEY("9E60", "Last name : "),
		PHONE_KEY("5159", "Phone number : "),
		TIME_STAMP_KEY("D812", "Date : "),
		PICTURE_KEY("6704", "Picture : ");

		public final String key;
		public final String label;
		private static final Map<String, propertyKeyEnum> lookupPropertyKey = new HashMap<>();

	    static {
	        for (propertyKeyEnum key : propertyKeyEnum.values()) {
	        	lookupPropertyKey.put(key.key, key);
	        }
	    }
	    
		propertyKeyEnum(String key, String label) {
			this.key = key;
			this.label = label;
		}
	}
	
	private static final String SUFFIX_TEXT = ".txt";
	private static final String SUFFIX_JPG = ".jpg";
	private static final int BEGIN_PROPERTY_KEY = 0;
	private static final int END_PROPERTY_KEY = 4;
	private static final int BEGIN_CONTACT_KEY = 0;
	private static final int END_CONTACT_KEY = 4;
	private static final int BEGIN_DATA_SIZE = END_CONTACT_KEY;
	private static final int END_DATA_SIZE = 9;
	private static final int BEGIN_DATA = END_DATA_SIZE;
	private static Map<String, Map<propertyKeyEnum, String>> mainContactsMap = new HashMap<>();
	
	public static void main(String args[]) {
		System.out.println("Hello user, plese enter path to file");
		
	    String filePath = getFilePathFromUser();

		parseFile(filePath);
		exportDataToDirectory(filePath);
    }

	private static String getFilePathFromUser() {
		Scanner scanPath = new Scanner(System.in);  
	    String filePath = scanPath.nextLine();  
	    scanPath.close();
	    
		return filePath;
	}
 
    private static void parseFile(String filePath) {
		try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
			String line = reader.readLine();
			while(null != line) {
				parseLine(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private static void parseLine(String line) {
    	propertyKeyEnum propertyKey = getPropertyKeyFromLine(line);
		line = line.substring(END_PROPERTY_KEY);
		
   	  	while(0 < line.length()) {
	   		String contactKey = line.substring(BEGIN_CONTACT_KEY, END_CONTACT_KEY);
	   		String sizeInHex = line.substring(BEGIN_DATA_SIZE, END_DATA_SIZE);
	   		int sizeInDecimal = getDecimal(sizeInHex);
	   	    String newData = line.substring(BEGIN_DATA, BEGIN_DATA + sizeInDecimal);

	   	    line = line.substring(line.indexOf(newData) + sizeInDecimal);
	   	    
		    if(propertyKey.equals(propertyKeyEnum.TIME_STAMP_KEY)) {
		    	newData = convertTimestampToDate(newData);
		    }
	   	    
	   	    insertDataToMap(contactKey, propertyKey, newData);        
   	  	}
    }

	private static propertyKeyEnum getPropertyKeyFromLine(String line) {
		String propertyKeyAsString = line.substring(BEGIN_PROPERTY_KEY, END_PROPERTY_KEY);
		
		return propertyKeyEnum.lookupPropertyKey.get(propertyKeyAsString);
	}
    
    public static void insertDataToMap(String contactkey, propertyKeyEnum propertyKey, String newData) {
    	if(mainContactsMap.containsKey(contactkey)) {
    		mainContactsMap.get(contactkey).put(propertyKey, newData);        		
    	}
    	else {
    		HashMap<propertyKeyEnum, String> newContact = new HashMap<>();
    		newContact.put(propertyKey, newData);
    		mainContactsMap.put(contactkey, newContact);
    	}
    }
    
	private static void exportDataToDirectory(String filePath) {
		String outputDirectoryPath = filePath.substring(0, filePath.lastIndexOf('/')) + "/ContactsDirectory";
		File outputDirectory = new File(outputDirectoryPath);
        File contactDir = null;
        File contactFile = null;
        
        if(!outputDirectory.exists()) {
        	outputDirectory.mkdir();        	
        }
       
        for(String key : mainContactsMap.keySet()) {
        	Map<propertyKeyEnum, String> innerContactMap = mainContactsMap.get(key); 
        	
    		contactDir = createContactDirectory(outputDirectory, innerContactMap.containsKey(propertyKeyEnum.FIRST_NAME_KEY) ? 
    																innerContactMap.get(propertyKeyEnum.FIRST_NAME_KEY) : 
    																innerContactMap.get(propertyKeyEnum.LAST_NAME_KEY));
    		contactFile = createFile(contactDir, innerContactMap.containsKey(propertyKeyEnum.FIRST_NAME_KEY) ? 
    													innerContactMap.get(propertyKeyEnum.FIRST_NAME_KEY) : 
    													innerContactMap.get(propertyKeyEnum.LAST_NAME_KEY));  
    		
        	exportContactToFile(contactFile, innerContactMap);
        }
	}

	private static void exportContactToFile(File contactFile, Map<propertyKeyEnum, String> innerContactMap) {
		try(BufferedWriter bf = new BufferedWriter(new FileWriter(contactFile))) {
			for(propertyKeyEnum key : innerContactMap.keySet()) {
			    if(key.equals(propertyKeyEnum.PICTURE_KEY)) {
			    	createOutputAsPicture(contactFile, innerContactMap); 
			    	break;
			    }
			    writeToFile(bf, key.label, innerContactMap, key);
			}
			bf.flush();
			
		}catch(IOException e){
		    e.printStackTrace();
		}
	}

	private static String convertTimestampToDate(String timestamp) {
		int dateAsInt = Integer.parseInt(timestamp); 
		String dateAsText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateAsInt * 1000L));
		
		return dateAsText;
	}

	private static void writeToFile(BufferedWriter bf,
									String propertyLabel,
									Map<propertyKeyEnum, String> innerContactMap,
									propertyKeyEnum propertyKey) throws IOException {
		bf.write(propertyLabel + innerContactMap.get(propertyKey));
    	bf.newLine();
	}

	private static void createOutputAsPicture(File contactFile, Map<propertyKeyEnum, String> innerContactMap) throws IOException {
		try(FileOutputStream imageOutFile = 
				new FileOutputStream(contactFile.toString().replace(SUFFIX_TEXT, SUFFIX_JPG))) {
			byte[] imageByteArray = Base64.getDecoder().decode(innerContactMap.get(propertyKeyEnum.PICTURE_KEY));
			imageOutFile.write(imageByteArray);
		}
	}
	
    private static File createContactDirectory(File outputDirectory, String name) {
    	File contactDir = new File(outputDirectory, name);
    	if(!contactDir.exists()) {
    		contactDir.mkdir();        	
        }
    	return contactDir;  
	}
    
    private static File createFile(File contactDir, String name) {
    	return new File(contactDir, name + SUFFIX_TEXT);  
	}

	public static int getDecimal(String hex) {
        String digits = "0123456789ABCDEF";  
        hex = hex.toUpperCase();
        int val = 0;  
        
        for(int i = 0; i < hex.length(); ++i) {  
        	char c = hex.charAt(i);  
            int d = digits.indexOf(c);  
            val = 16 * val + d;  
        }  
        
        return val;  
    }
}