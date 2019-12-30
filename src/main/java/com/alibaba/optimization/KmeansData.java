package com.alibaba.optimization;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 22:55
 */
public class KmeansData {
    public double[][] data;
    public int length;
    public int dim;
    public double[][] centers;

    public KmeansData(double[][] data, int length, int dim) {
        this.data = data;
        this.length = length;
        this.dim = dim;
    }
}