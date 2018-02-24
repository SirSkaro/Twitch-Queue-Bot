package skaro.queue_bot.core;

import java.util.Comparator;

public class QueueEntryComparator implements Comparator<QueueEntry> 
{
	@Override
	public int compare(QueueEntry arg0, QueueEntry arg1) 
	{
		if(arg0.isSub() && arg1.isSub())	//Equal priority
			return 0;
		
		else if(!arg0.isSub() && arg1.isSub())	//First argument has less priority (arg0 is not a sub, arg1 is a sub)
			return 1;
		
		return -1;	//First argument has more priority (arg0 is a sub, arg1 is not a sub)
	}

}
