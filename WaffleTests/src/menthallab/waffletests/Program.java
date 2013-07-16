package menthallab.waffletests;

import menthallab.wafflelib.*;

public class Program
{
	public static void main(String[] args)
	{
		try
		{
			final String trainFilePath = "dataset.csv";
			final Dataset dataset = DatasetManager.loadFromFile(trainFilePath);
			
			final Classifier classifier = new NeuralNetwork();
			//final Classifier classifier = new kNN();
			
			final CrossValidationResult result = CrossValidation.Execute(dataset, classifier);
			
			System.out.print("Mean classification error: ");
			System.out.println(result.MeanClassificationError);
			System.out.print("Mean learning time: ");
			System.out.println(result.MeanLearningTime);
			System.out.print("Mean classification time: ");
			System.out.println(result.MeanClassificationTime);
		}
		catch (Exception exc)
		{
			System.out.println("Exception!");
			System.out.println(exc);
		}
	}
}