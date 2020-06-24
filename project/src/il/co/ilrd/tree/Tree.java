package il.co.ilrd.tree;

import java.util.ArrayList;
import java.io.*;

public class Tree {
	
	FolderT folder;
	
	public Tree(String path) {
		folder = new FolderT(path);
	}
	
	public void print() {
		folder.print(0);
	}
	
	/****************************************************/
	
	private abstract class FileComponent {
		
		String name;
		
		public abstract void print(int indentation);
	}
	
	/****************************************************/

	private class FolderT extends FileComponent {

		ArrayList<FileComponent> list = new ArrayList<FileComponent>();
		
		public FolderT(String path) {
			File file = new File(path);
			File[] fileList = file.listFiles();
			name = file.getName();

			for(File iterator : fileList) {
				if(iterator.isFile()) {
					list.add(new FileT(iterator.getName()));
				}
				else {
					list.add(new FolderT(iterator.getPath()));
				}
			}
		}

		@Override
		public void print(int indentation) {
			System.out.print("____".repeat(indentation));
			System.out.println(" Folder: " + name);
			
			for(FileComponent iterator : list) {
				iterator.print(indentation + 1);
			}
		}
	}
	
	/****************************************************/

	private class FileT extends FileComponent {
			
		private FileT(String name) {
			this.name = name;
		}

		@Override
		public void print(int indentation) {
			System.out.print("____".repeat(indentation));
			System.out.println(" File: " + name);
		}
	}
	
	public static void main(String[] args) {
		Tree tree = new Tree("/home/student/folderforcomposite");
		
		tree.print();
	}
}

