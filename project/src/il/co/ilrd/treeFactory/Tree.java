//package il.co.ilrd.treeFactory;
//
//import java.util.ArrayList;
//import java.util.function.Function;
//
//import java.io.*;
//
//public class Tree {
//	
//	FolderT folder;
//	Factory<FileComponent, Boolean, String> factory = new Factory<>();
//	
//	{
//		Function<String, FileComponent> createFile = (a) -> new FileT(a);
//		Function<String, FileComponent> createFolder = (a) -> new FolderT(a);
//		
//		factory.add(true, createFile);
//		factory.add(false, createFolder);
//	}
//	
//	public Tree(String path) {
//		folder = new FolderT(path);
//	}
//	
//	public void print() {
//		folder.print(0);
//	}
//	
//	/****************************************************/
//	
//	private abstract class FileComponent {
//		
//		String name;
//		
//		public abstract void print(int indentation);
//	}
//	
//	/****************************************************/
//
//	private class FolderT extends FileComponent {
//
//		ArrayList<FileComponent> list = new ArrayList<FileComponent>();
//		
//		public FolderT(String path) {
//			File file = new File(path);
//			File[] fileList = file.listFiles();
//			name = file.getName();
//
//			for(File iterator : fileList) {
//				list.add(factory.create(iterator.isFile(), iterator.getPath()));
//			}
//		}
//
//		@Override
//		public void print(int indentation) {
//			System.out.print("____".repeat(indentation));
//			System.out.println(" Folder: " + name);
//			
//			for(FileComponent iterator : list) {
//				iterator.print(indentation + 1);
//			}
//		}
//	}
//	
//	/****************************************************/
//
//	private class FileT extends FileComponent {
//			
//		private FileT(String path) {
//			name = new File(path).getName();
//		}
//
//		@Override
//		public void print(int indentation) {
//			System.out.print("____".repeat(indentation));
//			System.out.println(" File: " + name);
//		}
//	}
//	
//	public static void main(String[] args) {
//		Tree tree = new Tree("/home/student/folderforcomposite");
//		
//		tree.print();
//	}
//}
//
