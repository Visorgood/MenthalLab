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
	
	public void learn(Dataset dataset)
	{
		DataSet trainSet = convertToNeuroph(dataset);
		final int inputUnits = trainSet.getInputSize();
		final int outputUnits = trainSet.getOutputSize();
		final int hiddenUnits = (inputUnits + outputUnits) / 2;
		mlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputUnits, hiddenUnits, outputUnits);
		mlPerceptron.learn(trainSet);
		mlPerceptron.getInputNeurons()[0].getLabel();
	}
	
	public String classify(Instance instance)
	{
		double[] input = new double[instance.size()];
		for (int i = 0; i < instance.size(); ++i)
			input[i] = instance.get("");
		double[] output = new double[mlPerceptron.getOutputsCount()];
		DataSetRow dataSetRow = new DataSetRow(input, output);
		mlPerceptron.setInput(dataSetRow.getInput());
		mlPerceptron.calculate();
		output = mlPerceptron.getOutput();
		return "";
	}

	private DataSet convertToNeuroph(Dataset dataset)
	{
		final int inputs = dataset.getAttributes().size();
		final int outputs = dataset.getDifferentLabels().size();
		DataSet dataSet = new DataSet(inputs, outputs);
		for (int i = 0; i < dataset.size(); ++i)
		{
			final RawInstance rawInstance = dataset.getRawInstance(i);
			double[] input = new double[inputs];
			for (int j = 0; j < rawInstance.size(); ++j)
				input[j] = rawInstance.get(j);
			final String label = dataset.getLabel(i);
			double[] output = new double[outputs];
			output[dataset.getDifferentLabels().indexOf(label)] = 1;
			dataSet.addRow(input, output);
		}
		return dataSet;
	}
}