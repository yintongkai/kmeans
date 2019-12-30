package com.alibaba.optimization;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 22:56
 */
public class KmeansParam {
    public static final int K = 240;//系统默认的最大聚类中心个数
    public static final int MAX_ATTEMPTS = 4000;//最大迭代次数
    public static final double MIN_CRITERIA = 0.1;
    public static final double MIN_EuclideanDistance = 0.8;
    public double criteria = MIN_CRITERIA; //最小阈值
    public int attempts = MAX_ATTEMPTS;
    public boolean isDisplay = true;
    public double min_euclideanDistance = MIN_EuclideanDistance;
}