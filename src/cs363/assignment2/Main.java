package cs363.assignment2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	
	static String filename;
	static int filenumber = 0;
	
	public static void main(String[] args){
			
		Data data1 = new Data("data/hw2dataset_10.txt", 3);	
		data1.printData();
		double[] probabilities = {0.4, 0.3, 0.9};
		
		BinaryBayesArc GenderHeight = new BinaryBayesArc(data1, 0,1, probabilities);
		
// 	FUN THING TO TRY LATER
//		Data[] data = new Data[5];
//				
//		try {
//			
//			Files.walk(Paths.get("data/")).forEach(filePath -> 
//				{
//				    if (Files.isRegularFile(filePath)) {
//				    	System.out.println(filePath);
//				        data[filenumber++] = new Data(filePath, 3);
//				    }
//				});
//			} 
//		
//		catch (IOException e) {}
	}
}
