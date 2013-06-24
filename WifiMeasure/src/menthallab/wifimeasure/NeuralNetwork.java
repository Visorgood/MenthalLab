package menthallab.wifimeasure;

import org.neuroph.core.learning.*;
import org.neuroph.nnet.*;
import org.neuroph.util.*;

public class NeuralNetwork
{
	private MultiLayerPerceptron mlPerceptron;
	
	public NeuralNetwork()
	{
		this.mlPerceptron = null;
	}
	
	public void learn(DataSet trainSet)
	{		
		final int inputUnits = trainSet.getInputSize();
		final int outputUnits = trainSet.getOutputSize();
		final int hiddenUnits = (inputUnits + outputUnits) / 2;
		mlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputUnits, hiddenUnits, outputUnits);
		mlPerceptron.learn(trainSet);
	}
	
	public double[] classify(DataSetRow dataSetRow)
	{
		mlPerceptron.setInput(dataSetRow.getInput());
		mlPerceptron.calculate();
		return mlPerceptron.getOutput();
	}
}