package march;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;


import lda.com.FileUtil;
import lda.conf.ConstantConfig;
import lda.conf.PathConfig;
import lda.main.Documents;
import lda.main.LdaModel;
import lda.main.LdaGibbsSampling.modelparameters;
import lda.main.LdaGibbsSampling.parameters;



public class Kmeans {

	public Kmeans(Map<String, Map<String, Double>> tfidfMap, int k,String desDir) throws IOException {
		
		Map<String, Integer> fi = Process(tfidfMap,k);
//		String desDir="E:\\MUC\\1.25\\Result\\Result.txt";	
			saveToFile(fi,desDir);
		
	}
	public void saveToFile(Map<String, Integer> fi, String desDirs) throws IOException {
//		System.out.println(fi.size());
		File file  = new File(desDirs);
		if(file.exists()){
			file.delete();
		}
		FileWriter resWriter = new FileWriter(desDirs,true);  
        Set<Map.Entry<String,Integer>> fiSet = fi.entrySet();  
        for(Iterator<Map.Entry<String,Integer>> it = fiSet.iterator(); it.hasNext(); ){  
            Map.Entry<String, Integer> me = it.next();  
            resWriter.append(me.getKey() + " " + me.getValue() + "\n");  
        }  
        resWriter.flush();  
        resWriter.close();  
    }
	/**
	 * 对数据进行处理
	 * @param tfIdfMap
	 * @param k
	 * @return 
	 * @throws IOException 
	 */
	public Map<String, Integer> Process(Map<String, Map<String, Double>> tfIdfMap, int k) throws IOException {
		String[] sampleName = new String[tfIdfMap.size()];
		int count = 0;
		int snLength = tfIdfMap.size();
		Set<Map.Entry<String, Map<String, Double>>> tfIdfMapSet = tfIdfMap.entrySet();  
		//文件名称数组      ，把所有的文件名存进了samplename数组  
		for(Iterator<Map.Entry<String, Map<String, Double>>> it = tfIdfMapSet.iterator(); it.hasNext(); ){  
            Map.Entry<String, Map<String, Double>> me = it.next();  
            sampleName[count++] = me.getKey();  
        }
//		使用传统kmeans获取中心点
//		Map<Integer, Map<String, Double>> meansMap = getInitPointKMS(tfIdfMap,sampleName, k);
		
//		使用kmeans++获取中心点
		Map<Integer, Map<String, Double>> meansMap = getInitPointKPlus(tfIdfMap,sampleName, k);
//		使用LDA获取中心点
//		Map<Integer, Map<String, Double>> meansMap = getInitPointLDA(tfIdfMap,sampleName,k);
//		System.out.println(meansMap);
//		记录点i到聚类中心点j的距离
		double [][] distance = new double[snLength][k];
//		初始化k个聚类
		int[] assignMeans = new int[snLength];
		//记录每个聚类的成员点序号
		Map<Integer, Vector<Integer>> clusterMember = new TreeMap<Integer,Vector<Integer>>();  
	    Vector<Integer> mem = new Vector<Integer>();  
	    int iterNum = 0;//迭代次数
	    while(true){  
            System.out.println("Iteration No." + (iterNum++) + "----------------------");  
            //计算每个点和每个聚类中心的距离  
            for(int i = 0; i < snLength; i++){  
                for(int j = 0; j < k; j++){  
                     distance[i][j] = getDistance(tfIdfMap.get(sampleName[i]),meansMap.get(j));  
                }  
            } 
//          找到与每个点最近的聚类中心
            int[] nearestMeans = new int[snLength];  
            for(int i = 0; i < snLength; i++){  
                nearestMeans[i] = findNearestMeans(distance, i);  
            }
            //判断当前所有点属于的聚类序号是否已经全部是其离得最近的聚类
            int okCount = 0;  
            for(int i = 0; i <snLength; i++){  
                if(nearestMeans[i] == assignMeans[i]) okCount++;  
            }  
            System.out.println("okCount = " + okCount); 
//            不给定迭代次数
//            if(iterNum == 26) break;
            if(okCount == snLength){
//            	//////////////////////////////////////////////////
            	System.out.println(sortmap(meansMap));
            	break;  
            }
            //如果前面条件不满足，那么需要重新聚类再进行一次迭代，需要修改每个聚类的成员和每个点属于的聚类信息  
            clusterMember.clear();  
            for(int i = 0; i < snLength; i++){  
                assignMeans[i] = nearestMeans[i];  
                if(clusterMember.containsKey(nearestMeans[i])){  
                    clusterMember.get(nearestMeans[i]).add(i);    
                }  
                else {  
                    mem.clear();  
                    mem.add(i);  
                    Vector<Integer> tempMem = new Vector<Integer>();  
                    tempMem.addAll(mem);  
//                    System.out.println(tempMem);
                    clusterMember.put(nearestMeans[i], tempMem);  
                }  
            }  
            //重新计算每个聚类的中心点
            for(int i = 0; i < k; i++){  
                if(!clusterMember.containsKey(i)){//注意kmeans可能产生空聚类  
                    continue;  
                }  
                Map<String, Double> newMean = computeNewMean(clusterMember.get(i), tfIdfMap, sampleName);  
                Map<String, Double> tempMean = new TreeMap<String, Double>();
                tempMean.putAll(newMean);  
                meansMap.put(i, tempMean);  
            }
        }  
        //形成聚类结果并且返回  
        Map<String, Integer> resMap = new TreeMap<String, Integer>();  
        for(int i = 0; i < snLength; i++){  
            resMap.put(sampleName[i], assignMeans[i]);  
        } 
        return resMap;
	}

