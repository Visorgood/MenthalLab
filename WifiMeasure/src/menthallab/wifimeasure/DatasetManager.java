package menthallab.wifimeasure;

import java.io.*;
import java.util.*;

public class DatasetManager
{
	public static Dataset loadFromFile(String filePath) throws IOException
	{
		Dataset dataset = new Dataset();
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		
		// Read line with attribute names and add attributes to data set
		String attributesLine = reader.readLine();
		String[] attributeNames = attributesLine.split(",");
		for (String attributeName : attributeNames)
			dataset.addAttribute(attributeName);
		
		// Read instances
		String instanceLine = null;
		while (null != (instanceLine = reader.readLine()))
		{
			String[] values = instanceLine.split(",");
			Instance instance = new Instance();
			for (int i = 0; i < attributeNames.length; ++i)
			{
				int value = Integer.parseInt(values[i]);
				instance.add(attributeNames[i], value);
			}
			String label = values[values.length - 1];
			dataset.addInstance(instance, label, false);
		}
		
		reader.close();
		
		return dataset;
	}
	
	public static void saveToFile(Dataset dataset, String filePath) throws IOException
	{
	    FileOutputStream fos = new FileOutputStream(filePath);
	    PrintStream printStream = new PrintStream(fos);
	    StringBuilder sb = new StringBuilder();
	    
	    // Write line with attribute names
	    for (String attributeName : dataset.getAttributes())
	    	sb.append(attributeName + ",");
	    sb.delete(sb.length() - 1, sb.length());
	    printStream.println(sb.toString());
	    
	    // Write instances
	    for (int i = 0; i < dataset.size(); ++i)
	    {
	    	sb.delete(0, sb.length());
	    	List<Integer> values = dataset.getRawInstance(i);
	    	for (int value : values)
	    	{
	    		sb.append(value);
	    		sb.append(",");
	    	}
	    	String label = dataset.getLabel(i);
	    	sb.append(label);
	    	printStream.println(sb.toString());
	    }
	    
	    printStream.close();
	}
}