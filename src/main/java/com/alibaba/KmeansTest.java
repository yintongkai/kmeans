package com.alibaba;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 21:58
 */
public class KmeansTest {
    public static void main(String[] args) {
        double [][]da = new double[6][];
        da[0] = new double[]{1,5,132};
        da[1] = new double[]{3,7,12};
        da[2] = new double[]{67,23,45};
        da[3] = new double[]{34,5,13};
        da[4] = new double[]{12,7,21};
        da[5] = new double[]{26,23,54};
        Kmeans kmeans = new Kmeans(da);
        KmeansParam param = new KmeansParam();
        kmeans.doKmeans(3,param);
    }
    public void test() {
    }
}