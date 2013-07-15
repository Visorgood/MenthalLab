import java.util.*;

public class Program
{
	public static void main(String[] args)
	{
		try
		{
			String trainFilePath = "dataset.csv";
			Dataset dataset = DatasetManager.loadFromFile(trainFilePath);
			
			dataset.shuffle();
			
			final int partitionsNumber = 10;
			final int partitionSize = dataset.size() / partitionsNumber;
			
			double meanError = 0.0;
			for (int i = 0; i < partitionsNumber; ++i)
			{
				Dataset testPartition = new Dataset();
				for (int j = i * partitionSize; j < (i + 1) * partitionSize; ++j)
				{
					Instance instance = dataset.getInstance(j);
					String label = dataset.getLabel(j);
					testPartition.addInstance(instance, label, true);
				}
				
				Dataset trainPartition = new Dataset();
				for (int j = 0; j < partitionsNumber * partitionSize; ++j)
				{
					if (j < i * partitionSize || j >= (i + 1) * partitionSize)
					{
						Instance instance = dataset.getInstance(j);
						String label = dataset.getLabel(j);
						trainPartition.addInstance(instance, label, true);
					}
				}
				
				kNN knn = new kNN(trainPartition, 9);
				
				double error = 0.0;
				for (int j = 0; j < testPartition.size(); ++j)
				{
					String desiredLabel = testPartition.getLabel(j);
					String classificationLabel = knn.classify(testPartition.getInstance(j));
					error += (desiredLabel.equals(classificationLabel) ? 0.0 : 1.0);
				}
				error /= testPartition.size();
				//System.out.println(error);
				meanError += error;
			}
			meanError /= partitionsNumber;
			System.out.println(meanError);
		}
		catch (Exception exc)
		{
			
		}
	}
}