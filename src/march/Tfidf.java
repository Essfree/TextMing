package march;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import lda.com.FileUtil;
import lda.conf.ConstantConfig;
import lda.conf.PathConfig;
import lda.main.Documents;
import lda.main.LdaModel;
import lda.main.LdaGibbsSampling.modelparameters;
import lda.main.LdaGibbsSampling.parameters;



public class Tfidf {
	public static List<String> fileList = new ArrayList<String>();
	public static List<String> alllist = new ArrayList<String>();
	//
	public static Map<String, Integer> dictMap = new HashMap<String, Integer>();
	/**
	 * allFileList  sample文件夹中所有文件名(不包括文件夹)
	 * allFile      把每篇文章当做一个元素放进list-->allFile里
	 * @param splitDir  sample文件夹
	 * @return 
	 * @throws IOException 
	 */
//	public static int top = 20;
	public Map<String, Map<String, Double>> CalTfIdf(String splitDir) throws IOException {
		
		String originalDocsPath = PathConfig.ldaDocsPath;
		String resultPath = PathConfig.LdaResultsPath;
		String parameterFile= ConstantConfig.LDAPARAMETERFILE;
		
		modelparameters ldaparameters = new modelparameters();
		getParametersFromFile(ldaparameters, parameterFile);
		Documents docSet = new Documents();
		docSet.readDocs(originalDocsPath);
		System.out.println("wordMap size " + docSet.termToIndexMap.size());
		FileUtil.mkdir(new File(resultPath));
		LdaModel model = new LdaModel(ldaparameters);
		System.out.println("1 Initialize the model ...");
		model.initializeModel(docSet);
		System.out.println("2 Learning and Saving the model ...");
		model.inferenceModel(docSet);
		System.out.println("3 Output the final model ...");
		model.saveIteratedModel(ldaparameters.iteration, docSet);
		System.out.println("Done!");
		
		String ldapath = "E:\\MUC\\三月\\3.23-3.29\\SampleResult\\LdaResults\\lda_100.keywords";
		FileInputStream fi = new FileInputStream(new File(ldapath));
		BufferedReader br = new BufferedReader(new InputStreamReader(fi));
		String linetemp = br.readLine();
		int count = 0;
		while(linetemp != null){
			String[] e = linetemp.split("\t");
				for (int j = 0; j < e.length; j++) {
					System.out.println(e[j]);
					if(dictMap.containsKey(e[j])){
						dictMap.put(e[j],dictMap.get(e[j])+1);
					}else{
						dictMap.put(e[j],1);
					}
				}
			linetemp = br.readLine();
		}
		System.out.println(dictMap.size());
		
		List allFileList = readDirs(splitDir);
		
		List allFile = readFile(allFileList);
		//得到所有文件个数
		int totalFile = allFileList.size();
//		System.out.println(totalFile);
//		System.out.println(allFile.get(5));
//		Map<String, Integer> totalCount = new HashMap<String, Integer>();
		
		Map<String,Map<String, Double>> tfIdfMap = new HashMap<String, Map<String,Double>>(); 
		for(int i = 0;i<totalFile;i++){
//			Map<String,Double> temp = new HashMap<String,Double>();
			String tString = allFile.get(i).toString();
			String[] sp = tString.split(" ");
		}
//		System.out.println(dictMap);
		for(int i = 0;i<totalFile;i++){
			Map<String,Double> temp = new HashMap<String,Double>();
			String tString = allFile.get(i).toString();
			String[] sp = tString.split(" ");
//			System.out.println(allFileList.get(i));
//			//计算某篇文档的单词和数目
//			temp = cal(sp);
//			//得到总文档的单词和单词数
//			totalCount.putAll(temp);
			temp = tfidf(sp, allFile);
			tfIdfMap.put((String) allFileList.get(i), temp);
		}
		//将得到的总文档的单词逆序排序整理并取前20%
//		Map<String, Integer> totalSort =sortMap(totalCount);
		System.out.println(tfIdfMap.size());
		return tfIdfMap;
	}
	

