package jan_17;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Analysis {

	public void analyData(String analy) throws IOException {
		Map<String, Integer> newMap = new HashMap<String, Integer>();
		File filein = new File(analy);
		InputStreamReader in = new InputStreamReader(new FileInputStream(filein));
		BufferedReader br = new BufferedReader(in);
		String temp =br.readLine();
		StringBuffer sb = null;
		while(temp!=null){
			String[] te = temp.split("txt ");
			newMap.put(te[0]+"txt",Integer.parseInt(te[1]));
			temp = br.readLine();
		}
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(newMap.entrySet());
		int cal[][] = new int[6][6];
		
		for(int j=0;j<6;j++){
			int count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Ceramic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][0] = count;
			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Organic Inorganic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][1] = count;

			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Semi-Metallic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][2] = count;
			
			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("carbon")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][3] = count;
			
			count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("metallic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][4] = count;
					
			count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("polymer")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[j][5] = count;
		}
		for (int i = 0; i < cal.length; i++) {
			for (int j = 0; j < cal.length; j++) {
				System.out.print(cal[i][j]+" ");
			}
			System.out.println();
		}
		
//		int amount = 0;
//		for(int i = 0;i<cal.length;i++){
//			for(int j=0;j<cal.length;j++){
//				System.out.print(cal[i][j]+" ");
//				amount = amount+cal[i][j];
//			}
//			System.out.println();
//		}
//		System.out.println(amount);
//		
//		int count = 0;
//		for(int i = 0;i<cal.length;i++){
//			for(int j=0;j<cal.length;j++){
//				if(i==j){
//					count+=cal[i][j];
//				}
//			}
//		}
//		System.out.println((double)count/amount);
		
		double[][] t = new double[6][6];
		for (int i = 0; i < cal.length; i++) {
			int m = 0;
			for (int j = 0; j < cal.length; j++) {
				m = m+cal[i][j];
			}
			for (int j = 0; j < cal.length; j++) {
				t[i][j] = (double)cal[i][j]/m;
//				t[i][j] = m;
				}
		}
		System.out.println("按熵来算");
		double[] e = new double[6];        
		for(int i=0;i<t.length;i++){
			for (int j = 0; j < t.length; j++) {
				e[i] =e[i]+ (-(t[i][j]*((double)Math.log(t[i][j])/Math.log(2))));
			}
			System.out.println(e[i]);
		}
//		double eToAll = 0;
//		int allMem = 0;
//		for(int i = 0;i<cal.length;i++){
//			for (int j = 0; j < cal.length; j++) {
//				allMem = allMem+cal[i][j];
//			}
//		}
//		for (int i = 0; i < e.length; i++) {
//			int m = 0;
//			for (int j = 0; j < cal.length; j++) {
//				m = m+cal[i][j];
//			}
//			eToAll += (double)m/allMem*e[i];
//		}

//		System.out.println(eToAll);
//		按纯度
		System.out.println("按纯度来算");
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t.length; j++) {
				System.out.print(t[i][j]+" ");
			}
			System.out.println();
		}
		
		
		
	}
}
