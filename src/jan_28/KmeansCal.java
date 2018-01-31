package jan_28;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class KmeansCal {

	public KmeansCal(Map<String, Map<String, Double>> tfidfMap, int k,String desDir) throws IOException {
		Map<String, Integer> fi = Process(tfidfMap,k);
//		String desDir="E:\\MUC\\1.25\\Result\\Result.txt";	
			saveToFile(fi,desDir);
		
	}
	public void saveToFile(Map<String, Integer> fi, String desDirs) throws IOException {
//		System.out.println(fi.size());
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
	 */
	public Map<String, Integer> Process(Map<String, Map<String, Double>> tfIdfMap, int k) {
		String[] sampleName = new String[tfIdfMap.size()];
		int count = 0;
		int snLength = tfIdfMap.size();
		Set<Map.Entry<String, Map<String, Double>>> tfIdfMapSet = tfIdfMap.entrySet();  
		//文件名称数组      ，把所有的文件名存进了samplename数组  
		for(Iterator<Map.Entry<String, Map<String, Double>>> it = tfIdfMapSet.iterator(); it.hasNext(); ){  
            Map.Entry<String, Map<String, Double>> me = it.next();  
            sampleName[count++] = me.getKey();  
        }
//		均分选择中心点
		Map<Integer, Map<String, Double>> meansMap = getInitPoint(tfIdfMap, k);
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
            if(okCount == snLength) break;  
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
                    clusterMember.put(nearestMeans[i], tempMem);  
                }  
            }  
            //重新计算每个聚类的中心点!  
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
        //8、形成聚类结果并且返回  
        Map<String, Integer> resMap = new TreeMap<String, Integer>();  
        for(int i = 0; i < snLength; i++){  
            resMap.put(sampleName[i], assignMeans[i]);  
        }  
        return resMap;
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
		double mul = 0;//, testAbs = 0, trainAbs = 0;  
        Set<Map.Entry<String, Double>> testWordTFMapSet = map1.entrySet();  
        for(Iterator<Map.Entry<String, Double>> it = testWordTFMapSet.iterator(); it.hasNext();){  
            Map.Entry<String, Double> me = it.next();  
            if(map2.containsKey(me.getKey())){  
                mul += me.getValue()*map2.get(me.getKey());  
            }  
              
        }  

        return mul ;
	}

	/**
	 * 取中心点，是将文件个数/k，均匀分开取的
	 * 得到中心点map k个，
	 * @param tfIdfMap Map<文件名, Map<单词,词向量>>
	 * @param k  k个中心点,存为Map<classNum,Map<单词，词向量>>
	 * @return
	 */
	public Map<Integer, Map<String, Double>> getInitPoint(
			Map<String, Map<String, Double>> tfIdfMap, int k) {
		int count = 0,i = 0;  
        Map<Integer, Map<String, Double>> meansMap = new TreeMap<Integer, Map<String, Double>>();//保存K个聚类中心点向量  
        System.out.println("本次聚类的初始点对应的文件为：");  
        Set<Map.Entry<String, Map<String,Double>>> tfIdfSet = tfIdfMap.entrySet();  
        for(Iterator<Map.Entry<String, Map<String,Double>>> it = tfIdfSet.iterator();it.hasNext();){  
            Map.Entry<String, Map<String,Double>> me = it.next();  
//            取的是第0个和第11个
            int temp = (int)(Math.random()*tfIdfSet.size());
            count = (int)(Math.random()*tfIdfSet.size());
            if(count <= temp){  
                meansMap.put(i, me.getValue());  
                System.out.println(me.getKey() + " map size is " + me.getValue().size());  
                i++;
            }  
            if(i == k){  
            	break;
            }
            
        } 
		return meansMap;
	}

}