	private static void getParametersFromFile(modelparameters ldaparameters,
			String parameterFile) {
		// TODO Auto-generated method stub
		ArrayList<String> paramLines = new ArrayList<String>();
		FileUtil.readLines(parameterFile, paramLines);
		for(String line : paramLines){
			String[] lineParts = line.split("\t");
			switch(parameters.valueOf(lineParts[0])){
			case alpha:
				ldaparameters.alpha = Float.valueOf(lineParts[1]);
				break;
			case beta:
				ldaparameters.beta = Float.valueOf(lineParts[1]);
				break;
			case topicNum:
				ldaparameters.topicNum = Integer.valueOf(lineParts[1]);
				break;
			case iteration:
				ldaparameters.iteration = Integer.valueOf(lineParts[1]);
				break;
			case saveStep:
				ldaparameters.saveStep = Integer.valueOf(lineParts[1]);
				break;
			case beginSaveIters:
				ldaparameters.beginSaveIters = Integer.valueOf(lineParts[1]);
				break;
			}
		}
	}


	private Map<String, Double> tfidf(String[] sp, List<String> allFile) {
		Map<String, Double> tfMap = tf(sp);
//		System.out.println(tfMap);
		Map<String,Double> idfMap = idf(tfMap,allFile);
//		System.out.println(idfMap);
		Map<String, Double> tfidfMap = tfIdf(tfMap,idfMap);
//		System.out.println(tfidfMap);
//		return tfidfMap;
		return tfidfMap;
	}

    private Map<String, Double> idf(Map<String, Double> tfMap,
			List<String> allFile) {
    	int totalCount = allFile.size();
    	Map<String, Double> idfMap = new HashMap<String, Double>();
    	List<Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
    	
    	for(int i = 0;i<tfMap.size();i++){
    		idfMap.put(tfList.get(i).getKey(), Math.log((double)totalCount/(1+dictMap.get(tfList.get(i).getKey()))));
    		
    	}
    	return idfMap;
	}

	private Map<String, Double> tfIdf(Map<String, Double> tfMap,Map<String, Double> idfMap) {
		List <Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
		List <Map.Entry<String, Double>> idfList = new ArrayList<Map.Entry<String,Double>>(idfMap.entrySet());
		HashMap<String, Double> temp = new HashMap<String, Double>();
		for (int i = 0; i < tfList.size(); i++) {
			temp.put(tfList.get(i).getKey(),((tfList.get(i).getValue())*(idfList.get(i).getValue())));	
		}
		List<Map.Entry<String, Double>> tfidfList = new ArrayList<Map.Entry<String,Double>>(temp.entrySet());
		Collections.sort(tfidfList, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> obj1 , Map.Entry<String, Double> obj2) {
				Double d1 = obj1.getValue();
				Double d2 = obj2.getValue();
				return d2.compareTo(d1);
			} 
		});
		HashMap<String, Double> tfidfMap = new HashMap<String, Double>();
		
			double topN = tfidfList.size();
			if(tfidfList.size()>=topN){
				for (int i = 0; i < topN; i++) {
					tfidfMap.put(tfidfList.get(i).getKey(), tfidfList.get(i).getValue());
				}
			}else{
				for(int i = 0;i< tfidfList.size();i++){
					tfidfMap.put(tfidfList.get(i).getKey(),tfidfList.get(i).getValue());
				}
			}
			
		return tfidfMap;
	}


