package menthallab.waffletests;

import java.util.Date;

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
			Date startTime = new Date();
			classifier.learn(trainPartition);
			Date endTime = new Date();
			meanLearningTime += (endTime.getTime() - startTime.getTime());
			
			// Test classifier on the test set and compute classification error
			double classificationError = 0.0;
			for (int j = 0; j < testPartition.size(); ++j)
			{
				String desiredLabel = testPartition.getLabel(j);
				Instance instance = testPartition.getInstance(j);
				startTime = new Date();
				String classificationLabel = classifier.classify(instance);
				endTime = new Date();
				meanClassificationTime += (endTime.getTime() - startTime.getTime());
				classificationError += (desiredLabel.equals(classificationLabel) ? 0.0 : 1.0);
			}
			meanClassificationError += (classificationError / testPartition.size());
		}
		meanClassificationError /= numberOfPartitions;
		meanLearningTime /= numberOfPartitions;
		meanClassificationTime /= (numberOfPartitions * partitionSize);

		return new CrossValidationResult(meanClassificationError, meanLearningTime, meanClassificationTime);
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