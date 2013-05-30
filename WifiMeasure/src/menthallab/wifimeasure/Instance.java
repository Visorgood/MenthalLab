package menthallab.wifimeasure;

import java.util.*;

public class Instance
{
	private final Map<String, Integer> values;
	
	public Instance()
	{
		this.values = new HashMap<String, Integer>();
	}
	
	public void add(String attributeName, int value)
	{
		this.values.put(attributeName, value);
	}
	
	public Integer get(String attributeName)
	{
		return this.values.get(attributeName);
	}
	
	public Set<String> getAttributes()
	{
		return Collections.unmodifiableSet(this.values.keySet());
	}
	
	public int size()
	{
		return this.values.size();
	}
}