//	private void dictMap(Map<String, Double> tfMap) {
////		int fileCount = allFile.size();
//		List <Map.Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
//		Map<String, Double> idfMap = new HashMap<String, Double>();
//		for(int i = 0;i<tfList.size();i++){
//			if(dictMap.containsKey(tfList.get(i).getKey())){
//				dictMap.put(tfList.get(i).getKey(), dictMap.get(tfList.get(i).getKey())+1);
//			}else{
//				dictMap.put(tfList.get(i).getKey(), 1);
//			}
//		}
//		
//	}


	/**
     * 
     * @param TextContent
     * @return HashMap<String, Double> tfMap  <单词，单词的tf值>
     */
	private HashMap<String, Double> tf(String[] TextContent) {
		HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
		for(String s : TextContent){
			if(!s.equals(" ") && dictMap.containsKey(s)){
				if(tfMap.get(s)!=null){
					tfMap.put(s,tfMap.get(s)+1);
				}else{
					tfMap.put(s,1);
				}
			}
		}
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(tfMap.entrySet());
		//得到文章单词总数
		int count = 0;
		for(int i = 0;i<infoIds.size();i++){
			count = count+infoIds.get(i).getValue();
		}
		//求得词频并将该词及其词频存到hashmap中
		HashMap<String, Double> map = new HashMap<String, Double>();
		for (int i = 0; i < infoIds.size(); i++) { 
	    	map.put(infoIds.get(i).getKey(), (double)infoIds.get(i).getValue()/count);
//				    	System.out.print(infoIds.get(i));
		}
		return map;
	}


	/**
	 * 把每篇文章当做一个元素放进list里
	 * @param fileList
	 * @return List
	 * 文档总篇数就是list.size
	 */
	public List<String> readFile(List<String> fileList){
//		List<String> alllist = new ArrayList<String>();
		String snppi=null;
		for(String file : fileList)
		{
			
			snppi= MyFileReader.read(file);
			snppi = snppi.replace("　　","");
			alllist.add(snppi);
		}
		return alllist;
	}



	/**
	 * 递归获得指定文件夹中的所有文件
	 * @return fileList
	 * @param  filepath
	 */
	public List<String> readDirs(String filepath) {
//		List<String> fileList = new ArrayList<String>();
		File file = new File(filepath);
		if(!file.isDirectory()){
			System.out.println("这不是文件夹名 : "+file.getAbsolutePath());
		}else if(file.isDirectory()){
			String[] filelist = file.list();
			for(int i = 0;i<filelist.length;i++){
				File readFile = new File(filepath + File.separator + filelist[i]);
				if(!readFile.isDirectory()){
					fileList.add(readFile.getAbsolutePath());
				}else if(readFile.isDirectory()){
					readDirs(filepath+File.separator+filelist[i]);
				}
			
			}
		}
		return fileList;
	}

}



