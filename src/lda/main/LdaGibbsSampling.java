package lda.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import lda.com.FileUtil;
import lda.conf.ConstantConfig;
import lda.conf.PathConfig;
import lda.main.Documents;
import lda.main.LdaModel;
//
///**Liu Yang's implementation of Gibbs Sampling of LDA
// * @author yangliu
// * @blog http://blog.csdn.net/yangliuy
// * @mail yangliuyx@gmail.com
// */
//
public class LdaGibbsSampling {
	
	public static class modelparameters {
		public float alpha = 50/6; //usual value is 50 / K
		public float beta = 0.01f;//usual value is 0.1
		public int topicNum = 100;
		public int iteration = 100;
		public int saveStep = 10;
		public int beginSaveIters = 50;
	}
//	
//	/**Get parameters from configuring file. If the 
//	 * configuring file has value in it, use the value.
//	 * Else the default value in program will be used
//	 * @param ldaparameters
//	 * @param parameterFile
//	 * @return void
//	 */
//	private static void getParametersFromFile(modelparameters ldaparameters,
//			String parameterFile) {
//		// TODO Auto-generated method stub
//		ArrayList<String> paramLines = new ArrayList<String>();
//		FileUtil.readLines(parameterFile, paramLines);
//		for(String line : paramLines){
//			String[] lineParts = line.split("\t");
//			switch(parameters.valueOf(lineParts[0])){
//			case alpha:
//				ldaparameters.alpha = Float.valueOf(lineParts[1]);
//				break;
//			case beta:
//				ldaparameters.beta = Float.valueOf(lineParts[1]);
//				break;
//			case topicNum:
//				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
//				break;
//			case iteration:
//				ldaparameters.iteration = Integer.valueOf(lineParts[1]);
//				break;
//			case saveStep:
//				ldaparameters.saveStep = Integer.valueOf(lineParts[1]);
//				break;
//			case beginSaveIters:
//				ldaparameters.beginSaveIters = Integer.valueOf(lineParts[1]);
//				break;
//			}
//		}
//	}
//	
	public enum parameters{
		alpha, beta, topicNum, iteration, saveStep, beginSaveIters;
	}
//	
//	/**
//	 * @param args
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		String originalDocsPath = PathConfig.ldaDocsPath;
//		String resultPath = PathConfig.LdaResultsPath;
//		String parameterFile= ConstantConfig.LDAPARAMETERFILE;
//		
//		modelparameters ldaparameters = new modelparameters();
//		getParametersFromFile(ldaparameters, parameterFile);
//		Documents docSet = new Documents();
//		docSet.readDocs(originalDocsPath);
//		System.out.println("wordMap size " + docSet.termToIndexMap.size());
//		FileUtil.mkdir(new File(resultPath));
//		LdaModel model = new LdaModel(ldaparameters);
//		System.out.println("1 Initialize the model ...");
//		model.initializeModel(docSet);
//		System.out.println("2 Learning and Saving the model ...");
//		model.inferenceModel(docSet);
//		System.out.println("3 Output the final model ...");
//		model.saveIteratedModel(ldaparameters.iteration, docSet);
//		System.out.println("Done!");
//		
//		HashMap<String, Integer> test = new HashMap<String, Integer>();
//		String ldapath = "D:\\eclipseworkspace\\NLPLDAYL\\data\\LdaResults\\lda_100.theta";
//		FileInputStream fi = new FileInputStream(new File(ldapath));
//		BufferedReader br = new BufferedReader(new InputStreamReader(fi));
//		String temp = br.readLine();
//		double m[][] = new double[10][1];
//		while(temp != null){
//			String[] e = temp.split("	");
//			for (int i = 0; i < m.length; i++) {
//				double we = 0;
//				int re = 0;
//				for (int j = 0; j < e.length; j++) {
//					double cn = Double.valueOf(e[j]);
//					if(cn>we){
//						we = cn;
//						re = j;
//					}
//				}
//				test.put(docSet.docNames.get(i), re);
//			}
//			temp = br.readLine();
//		}
//		System.out.println(test);
//	}
}
