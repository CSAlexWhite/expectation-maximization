package cs363.assignment2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
	
	static String filename;
	static int filenumber = 0;
	
	static BinaryProbability pFemale, pThinFemale, pThinMale, pShortFemale, pShortMale;
	
	public static void main(String[] args){
			
		Data data1 = new Data("data/hw2dataset_70.txt", 3);	
		//data1.printData();
		
		pFemale = new BinaryProbability(0.7);
		pThinFemale = new BinaryProbability(0.8);
		pThinMale = new BinaryProbability(0.4);
		pShortFemale = new BinaryProbability(0.7);
		pShortMale = new BinaryProbability(0.3);
		
		
		//for(int i=0; i<10; i++){	
			
			BinaryBayesArc GenderHeight = 
					new BinaryBayesArc("Gender", "Height",
							data1, 0, 1, pFemale, pThinFemale, pThinMale);
			
			BinaryBayesArc GenderWeight = 
					new BinaryBayesArc("Gender", "Weight", 
							data1, 0, 2, pFemale, pShortFemale, pShortMale);
			
			pFemale.set( (	GenderHeight.pChildgTrueParent.pTrue *  
					GenderWeight.pParentgTrueChild.pTrue)
					/ GenderHeight.pChild.pTrue);
			
//			for(int i=0; i<10; i++){
//				
//				GenderHeight.loop();
//				GenderWeight.loop();
//			}
			
//			pFemale.set( (	GenderHeight.pChildgTrueParent.pTrue *  
//							GenderWeight.pParentgTrueChild.pTrue)
//							/ GenderHeight.pChild.pTrue);
//			
			System.out.println(pFemale.pTrue);
		//}
		
//		pFemale.set( GenderHeight.pChildgTrueParent.pTrue * GenderWeight.pParentgTrueChild.pTrue);
		
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
