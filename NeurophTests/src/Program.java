import org.neuroph.core.learning.*;

public class Program
{
	public static void main(String[] args)
	{		
		final int inputs = 21;
		final int outputs = 4;
		final double maxError = 0.01;
		final double learningRate = 0.01;
		
		TrainingSet<SupervisedTrainingElement> trainSet = TrainingSet.createFromFile("train.txt", inputs, outputs, ",");
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		neuralNetwork.learn(trainSet, maxError, learningRate);
		
		TrainingSet<SupervisedTrainingElement> testSet = TrainingSet.createFromFile("test.txt", inputs, outputs, ",");
		for (SupervisedTrainingElement testInstance : testSet.elements())
		{
			double[] output = neuralNetwork.classify(testInstance);
			for (double value : output)
			{
				System.out.print((int)(100 * value) / 100.0);
				System.out.print(", ");
			}
			System.out.println();
		}
	}
	
//	public static void main(String[] args)
//	{
//		DataSet trainSet = DataSet.createFromFile("train.txt", 8, 3, ",");
//		NeuralNetwork neuralNetwork = new NeuralNetwork();
//		neuralNetwork.learn(trainSet);
//		
//		DataSet testSet = DataSet.createFromFile("test.txt", 8, 3, ",");
//		for (DataSetRow dataSetRow : testSet.getRows())
//		{
//			double[] output = neuralNetwork.classify(dataSetRow);
//			for (double value : output)
//			{
//				System.out.print((int)(1000 * value) / 1000.0);
//				System.out.print(", ");
//			}
//			System.out.println();
//		}
//	}
}