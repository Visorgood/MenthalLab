package menthallab.waffletests;

import menthallab.wafflelib.*;

public class CrossValidation
{
	public static CrossValidationResult Execute(final Dataset dataset, final Classifier classifier)
	{
		Dataset tempDataset = new Dataset(dataset);
		
		tempDataset.shuffle();
		
		final int numberOfPartitions = 10;
		final int partitionSize = tempDataset.size() / numberOfPartitions;
		
		double meanClassificationError = 0.0;
		int meanLearningTime = 0;
		int meanClassificationTime = 0;
		
		for (int i = 0; i < numberOfPartitions; ++i)
		{
			// Generate test set
			Dataset testPartition = new Dataset();
			for (int j = i * partitionSize; j < (i + 1) * partitionSize; ++j)
			{
				Instance instance = tempDataset.getInstance(j);
				String label = tempDataset.getLabel(j);
				testPartition.addInstance(instance, label, true);
			}
			
			// Generate training set
			Dataset trainPartition = new Dataset();
			for (int j = 0; j < numberOfPartitions * partitionSize; ++j)
			{
				if (j < i * partitionSize || j >= (i + 1) * partitionSize)
				{
					Instance instance = tempDataset.getInstance(j);
					String label = tempDataset.getLabel(j);
					trainPartition.addInstance(instance, label, true);
				}
			}
			
			// Learn classifier on the training set
			classifier.learn(trainPartition);
			
			// Test classifier on the test set and compute classification error
			double error = 0.0;
			for (int j = 0; j < testPartition.size(); ++j)
			{
				String desiredLabel = testPartition.getLabel(j);
				String classificationLabel = classifier.classify(testPartition.getInstance(j));
				error += (desiredLabel.equals(classificationLabel) ? 0.0 : 1.0);
			}
			meanClassificationError += (error / testPartition.size());
		}
		meanClassificationError /= numberOfPartitions;

		return new CrossValidationResult(meanClassificationError, meanError, meanError);
	}
}

class CrossValidationResult
{
	public final double MeanClassificationError;
	public final int MeanLearningTime;
	public final int MeanClassificationTime;
	
	public CrossValidationResult(double meanClassificationError, int meanLearningTime, int meanClassificationTime)
	{
		this.MeanClassificationError = meanClassificationError;
		this.MeanLearningTime = meanLearningTime;
		this.MeanClassificationTime = meanClassificationTime;
	}
}