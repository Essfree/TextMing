package march_24;

import java.io.IOException;
import java.util.Map;

//三月改程序系列
public class Main {
	
	public static void main(String[] args) throws IOException {
		
		//文本分词		
//		SplitText st = new SplitText();
//		String dataDir = "E:\\MUC\\三月\\3.23-3.29\\data";
//		st.split(dataDir);
		
		//计算tfidf
		String splitDir = "E:\\MUC\\三月\\3.23-3.29\\clean_data";
		Tfidf tI = new Tfidf();
		Map<String, Map<String, Double>> tfidfMap = tI.CalTfIdf(splitDir);
		
		//kmeans聚类
//		int classNum = 6;
//		String desDir = "E:\\MUC\\textmining\\Result\\Result.txt";
////		String RocDir = "E:\\MUC\\1.28\\Result\\Roc.txt";
//		new KmeansPlus(tfidfMap,classNum,desDir);
//		Analysis as = new Analysis();
//		as.analyData(desDir,classNum);
	}

}
