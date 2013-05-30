package menthallab.wifimeasure;

import java.util.*;

public class Dataset
{
	private final List<String> attributes;
	private final List<List<Integer>> instances;
	private final List<String> labels;
	private final List<String> differentLabels;
	
	public Dataset()
	{
		this.attributes = new ArrayList<String>();
		this.instances = new ArrayList<List<Integer>>();
		this.labels = new ArrayList<String>();
		this.differentLabels = new ArrayList<String>();
	}
	
	public boolean addAttribute(String attributeName)
	{
		if (this.attributes.contains(attributeName))
			return false;
		this.attributes.add(attributeName);
		for (List<Integer> instance : this.instances)
			instance.add(0);
		return true;
	}
	
	public void addInstance(Instance instance, String label, boolean addNewAttributes)
	{
		if (instance.size() == 0)
			return;
		if (addNewAttributes)
			for (String attributeName : instance.getAttributes())
				this.addAttribute(attributeName);
		boolean mustBeAdded = false;
		List<Integer> values = new ArrayList<Integer>();
		for (String attributeName : this.attributes)
		{
			Integer value = instance.get(attributeName);
			if (null != value)
			{
				values.add(value);
				mustBeAdded = true;
			}
			else
				values.add(0);
				
		}
		if (mustBeAdded)
		{
			this.instances.add(values);
			this.labels.add(label);
			if (!this.differentLabels.contains(label))
				this.differentLabels.add(label);
		}
	}
	
	public Instance getInstance(int index)
	{
		if (index < 0 || index >= this.instances.size())
			return null;
		List<Integer> values = this.instances.get(index);
		Instance instance = new Instance();
		for (int i = 0; i < this.attributes.size(); ++i)
			instance.add(this.attributes.get(i), values.get(i));
		return instance;
	}
	
	public List<Integer> getRawInstance(int index)
	{
		if (index < 0 || index >= this.instances.size())
			return null;
		return Collections.unmodifiableList(this.instances.get(index));
	}
	
	public String getLabel(int index)
	{
		if (index < 0 || index >= this.labels.size())
			return null;
		return this.labels.get(index);
	}
	
	public List<String> getAttributes()
	{
		return Collections.unmodifiableList(this.attributes);
	}
	
	public List<String> getDifferentLabels()
	{
		return Collections.unmodifiableList(this.differentLabels);
	}
	
	public int size()
	{
		return this.instances.size();
	}
}