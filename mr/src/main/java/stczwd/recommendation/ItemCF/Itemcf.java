package stczwd.recommendation.ItemCF;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.JDBCDataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

//import org.apache.mahout.common.RandomUtils;

public class Itemcf {

	public static void main(String[] args) throws IOException, TasteException {
		// TODO Auto-generated method stub
		RecommenderIntro Item_reco = new RecommenderIntro();
		//记录当前时间
		long currentTime=System.currentTimeMillis();
		// Item_reco.run_user();
		Item_reco.run_item();
		//计算程序运行时间
		//Date date=new Date((System.currentTimeMillis()-currentTime));
		System.out.println((System.currentTimeMillis()-currentTime));
	}

}

class RecommenderIntro {

	public void run_item() throws IOException, TasteException {
		//File-based DataModel - FileDataModel 
		//DataModel model = new FileDataModel(new File("data/intro.csv"));

		 //Database-based DataModel - MySQLJDBCDataModel 
		MysqlDataSource dataSource = new MysqlDataSource(); 
		dataSource.setServerName("123.57.223.22"); 
		dataSource.setUser("root"); 
		dataSource.setPassword("12345");
		dataSource.setDatabaseName("LogisticRecommendation");
		dataSource.setAutoReconnect(true);
//		dataSource.setConnectTimeout(31536000);
		JDBCDataModel datamodel = new MySQLJDBCDataModel(dataSource, "UserItemPref", 
		"userID", "itemID", "Pref",null);
		ItemSimilarity similarity = new UncenteredCosineSimilarity(datamodel);
		Recommender recommender = new GenericItemBasedRecommender(datamodel,
				similarity);
		List<RecommendedItem> recommendations = recommender.recommend(1, 3);
		for (RecommendedItem recommendation : recommendations) {
			System.out.println("The itemCF recommendation is " + recommendation);
		}
	}

	public void run_user() throws IOException, TasteException {
		DataModel model = new FileDataModel(new File("data/intro.csv"));
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(100,
				similarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model,
				neighborhood, similarity);
		List<RecommendedItem> recommendations = recommender.recommend(1, 3);
		for (RecommendedItem recommendation : recommendations) {
			System.out
					.println("The userCF recommendation is " + recommendation);
		}
	}

	public void run_user_elevator() throws IOException, TasteException {
		// 通过File构造函数，将路径转换为File对象
		//DataModel datamodel = new FileDataModel(new File("data/intro.csv"));

		 //Database-based DataModel - MySQLJDBCDataModel 
		MysqlDataSource dataSource = new MysqlDataSource(); 
		dataSource.setServerName("123.57.223.22"); 
		dataSource.setUser("root"); 
		dataSource.setPassword("12345"); 
		JDBCDataModel datamodel = new MySQLJDBCDataModel(dataSource, "mysqlmodetest", 
		"userID", "itemID", "prefvalue",null);
		
		RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {

				UserSimilarity similarity = new PearsonCorrelationSimilarity(
						model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(2,
						similarity, model);

				Recommender recommender = new GenericUserBasedRecommender(
						model, neighborhood, similarity);

				List<RecommendedItem> recommendations = recommender.recommend(
						1, 1);

				for (RecommendedItem recommendation : recommendations) {
					System.out.println("The userCF recommendation is :"
							+ recommendation);
				}
				return recommender;
			}
		};

		this.CF_evaluate(recommenderBuilder, datamodel);
	}

	public void CF_evaluate(final RecommenderBuilder recommenderBuilder,
			DataModel model) throws TasteException {
		// RandomUtils.useTestSeed();

		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

		IRStatistics stats = evaluator.evaluate(recommenderBuilder, null,
				model, null, 2,
				GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		System.out.println(stats.getPrecision());
		System.out.println(stats.getRecall());
	}
}