import java.util.*;

import org.neuroph.core.learning.*;

public class Program
{
	public static void main(String[] args)
	{		
		final int inputs = 21;
		final int outputs = 4;
		final double maxError = 0.01;
		final double learningRate = 0.01;
		final int partitionsNumber = 10;
		
		TrainingSet<SupervisedTrainingElement> trainSet = TrainingSet.createFromFile("train.txt", inputs, outputs, ",");
		
		final int partitionSize = trainSet.size() / partitionsNumber;
		
		trainSet.shuffle();
		
		List<TrainingSet<SupervisedTrainingElement>> partitions = 
				new ArrayList<TrainingSet<SupervisedTrainingElement>>();
		
		for (int i = 0; i < partitionSize * partitionsNumber; i += partitionSize)
		{
			TrainingSet<SupervisedTrainingElement> partition = new TrainingSet<SupervisedTrainingElement>();
			for (int j = i; j < i + partitionSize; j++)
			{
				partition.addElement((SupervisedTrainingElement)trainSet.elementAt(j));
			}
			partitions.add(partition);
		}
		
		NeuralNetwork neuralNetwork = new NeuralNetwork();
		double meanError = 0.0;
		for (int i = 0; i < partitionsNumber; ++i) 
		{
			TrainingSet<SupervisedTrainingElement> trainPartitions = new TrainingSet<SupervisedTrainingElement>();
			TrainingSet<SupervisedTrainingElement> testPartition = new TrainingSet<SupervisedTrainingElement>();
			for (int n = 0; n < partitionsNumber; n++)
			{
				if (n != i)
					for (int m = 0; m < partitions.get(n).size(); m++)
						trainPartitions.addElement((SupervisedTrainingElement)partitions.get(n).elementAt(m));
			}
			testPartition = partitions.get(i);
			
			// neuralNetwork.learn doesn't work without such thing:
			trainPartitions.saveAsTxt("temp.txt", ",");
			trainPartitions = TrainingSet.createFromFile("temp.txt", inputs, outputs, ",");
			
			neuralNetwork.learn(trainPartitions, maxError, learningRate);
			
			double error = 0;
			for (SupervisedTrainingElement testInstance : testPartition.elements())
			{
				double[] desiredOutput = testInstance.getDesiredOutput();
				int indexOfMaxElement1 = getIndexOfMaxElement(desiredOutput);
				
				double[] output = neuralNetwork.classify(testInstance);
				int indexOfMaxElement2 = getIndexOfMaxElement(output);
				
				error += (indexOfMaxElement1 == indexOfMaxElement2 ? 0.0 : 1.0);
				
				//for (double value : output)
				//{
				//	System.out.print((int)(100 * value) / 100.0);
				//	System.out.print(", ");
				//}
				//System.out.println();
			}
			error /= testPartition.size();
			meanError += error;
			System.out.println("Classification error: " + error);
		}
		meanError /= partitionsNumber;
		System.out.println("Mean classification error: " + meanError);
		
		/*trainSet.saveAsTxt("output.txt", ",");
		
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
		}*/
	}
	
	private static int getIndexOfMaxElement(double[] array)
	{
		double max = array[0];
		int maxIndex = 0;
		for (int i = 1; i < array.length; ++i)
		{
			if (array[i] > max)
			{
				max = array[i];
				maxIndex = i;
			}
		}
		return maxIndex;
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