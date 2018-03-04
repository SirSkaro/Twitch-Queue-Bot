package skaro.queue_bot.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("rawtypes")
public class OrderedPriorityQueue implements Queue
{
	private Queue<QueueEntry> priorityEntries;
	private Queue<QueueEntry> nonPriorityEntries;
	
	public OrderedPriorityQueue() 
	{	
		super();
		priorityEntries = new LinkedList<QueueEntry>();
		nonPriorityEntries = new LinkedList<QueueEntry>();
	}
	
	@Override
	public boolean addAll(Collection c) 
	{
		for(Object o : c)
		{
			if(!(o instanceof QueueEntry))
				throw new IllegalArgumentException();
			
			QueueEntry entry = (QueueEntry)o;
			if(entry.isSub())
				priorityEntries.add(entry);
			else
				nonPriorityEntries.add(entry);
		}
		
		return true;
	}

	@Override
	public void clear() 
	{
		priorityEntries.clear();
		nonPriorityEntries.clear();
	}

	@Override
	public boolean contains(Object arg0) 
	{
		throw new UnsupportedOperationException("contains not supported");
	}

	@Override
	public boolean containsAll(Collection arg0) 
	{
		throw new UnsupportedOperationException("containsAll not supported");
	}

	@Override
	public boolean isEmpty() 
	{
		return priorityEntries.isEmpty() && nonPriorityEntries.isEmpty();
	}

	@Override
	public Iterator iterator() 
	{
		Queue fullQueue = consolodateQueues();
		return fullQueue.iterator();
	}

	@Override
	public boolean remove(Object o)
	{
		return priorityEntries.remove(o) || nonPriorityEntries.remove(o);
	}

	@Override
	public boolean removeAll(Collection arg0) 
	{
		throw new UnsupportedOperationException("removeAll not supported");
	}

	@Override
	public boolean retainAll(Collection arg0) 
	{
		throw new UnsupportedOperationException("retainAll not supported");
	}

	@Override
	public int size() 
	{
		return priorityEntries.size() + nonPriorityEntries.size();
	}

	@Override
	public Object[] toArray() 
	{
		Queue fullQueue = consolodateQueues();
		return fullQueue.toArray();
	}

	@Override
	public Object[] toArray(Object[] arg0) 
	{
		Queue<QueueEntry> fullQueue = consolodateQueues();
		return fullQueue.toArray(arg0);
	}

	@Override
	public boolean add(Object o)
	{
		if(!(o instanceof QueueEntry))
			throw new IllegalArgumentException();
		
		QueueEntry entry = (QueueEntry)o;
		
		if(entry.isSub())
			priorityEntries.add(entry);
		else
			nonPriorityEntries.add(entry);
			
		return true;
	}

	@Override
	public Object element() 
	{
		throw new UnsupportedOperationException("element not supported");
	}

	@Override
	public boolean offer(Object arg0) 
	{
		throw new UnsupportedOperationException("offer not supported");
	}

	@Override
	public Object peek() 
	{
		QueueEntry result = priorityEntries.peek();
		
		if(result == null)
			result = nonPriorityEntries.peek();
		
		return result;
	}

	@Override
	public Object poll() 
	{
		QueueEntry result = priorityEntries.poll();
		
		if(result == null)
			result = nonPriorityEntries.poll();
		
		return result;
	}

	@Override
	public Object remove() 
	{
		throw new UnsupportedOperationException("remove not supported");
	}
	
	private Queue<QueueEntry> consolodateQueues()
	{
		Queue<QueueEntry> fullQueue = new LinkedList<QueueEntry>();
		fullQueue.addAll(priorityEntries);
		fullQueue.addAll(nonPriorityEntries);
		
		return fullQueue;
	}
}
