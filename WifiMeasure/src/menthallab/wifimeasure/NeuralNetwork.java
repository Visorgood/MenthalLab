package menthallab.wifimeasure;

import java.util.*;

import org.neuroph.core.learning.*;
import org.neuroph.nnet.*;
import org.neuroph.util.*;

public class NeuralNetwork
{
	private MultiLayerPerceptron mlPerceptron;
	private List<String> attributes;
	private List<String> labels;
	
	public NeuralNetwork()
	{
		this.mlPerceptron = null;
	}
	
	public void learn(Dataset dataset)
	{
		TrainingSet<SupervisedTrainingElement> trainSet = convertToNeuroph(dataset);
		final int inputUnits = trainSet.getInputSize();
		final int outputUnits = trainSet.getOutputSize();
		final int hiddenUnits = (inputUnits + outputUnits) / 2;
		this.mlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputUnits, hiddenUnits, outputUnits);
		this.mlPerceptron.learn(trainSet);
	}
	
	public String classify(Instance instance)
	{
		double[] input = new double[this.attributes.size()];
		for (int i = 0; i < this.attributes.size(); ++i)
		{
			String attributeName = this.attributes.get(i);
			Double value = instance.get(attributeName);
			input[i] = (null != value ? value : 0);
		}
		double[] output = new double[this.labels.size()];
		SupervisedTrainingElement testInstance = new SupervisedTrainingElement(input, output);
		this.mlPerceptron.setInput(testInstance.getInput());
		this.mlPerceptron.calculate();
		return getLabel(this.mlPerceptron.getOutput());
	}

	private TrainingSet<SupervisedTrainingElement> convertToNeuroph(Dataset dataset)
	{
		this.attributes = new ArrayList<String>(dataset.getAttributes());
		this.labels = new ArrayList<String>(dataset.getDifferentLabels());
		
		final int inputs = this.attributes.size();
		final int outputs = this.labels.size();		
		TrainingSet<SupervisedTrainingElement> trainSet = new TrainingSet<SupervisedTrainingElement>(inputs, outputs);
		
		for (int i = 0; i < dataset.size(); ++i)
		{
			final RawInstance rawInstance = dataset.getRawInstance(i);
			double[] input = new double[inputs];
			for (int j = 0; j < rawInstance.size(); ++j)
				input[j] = rawInstance.get(j);
			final String label = dataset.getLabel(i);
			double[] output = new double[outputs];
			output[this.labels.indexOf(label)] = 1;
			trainSet.addElement(new SupervisedTrainingElement(input, output));
		}
		return trainSet;
	}
	
	private String getLabel(double[] output)
	{
		double maxOutput = 0.0;
		int index = 0;
		for (int i = 0; i < output.length; ++i)
			if (output[i] > maxOutput)
			{
				maxOutput = output[i];
				index = i;
			}
		return this.labels.get(index);
	}
}