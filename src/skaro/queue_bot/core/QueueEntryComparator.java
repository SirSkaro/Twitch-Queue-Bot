package skaro.queue_bot.core;

import java.util.Comparator;

public class QueueEntryComparator implements Comparator<QueueEntry> 
{
	@Override
	/*
	 * If two entries have the same priority, return 0 (which should never happen)
	 * If entry1 has more priority than entry2, return -1
	 * If entry2 has more priority than entry1, return 1
	 * 
	 * If both entries have the same subscription status, then the entry with the smallest timestamp gets priority.
	 * Assumes that no two entries can have the same time stamp.
	 */
	public int compare(QueueEntry entry1, QueueEntry entry2) 
	{
		if(entry1.isSub() == entry2.isSub())	//Equal subscriber priority
		{
			if(entry1.getCreationTime() == entry2.getCreationTime())
				System.out.println("Wow");
			
			if(entry1.getCreationTime() < entry2.getCreationTime())
				return -1;
			return 1;
		}
		
		else if(entry1.isSub() && !entry2.isSub())
			return -1;
		
		return 1;	
	}

}
