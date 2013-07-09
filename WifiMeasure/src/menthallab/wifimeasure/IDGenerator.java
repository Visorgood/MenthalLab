package menthallab.wifimeasure;

public class IDGenerator
{
	private static int id = 1;
	
	public static void init(int initValue)
	{
		id = initValue;
	}
	
	public static int getNextId()
	{
		return id++;
	}
	
	public static void reset()
	{
		id = 1;
	}
}