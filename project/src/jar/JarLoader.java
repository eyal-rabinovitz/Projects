package jar;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;


import java.util.jar.JarFile;

public class JarLoader {
	private static final String DOT = ".";
	private static final String SLASH = "/";
	private final static String CLASS_EXTENSTION = ".class";
	private static final String FILE_PREFIX = "file:\\";
	private static List<Class<?>> classListToReturn;

	public static List<Class<?>> load(String interfaceName, String jarPath) throws ClassNotFoundException, IOException {
		classListToReturn = new ArrayList<>();
		try (JarFile jarFile = new JarFile(jarPath)) {
			URLClassLoader classLoader = new URLClassLoader(new URL[] { new URL(FILE_PREFIX + jarPath)});
			Enumeration<JarEntry> entries = jarFile.entries();
			
			while(entries.hasMoreElements()){
				 JarEntry entry = entries.nextElement();
				 
	             if(isClassFile(entry)){
	                 Class<?> currentClass = Class.forName(getClassName(entry), false, classLoader);
	                 
	                 if(checkIfClassImplementsInterfac(interfaceName, currentClass)) {
	                	 classListToReturn.add(currentClass);
	                 }
	             }
			}
				
			return classListToReturn;
		}
	}

	private static String getClassName(JarEntry entry) {
		String className;
		className = entry.getName();
		 className = removeExtensionClass(className).replaceAll(SLASH, DOT);
		return className;
	}

	private static boolean isClassFile(JarEntry entry) {
		return !entry.isDirectory() && checkIfExtensionIsClass(entry);
	}
	 
	private static boolean checkIfExtensionIsClass(JarEntry entry) {
        if(entry.getName().endsWith(CLASS_EXTENSTION)){
       	 	return true;
        }
        
        return false;
	}
	
	private static String removeExtensionClass(String str) {
		str = str.substring(0, str.lastIndexOf(DOT));
		
		return str;
	}
	
	private static boolean checkIfClassImplementsInterfac(String interfaceName, Class<?> currentClass) {
		 String currentInterface;
		 
         for(Class<?> element : currentClass.getInterfaces()) {
        	 currentInterface = element.getName();
        	 if(interfaceName.equals(currentInterface.substring(currentInterface.lastIndexOf(DOT) + 1))){
        		 return true;
        	 }
         }
         
         return false;
	}
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String filePath = "C:\\jars\\filetocheck.jar";
		List<Class<?>> list = JarLoader.load("Runnable", filePath);
		list.forEach(System.out::println);
	}
}
