package jan_13;

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
			cal[0][j] = count;
			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Organic Inorganic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[1][j] = count;

			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("Semi-Metallic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[2][j] = count;
			
			count =  0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("carbon")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[3][j] = count;
			
			count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("metallic")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[4][j] = count;
					
			count = 0;
			for(int i = 0;i<newMap.size();i++){
				if(infoIds.get(i).getKey().contains("polymer")&&infoIds.get(i).getValue()==j){
					count++;
				}
			}
			cal[5][j] = count;
		}
		
		double[][] t = new double[6][6];
		for (int i = 0; i < cal.length; i++) {
			int m = 0;
			for (int j = 0; j < cal.length; j++) {
				m = m+cal[i][j];
			}
			for (int j = 0; j < cal.length; j++) {
				t[i][j] = (double)cal[i][j]/m;
			}
		}
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < t.length; j++) {
				System.out.print(t[i][j]+" ");
			}
			System.out.println();
		}
	}

}
