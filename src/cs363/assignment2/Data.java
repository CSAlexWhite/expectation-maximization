package cs363.assignment2;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Vector;

public class Data {
	
	public String filename;
	public static Vector<Vector<Double>> column;
	
	public Data(Path filePath, int columns){
		
		this.filename = filePath.toString();
		column = new Vector<Vector<Double>>(columns);
		for(int i=0; i<3; i++) column.add(i, new Vector<Double>(0));
		
		try { importData(filename); } 
		catch (FileNotFoundException e) {}
		
		printData();
	}
	
	public Data(String filename, int columns){
		
		this.filename = filename;
		column = new Vector<Vector<Double>>(columns);
		for(int i=0; i<3; i++) column.add(i, new Vector<Double>(0));
		
		try { importData(filename); } 
		catch (FileNotFoundException e) {}
		
		printData();
	}

	/**
	 * Finds the data, ignores the column headings, imports each column of values
	 * into a new vector
	 * @param filename
	 * @throws FileNotFoundException
	 */
	public void importData(String filename) throws FileNotFoundException{
		
		Scanner inFile = new Scanner(new FileReader(filename));
		
		String nextToken;
		int next = 0, i=0, j=0;
		
		for(int k=0; k<3; k++) nextToken = inFile.next();
		while(inFile.hasNext()){
			
			nextToken = inFile.next();
			if(nextToken.equals("-")) column.get(i%3).add(-1.0);
			else column.get(i%3).add(Double.parseDouble(nextToken));
			i++; j++;
		}
	}
	
	/**
	 * Prints the data as in the input file, column-oriented
	 */
	public void printData(){
		
		for(int i=0; i<column.get(0).size(); i++){
			
			for(int j=0; j<3; j++)	System.out.print(column.get(j).get(i) + "\t");
			
			System.out.println();
		}
	}
}
