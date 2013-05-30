package menthallab.wifimeasure;

import java.util.*;

public class kNN
{
	private static int DEFAULT_K = 5;
	
	private final Dataset dataset;
	private final int k;
	
	public kNN(Dataset dataset, int k)
	{
		this.dataset = dataset;
		this.k = (k > 0 ? k : DEFAULT_K);
	}
	
	public String classify(Instance queryInstance)
	{
		InstanceInfo[] kNearestInstances = findKNearestInstances(queryInstance);
		String classificationLabel = computeLabel(kNearestInstances);
		return classificationLabel;
	}
	
	// Compute distances from query instance to all instances in data set,
	// and return set of k nearest instance infos
	private InstanceInfo[] findKNearestInstances(Instance queryInstance)
	{
		SortedByDistanceList orderedInstances = new SortedByDistanceList(this.k);
		List<String> datasetAttributes = this.dataset.getAttributes();
		for (int i = 0; i < this.dataset.size(); ++i)
		{
			Instance instance = this.dataset.getInstance(i);
			int sum = 0;
			for (String attributeName : datasetAttributes)
			{
				int value = instance.get(attributeName);
				Integer queryValue = queryInstance.get(attributeName);
				queryValue = (null != queryValue ? queryValue : 0);
				sum += Math.pow(value - queryValue, 2);
			}
			double distance = Math.sqrt(sum);
			double weight = 1.0 / sum;
			String label = this.dataset.getLabel(i);
			orderedInstances.add(new InstanceInfo(distance, weight, label));
		}
		return orderedInstances.toArray();
	}

	// Compute label by weighted voting of k nearest instances
	private String computeLabel(InstanceInfo[] kNearestInstances)
	{
		String classificationLabel = null;
		double maxSum = 0;
		// Iterate by possible labels and search label, which brings maximum voting value 
		for (String label : this.dataset.getDifferentLabels())
		{
			double sum = 0;
			for (InstanceInfo instanceInfo : kNearestInstances)
				if (instanceInfo.label.equals(label))
					sum += instanceInfo.weight;
			if (sum > maxSum)
			{
				maxSum = sum;
				classificationLabel = label;
			}
		}
		return classificationLabel;
	}
}

class InstanceInfo
{
	public final double distance;
	public final double weight;
	public final String label;
	
	public InstanceInfo(double distance, double weight, String label)
	{
		this.distance = distance;
		this.weight = weight;
		this.label = label;
	}
}

class SortedByDistanceList
{
	private final int k;
	private final LinkedList<InstanceInfo> instanceInfos;

	public SortedByDistanceList(int k)
	{
		this.k = k;
		this.instanceInfos = new LinkedList<InstanceInfo>();
	}
	
	public boolean add(InstanceInfo instanceInfo)
	{
		boolean success = false;
		
		for (int i = 0; i < this.instanceInfos.size() && !success; ++i)
			if (instanceInfo.distance < this.instanceInfos.get(i).distance)
			{
				this.instanceInfos.add(i, instanceInfo);
				success = true;
			}
		if (!success && this.instanceInfos.size() < this.k)
		{
			this.instanceInfos.add(instanceInfo);
			success = true;
		}
		if (this.instanceInfos.size() > this.k)
			this.instanceInfos.removeLast();
		
		return success;
	}
	
	public InstanceInfo[] toArray()
	{
		InstanceInfo[] instanceInfoArray = new InstanceInfo[this.instanceInfos.size()];
		this.instanceInfos.toArray(instanceInfoArray);
		return instanceInfoArray;
	}
}