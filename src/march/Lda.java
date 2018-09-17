package march;

public class Lda {
	public static class modelparameters {
		public float alpha = 50/6; //usual value is 50 / K
		public float beta = 0.01f;//usual value is 0.1
		public int topicNum = 100;
		public int iteration = 100;
		public int saveStep = 10;
		public int beginSaveIters = 50;
	}
}