//package march;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import tfidf.MyFileReader;
//
//public class Tfidf {
//	public static List<String> fileList = new ArrayList<String>();
//	public static List<String> alllist = new ArrayList<String>();
//	//
//	public static Map<String, Integer> dictMap = new HashMap<String, Integer>();
//	/**
//	 * allFileList  sample文件夹中所有文件名(不包括文件夹)
//	 * allFile      把每篇文章当做一个元素放进list-->allFile里
//	 * @param splitDir  sample文件夹
//	 * @return 
//	 */
////	public static int top = 20;
//	public Map<String, Map<String, Double>> CalTfIdf(String splitDir) {
//		
//		List allFileList = readDirs(splitDir);
//		
//		List allFile = readFile(allFileList);
//		//得到所有文件个数
//		int totalFile = allFileList.size();
////		System.out.println(totalFile);
////		System.out.println(allFile.get(5));
////		Map<String, Integer> totalCount = new HashMap<String, Integer>();
//		
//		Map<String,Map<String, Double>> tfIdfMap = new HashMap<String, Map<String,Double>>(); 
//		for(int i = 0;i<totalFile;i++){
////			Map<String,Double> temp = new HashMap<String,Double>();
//			String tString = allFile.get(i).toString();
//			String[] sp = tString.split(" ");
//			dictMap(tf(sp));
//		}
////		System.out.println(dictMap);
//		double den = 0;
//		for(int i = 0;i<totalFile;i++){
//			Map<String,Double> tftemp = new HashMap<String,Double>();
//			Map<String,Double> idftemp = new HashMap<String,Double>();
//			String tString = allFile.get(i).toString();
//			String[] sp = tString.split(" ");
////			System.out.println(allFileList.get(i));
////			//计算某篇文档的单词和数目
////			temp = cal(sp);
////			//得到总文档的单词和单词数
////			totalCount.putAll(temp);
////			temp = tfidf(sp, allFile);
//			tftemp = tf(sp);
//			idftemp = idf(tftemp,allFile.size());
////			System.out.println(tftemp);
////			System.out.println(idftemp);
//			den = den + countd(tftemp,idftemp);
////			tfIdfMap.put((String) allFileList.get(i), temp);
//		}
//		//将得到的总文档的单词逆序排序整理并取前20%
////		Map<String, Integer> totalSort =sortMap(totalCount);
//		System.out.println(tfIdfMap.size());
//		
//		return tfIdfMap;
//	}
//	
//
////	private Map<String, Double> tfidf(String[] sp, List<String> allFile) {
////		Map<String, Double> tfMap = tf(sp);
//////		System.out.println(tfMap);
////		Map<String,Double> idfMap = idf(tfMap);
//////		System.out.println(idfMap);
//////		Map<String, Double> tfidfMap = tfIdf(tfMap,idfMap,allFile);
//////		System.out.println(tfidfMap);
//////		return tfidfMap;
////		return tfidfMap;
//
////	}
//	private double countd(Map<String, Double> tftemp,Map<String, Double> idftemp) {
//		double q = 0;
//		List<Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tftemp.entrySet());
//		List<Entry<String, Double>> idfList = new ArrayList<Map.Entry<String,Double>>(idftemp.entrySet());
//		for (int i = 0; i < tfList.size(); i++) {
//			q = q + tfList.get(i).getValue()*idfList.get(i).getValue();
//		}
//		return q;
//	}
//
//
//
//    private Map<String, Double> idf(Map<String, Double> tfMap, int total) {
//    	Map<String, Double> idfMap = new HashMap<String, Double>();
//    	List<Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(tfMap.entrySet());
//    	
//    	for(int i = 0;i<tfMap.size();i++){
//    		idfMap.put(tfList.get(i).getKey(), Math.log((double)total/(dictMap.get(tfList.get(i).getKey()))));
//    	}
//    	return idfMap;
//	}
//
//	private Map<String, Double> tfIdf(Map<String, Integer> tfMap,Map<String, Double> idfMap,List<String> allFile) {
//    	int totalCount = allFile.size();
//		List <Map.Entry<String, Integer>> tfList = new ArrayList<Map.Entry<String,Integer>>(tfMap.entrySet());
//		List <Map.Entry<String, Double>> idfList = new ArrayList<Map.Entry<String,Double>>(idfMap.entrySet());
//		HashMap<String, Double> temp = new HashMap<String, Double>();
//		double total = 0;
//		for (int i = 0; i < tfList.size(); i++) {
//			double tf = Math.log(tfList.get(i).getValue()+0.1);
//			double idf = Math.log((double)totalCount/idfList.get(i).getValue());
//			total += (tf*idf)*(tf*idf);
////			temp.put(tfList.get(i).getKey(),((tfList.get(i).getValue())*(idfList.get(i).getValue())));	
//		}
//		double m = Math.sqrt(total);
//		for (int i = 0; i < tfList.size(); i++) {
//			double tf = Math.log(tfList.get(i).getValue()+1);
//			double idf = Math.log((double)totalCount/idfList.get(i).getValue());
//			double w = (double)(tf*idf)/m;
//			temp.put(tfList.get(i).getKey(), w);
////			System.out.println("w ---"+w);
//		}
//		List<Map.Entry<String, Double>> tfidfList = new ArrayList<Map.Entry<String,Double>>(temp.entrySet());
//		Collections.sort(tfidfList, new Comparator<Map.Entry<String, Double>>() {
//			public int compare(Map.Entry<String, Double> obj1 , Map.Entry<String, Double> obj2) {
//				Double d1 = obj1.getValue();
//				Double d2 = obj2.getValue();
//				return d2.compareTo(d1);
//			} 
//		});
//		HashMap<String, Double> tfidfMap = new HashMap<String, Double>();
//		
//			double topN = tfidfList.size()*0.8;
//			if(tfidfList.size()>=topN){
//				for (int i = 0; i < topN; i++) {
//					tfidfMap.put(tfidfList.get(i).getKey(), tfidfList.get(i).getValue());
//				}
//			}else{
//				for(int i = 0;i< tfidfList.size();i++){
//					tfidfMap.put(tfidfList.get(i).getKey(),tfidfList.get(i).getValue());
//				}
//			}
//		return tfidfMap;
//	}
//
//
//	private void dictMap(HashMap<String, Double> hashMap) {
////		int fileCount = allFile.size();
//		List<Entry<String, Double>> tfList = new ArrayList<Map.Entry<String,Double>>(hashMap.entrySet());
//		Map<String, Double> idfMap = new HashMap<String, Double>();
//		for(int i = 0;i<tfList.size();i++){
//			if(dictMap.containsKey(tfList.get(i).getKey())){
//				dictMap.put(tfList.get(i).getKey(), dictMap.get(tfList.get(i).getKey())+1);
//			}else{
//				dictMap.put(tfList.get(i).getKey(), 1);
//			}
//		}
//		
//	}
//
//
//	/**
//     * 
//     * @param TextContent
//     * @return HashMap<String, Double> tfMap  <单词，单词的tf值>
//     */
//	private HashMap<String, Double> tf(String[] TextContent) {
//		HashMap<String, Integer> tfMap = new HashMap<String, Integer>();
//		for(String s : TextContent){
//			if(!s.equals(" ")){
//				if(tfMap.get(s)!=null){
//					tfMap.put(s,tfMap.get(s)+1);
//				}else{
//					tfMap.put(s,1);
//				}
//			}
//		}
//		
//		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(tfMap.entrySet());
////		//得到文章单词总数
////		int count = 0;
////		for(int i = 0;i<infoIds.size();i++){
////			count = count+infoIds.get(i).getValue();
////		}
////		//求得词频并将该词及其词频存到hashmap中
//		HashMap<String, Double> map = new HashMap<String, Double>();
//		for (int i = 0; i < infoIds.size(); i++) { 
//	    	map.put(infoIds.get(i).getKey(), Math.log(infoIds.get(i).getValue()+0.1));
////				    	System.out.print(infoIds.get(i));
//		}
//		return map;
//	}
//
//
//	/**
//	 * 把每篇文章当做一个元素放进list里
//	 * @param fileList
//	 * @return List
//	 * 文档总篇数就是list.size
//	 */
//	public List<String> readFile(List<String> fileList){
////		List<String> alllist = new ArrayList<String>();
//		String snppi=null;
//		for(String file : fileList)
//		{
//			
//			snppi= MyFileReader.read(file);
//			snppi = snppi.replace("　　","");
//			alllist.add(snppi);
//		}
//		return alllist;
//	}
//
//
//
//	/**
//	 * 递归获得指定文件夹中的所有文件
//	 * @return fileList
//	 * @param  filepath
//	 */
//	public List<String> readDirs(String filepath) {
////		List<String> fileList = new ArrayList<String>();
//		File file = new File(filepath);
//		if(!file.isDirectory()){
//			System.out.println("这不是文件夹名 : "+file.getAbsolutePath());
//		}else if(file.isDirectory()){
//			String[] filelist = file.list();
//			for(int i = 0;i<filelist.length;i++){
//				File readFile = new File(filepath + File.separator + filelist[i]);
//				if(!readFile.isDirectory()){
//					fileList.add(readFile.getAbsolutePath());
//				}else if(readFile.isDirectory()){
//					readDirs(filepath+File.separator+filelist[i]);
//				}
//			
//			}
//		}
//		return fileList;
//	}
//
//}
