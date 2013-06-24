package menthallab.wifimeasure;

import java.util.*;

public class RawInstance
{
	private final List<Double> values;
	
	public RawInstance()
	{
		this.values = new ArrayList<Double>();
	}
	
	public void add(double value)
	{
		this.values.add(value);
	}
	
	public double get(int index)
	{
		if (index < 0 || index >= this.values.size())
			throw new IndexOutOfBoundsException("RawInstance.get");
		return this.values.get(index);
	}
	
	public List<Double> getValues()
	{
		return Collections.unmodifiableList(this.values);
	}
	
	public int size()
	{
		return this.values.size();
	}
}