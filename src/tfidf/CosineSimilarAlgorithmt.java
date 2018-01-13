package tfidf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CosineSimilarAlgorithmt{
	public double cosine(HashMap<String, Double> tfidfa,HashMap<String, Double> tfidfb){

		HashMap<String,Double> all = new HashMap<String, Double>();

		all.putAll(tfidfa);
		all.putAll(tfidfb);
		List<Entry<String, Double>> allt = new ArrayList<Map.Entry<String,Double>>(all.entrySet());
//		System.out.println(all);
		for(int i=0;i<allt.size();i++){
			if(tfidfa.get(allt.get(i).getKey())==null){
				tfidfa.put(allt.get(i).getKey(), 0.0);
			}
		}
		for(int i=0;i<allt.size();i++){
			if(tfidfb.get(allt.get(i).getKey())==null){
				tfidfb.put(allt.get(i).getKey(), 0.0);
			}
		}
		
		List<Entry<String, Double>> aList = new ArrayList<Map.Entry<String,Double>>(tfidfa.entrySet());
		List<Entry<String, Double>> bList = new ArrayList<Map.Entry<String,Double>>(tfidfb.entrySet());
		double cos = 0,cosU = 0,cosLa = 0,cosLb = 0,cosL = 0;

		for(int i=0;i<tfidfa.size();i++){
			cosU = cosU +(aList.get(i).getValue()*bList.get(i).getValue()); 
			cosLa = cosLa+Math.pow(aList.get(i).getValue(), 2);
			cosLb = cosLb+Math.pow(bList.get(i).getValue(), 2);
		}
		cosL = Math.sqrt(cosLa+cosLb);
		cos = (double)cosU/cosL;
		System.out.println(cos);
		return cos;
	}
	
}