	private Map<Integer, Map<String, Double>> getInitPointKMS(
			Map<String, Map<String, Double>> tfIdfMap, String[] sampleName,
			int k) {
		int count = 0,i = 0,l=0;	
		int snLength = sampleName.length;
		Map<Integer, Map<String, Double>> meansMap = new TreeMap<Integer, Map<String, Double>>();//保存K个聚类中心点向量  
        System.out.println("本次聚类的初始点对应的文件为：");  
        Set<Map.Entry<String, Map<String,Double>>> tfIdfSet = tfIdfMap.entrySet();  
        for(Iterator<Map.Entry<String, Map<String,Double>>> it = tfIdfSet.iterator();it.hasNext();){  
            Map.Entry<String, Map<String,Double>> me = it.next();
//            随机取第一个点  
            
            int j = (int)(Math.random()*sampleName.length);
            while(l!=j){
            	me = it.next();
            	l++;
            }
        	meansMap.put(i,me.getValue());
        	l = 0;
        	System.out.println(i);
        	i++;
        	System.out.println(me.getKey() + " map size is " + me.getValue().size()); 
        	it = tfIdfSet.iterator();
        	if(i==k){
        		break;
        	}
        }
        	
        return meansMap;
	}
	private Map<Integer, Map<String, Double>> getInitPointLDA(
			Map<String, Map<String, Double>> tfIdfMap, String[] sampleName,int k) throws IOException {
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
		
		Map<String, Integer> test = new HashMap<String, Integer>();
		Map<Integer, Map<String,Double>> lda = new HashMap<Integer, Map<String,Double>>();
		
		String ldapath = "E:\\MUC\\三月\\3000\\SampleResult\\LdaResults\\lda_100.theta";
		FileInputStream fi = new FileInputStream(new File(ldapath));
		BufferedReader br = new BufferedReader(new InputStreamReader(fi));
		String temp = br.readLine();
		int count = 0;
		while(temp != null){
			String[] e = temp.split("	");
				double we = 0;
				int re = 0;
				for (int j = 0; j < k; j++) {
					double cn = Double.valueOf(e[j]);
					if(cn>we){
						we = cn;
						re = j;
					}
				}
			test.put(docSet.docNames.get(count), re);
			count++;
			temp = br.readLine();
			
		}
//		System.out.println("test size = "+test.size());
//		System.out.println(sampleName[1397]);
		for (int i = 0; i < k; i++) {
			Map<String, Double> tq = new HashMap<String, Double>();
			int mem = 0;
			for (int j = 0; j < test.size(); j++) {
				if(test.get(sampleName[j]) == i){
					mem++;
					Set<Map.Entry<String, Double>> wer = tfIdfMap.get(sampleName[j]).entrySet();
					for(Iterator<Map.Entry<String, Double>> jt = wer.iterator();jt.hasNext();){
						Map.Entry<String, Double> ne = jt.next();
						if (tq.containsKey(ne.getKey())){
							tq.put(ne.getKey(), tq.get(ne.getKey())+ne.getValue());
						}else{
							tq.put(ne.getKey(),ne.getValue());
						}
					}
				}
			}
			Set<Map.Entry<String, Double>> newMeanMapSet = tq.entrySet();  
	        for(Iterator<Map.Entry<String, Double>> jt = newMeanMapSet.iterator(); jt.hasNext();){  
	            Map.Entry<String, Double> ne = jt.next();  
	            tq.put(ne.getKey(), tq.get(ne.getKey()) / mem);     
	        }
	        lda.put(i, tq);
		}
		return lda;
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
	
	
	
	private Map<Integer, Map<String, Double>> sortmap(Map<Integer, Map<String, Double>> meansMap) {
		Map<Integer,Map<String, Double>> finalMap = new HashMap<Integer,Map<String, Double>>();
		for (int i = 0; i < meansMap.size(); i++) {
			Map<String, Double> temp = meansMap.get(i);
			List<Map.Entry<String, Double>> tempList = new ArrayList<Map.Entry<String,Double>>(temp.entrySet());
			Collections.sort(tempList, new Comparator<Map.Entry<String, Double>>() {
				public int compare(Map.Entry<String, Double> obj1 , Map.Entry<String, Double> obj2) {
					Double d1 = obj1.getValue();
					Double d2 = obj2.getValue();
					return d2.compareTo(d1);
				} 
			});
			Map<String, Double> test = new HashMap<String, Double>();
			for (int j = 0; j < 10; j++) {
				test.put(tempList.get(j).getKey(), tempList.get(j).getValue());				
			}
			finalMap.put(i, test);
		}
		return finalMap;
	}
	/**计算当前聚类新的中心，采用向量平均 
     * @param clusterM 所有属于该聚类的点
     * @param allTestSampleMap 所有测试样例的TfIdfMap 
     * @param testSampleNames 所有测试样例文件名构成的数组 
     * @return Map<String, Double> 新的聚类中心的向量 
     * @throws IOException  
     */  
    private Map<String, Double> computeNewMean(Vector<Integer> clusterM,  
            Map<String, Map<String, Double>> allTestSampleMap,  
            String[] testSampleNames) {  
        double memberNum = (double)clusterM.size();  
        Map<String, Double> newMeanMap = new TreeMap<String,Double>();  
        Map<String, Double> currentMemMap = new TreeMap<String,Double>();  
        for(Iterator<Integer> it = clusterM.iterator(); it.hasNext();){  
            int me = it.next();  
            currentMemMap = allTestSampleMap.get(testSampleNames[me]);  
            Set<Map.Entry<String, Double>> currentMemMapSet = currentMemMap.entrySet();  
            for(Iterator<Map.Entry<String, Double>> jt = currentMemMapSet.iterator(); jt.hasNext();){  
                Map.Entry<String, Double> ne = jt.next();  
                if(newMeanMap.containsKey(ne.getKey())){  
                    newMeanMap.put(ne.getKey(), newMeanMap.get(ne.getKey()) + ne.getValue());  
                }   
                else {  
                    newMeanMap.put(ne.getKey(), ne.getValue());  
                }  
            }  
        }  
          
        Set<Map.Entry<String, Double>> newMeanMapSet = newMeanMap.entrySet();  
        for(Iterator<Map.Entry<String, Double>> jt = newMeanMapSet.iterator(); jt.hasNext();){  
            Map.Entry<String, Double> ne = jt.next();  
            newMeanMap.put(ne.getKey(), newMeanMap.get(ne.getKey()) / memberNum);     
        }  
        return newMeanMap;  
    } 

	public int findNearestMeans(double[][] distance, int m) {
		double minDist = 10;  
        int j = 0;  
        for(int i = 0; i < distance[m].length; i++){  
            if(distance[m][i] < minDist){  
                minDist = distance[m][i];  
                j = i;  
            }  
        }  
        return j;  
    }

	public double getDistance(Map<String, Double> map1, Map<String, Double> map2) {
		return 1-cos(map1,map2);
	}

	public double cos(Map<String, Double> map1, Map<String, Double> map2) {
		double mul = 0;
		double testAbs = 0, trainAbs = 0;  
        Set<Map.Entry<String, Double>> testWordTFMapSet = map1.entrySet();  
        for(Iterator<Map.Entry<String, Double>> it = testWordTFMapSet.iterator(); it.hasNext();){  
            Map.Entry<String, Double> me = it.next();  
            if(map2.containsKey(me.getKey())){  
                mul += me.getValue()*map2.get(me.getKey());  
            }  
            testAbs += me.getValue() * me.getValue();  
        }  
        testAbs = Math.sqrt(testAbs);  
        
        Set<Map.Entry<String, Double>> trainWordTFMapSet = map2.entrySet(); 
        for(Iterator<Map.Entry<String, Double>> it = trainWordTFMapSet.iterator(); it.hasNext();){ 
            Map.Entry<String, Double> me = it.next(); 
            trainAbs += me.getValue()*me.getValue(); 
        } 
        trainAbs = Math.sqrt(trainAbs);
        double cos = (double)(mul)/(testAbs*trainAbs);
        return cos ;
	}

	/**
	 * 取中心点，Kmeans++
	 * 得到中心点map k个，
	 * @param tfIdfMap Map<文件名, Map<单词,词向量>>
	 * @param sampleName 
	 * @param k  k个中心点,存为Map<classNum,Map<单词，词向量>>
	 * @return
	 */
	public Map<Integer, Map<String, Double>> getInitPointKPlus(
			Map<String, Map<String, Double>> tfIdfMap, String[] sampleName, int k) {
		int count = 0,i = 0,l=0;
		
		int snLength = sampleName.length;
		Map<Integer, Map<String, Double>> meansMap = new TreeMap<Integer, Map<String, Double>>();//保存K个聚类中心点向量  
        System.out.println("本次聚类的初始点对应的文件为：");  
        Set<Map.Entry<String, Map<String,Double>>> tfIdfSet = tfIdfMap.entrySet();  
        for(Iterator<Map.Entry<String, Map<String,Double>>> it = tfIdfSet.iterator();it.hasNext();){  
            Map.Entry<String, Map<String,Double>> me = it.next();
//            随机取第一个点  
            if(i == 0){
            	int q = (int)(Math.random()*1500);
            	for(int t = 0;t<q;t++){
            		me = it.next();
            	}
                meansMap.put(i, me.getValue()); 
                System.out.println(i);
                i++;
                System.out.println(me.getKey() + " map size is " + me.getValue().size()); 
            }
            it = tfIdfSet.iterator();
            
            int j = getPointByKplus(snLength,tfIdfMap,sampleName,meansMap);
            while(l!=j){
            	me = it.next();
            	l++;
            }
        	meansMap.put(i,me.getValue());
        	l = 0;
        	System.out.println(i);
        	i++;
        	System.out.println(me.getKey() + " map size is " + me.getValue().size()); 
        	it = tfIdfSet.iterator();
        	if(i==k){
        		break;
        	}
        }
        return meansMap;
    }

	/**
	 * 
	 * 
	 * @param snLength  数据集的个数
	 * @param i 现有的聚类点数+1
	 * @param tfIdfMap
	 * @param sampleName  文件名
	 * @param tempMap  <簇k,<单词，词向量>>
	 * @return
	 */
	public int getPointByKplus(int snLength, Map<String, Map<String, Double>> tfIdfMap, String[] sampleName, Map<Integer, Map<String, Double>> tempMap){
//		距离distance[自己][聚类点]
		int i = tempMap.size();
		double distance[][] = new double[snLength][i];
        int temp = 0;
        
        
        double d[] = new double [snLength];
        double sum = 0;
        
		for(int m = 0; m < snLength; m++){
			double min = 10000.000;
			for(int j = 0; j < i; j++){
                distance[m][j] = getDistance(tfIdfMap.get(sampleName[m]),tempMap.get(j)); 
                if(distance[m][j]<min){
                	min = distance[m][j];
                }  
			}
			d[m] = min;
			sum = sum+d[m];
        }
		double rand = Math.random()*sum;
        for (int j = 0; j < d.length; j++) {
			rand -= d[j];
			if (rand <=0) {
				temp = j;
				break;
			}
		}
        System.out.println("-----------temp:"+temp);
//        ----------------------
		return temp;
	}

}
