package com.alibaba;

import java.util.Arrays;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 20:54
 */
public class KmeansData {
    public double[][] data;//原始矩阵
    public int length;//矩阵长度
    public int dim;//特征维度
    public int[] labels;//数据所属类别的标签，即聚类中心的索引值
    public double[][] centers;//聚类中心矩阵
    public int[] centerCounts;//每个聚类中心的元素个数
    public double[][] originalCenters;//最初的聚类中心坐标点集

    public KmeansData(double[][] data, int length, int dim) {
        this.data = data;
        this.length = length;
        this.dim = dim;
    }

    @Override
    public String toString() {
        return "KmeansData{" +
                "data=" + Arrays.toString(data) +
                ", length=" + length +
                ", dim=" + dim +
                ", labels=" + Arrays.toString(labels) +
                ", centers=" + Arrays.toString(centers) +
                ", centerCounts=" + Arrays.toString(centerCounts) +
                ", originalCenters=" + Arrays.toString(originalCenters) +
                '}';
    }
}