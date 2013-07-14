import org.neuroph.core.learning.*;
import org.neuroph.nnet.*;
import org.neuroph.nnet.learning.*;
import org.neuroph.util.*;

public class NeuralNetwork
{
	private MultiLayerPerceptron mlPerceptron;
	
	public NeuralNetwork()
	{
		this.mlPerceptron = null;
	}
	
	public void learn(TrainingSet<SupervisedTrainingElement> trainSet, double maxError, double learningRate)
	{
		final TransferFunctionType transferFunction = TransferFunctionType.SIGMOID;
		
		final int inputUnits = trainSet.getInputSize();
		final int outputUnits = trainSet.getOutputSize();
		final int hiddenUnits = (inputUnits + outputUnits) / 2;
		this.mlPerceptron = new MultiLayerPerceptron(transferFunction, inputUnits, hiddenUnits, outputUnits);
		this.mlPerceptron.randomizeWeights();
		BackPropagation backPropagation = new BackPropagation();
		backPropagation.setMaxError(maxError);
		backPropagation.setLearningRate(learningRate);
		//this.mlPerceptron.learn(trainSet, backPropagation);
		this.mlPerceptron.learnInNewThread(trainSet, backPropagation);
		for (int i = 0; !backPropagation.isStopped(); ++i)
		{
			System.out.println(i);
			try
			{
				Thread.sleep(100);
			}
			catch (Exception exc)
			{
			
			}
		}
	}
	
	public double[] classify(SupervisedTrainingElement testInstance)
	{
		this.mlPerceptron.setInput(testInstance.getInput());
		this.mlPerceptron.calculate();
		return this.mlPerceptron.getOutput();
	}
}

//public class NeuralNetwork
//{
//	private MultiLayerPerceptron mlPerceptron;
//	
//	public NeuralNetwork()
//	{
//		this.mlPerceptron = null;
//	}
//	
//	public void learn(DataSet trainSet)
//	{
//		final int inputUnits = trainSet.getInputSize();
//		final int outputUnits = trainSet.getOutputSize();
//		final int hiddenUnits = (inputUnits + outputUnits) / 2;
//		mlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputUnits, hiddenUnits, outputUnits);
//		mlPerceptron.learn(trainSet);
//	}
//	
//	public double[] classify(DataSetRow dataSetRow)
//	{
//		mlPerceptron.setInput(dataSetRow.getInput());
//		mlPerceptron.calculate();
//		return mlPerceptron.getOutput();
//	}
//}