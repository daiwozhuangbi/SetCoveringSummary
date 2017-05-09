package edu.ecnu.dase.summarization;

 

public class RandomNumofRows {
	 /**
		 * 随机指定范围内N个不重复的数
		 * 最简单最基本的方法
		 * @param min 指定范围最小值
		 * @param max 指定范围最大值
		 * @param n 随机数个数
		 * @return 返回随机生成数的结果
		 */
		public static int[] randomCommon(int min, int max, int n){
			if (n > (max - min + 1) || max < min) {
	            return null;
	        }
			int[] result = new int[n];
			int count = 0;
			while(count < n) {
				int num = (int) (Math.random() * (max - min)) + min;
				boolean flag = true;
				for (int j = 0; j < n; j++) {
					if(num == result[j]){
						flag = false;
						break;
					}
				}
				if(flag){
					result[count] = num;
					count++;
				}
			}
			return result;
		}
//		/**
//		 * 随机生成抽取的语料库行数
//		 * @param filePath 语料库路径 
//		 * @param numofdocs 随机抽取的文件数量
//		 * @return resulttemp 随机生成行数的结果
//		 */
//		@SuppressWarnings("resource")
//		public int[] getRow0fContent(String filePath,int numofdocs){
//			HSSFWorkbook wb;
//			int[] resulttemp = new int[numofdocs];
//			try {
//				wb = new HSSFWorkbook(new FileInputStream(filePath));
//			    for (int l = 0; l < wb.getNumberOfSheets(); l++) {// l张表格
//				//从所有语料库的随机抽取GET_NUM_DOCS行,将行数存入resulttemp，-1因为getRow()从0开始
//				resulttemp=randomCommon(1,wb.getSheetAt(l).getLastRowNum()-1,numofdocs);
//			} 		
//			}catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return resulttemp;
//		}
}
