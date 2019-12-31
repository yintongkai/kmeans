package com.alibaba.optimization;

/**
 * @program: kmeans
 * @description: none
 * @author: tongkai yin
 * @create: 2019/12/30 23:08
 * train方法有两种，一个不需要传递K值，算法内部自动处理为最优值，此为最细粒度聚类，另一个需要传递K值，k值大小任意，当k值>算法内部最优值时，自动调整 为最优值
 * 利用model预测时，只需传递feature标识
 */
public class KmeansTest {
    public static void main(String[] args) throws Exception {
        Kmeans kmeans = new Kmeans();
        String path = "E:\\demo\\kmeans\\src\\main\\resources\\Kmeans.txt";
        ClusterModel model = kmeans.train(path);
        model.centers();
        System.out.println("中国属于第" + (model.predict("中国") + 1) + "类");
        model.outputAllResult();
        System.out.println("-------------------------------------------------------------------------------------");
        model = kmeans.train(path, 100000);
        model.centers();
        System.out.println("中国属于第" + (model.predict("中国") + 1) + "类");
        model.outputAllResult();
    }
}