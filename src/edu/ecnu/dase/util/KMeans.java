package edu.ecnu.dase.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class KMeans {
	/**
	 * kmeans 算法
	 * @param filePath 存放待分类点的文件路径
	 * @param resulttemp 待生成摘要的文档
	 * @return clustermap 返回聚类结果
	 */
	public Map<Integer, Integer> Kmeans(String filePath, int[] resulttemp) {
		Instances ins = null;
		SimpleKMeans KM = null;
		File file = new File(filePath);
		ArffLoader loader = new ArffLoader();
		Map<Integer, Integer> clustermap = new HashMap<Integer, Integer>();
			
		try {
			loader.setFile(file);

			ins = loader.getDataSet();

			KM = new SimpleKMeans();
			KM.setNumClusters(4);
			KM.buildClusterer(ins);// 开始进行聚类
			// System.out.println(KM.preserveInstancesOrderTipText());
			// System.out.println(KM.toString());//打印聚类结果
			// Instances centroids = KM.getClusterCentroids();
			// for (int i = 0; i < centroids.numInstances(); i++) {
			// System.out.println( "Centroid " + i + ": " +
			// centroids.instance(i));
			// }
			for (int i = 0; i < ins.numInstances(); i++) {
				// System.out.println( ins.instance(i) + " is in cluster " +
				// KM.clusterInstance(ins.instance(i)));
				clustermap.put(resulttemp[i], KM.clusterInstance(ins.instance(i))+1);
				System.out.println(KM.clusterInstance(ins.instance(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clustermap;
	}
}
