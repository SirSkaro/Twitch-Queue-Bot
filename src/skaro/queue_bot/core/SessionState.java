package skaro.queue_bot.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

/**
 * A class the track the state of a queuing session.
 * Contains all data to track and manage of the state of the queue.
 * 
 * From the user's perspective, there is only one queue of requests. However,
 * the user can choose is subscribers (subs) have priority in the queue. This option
 * is toggle-able. The order of first-come-first-serve (FCFS) and the order of FCFS with
 * priority should both be preserved.
 * @author Benjamin Churchill
 *
 */
@SuppressWarnings("unchecked")
public class SessionState 
{
	private Queue<QueueEntry> requestQueue;
	private OrderedPriorityQueue requestPriorityQueue;
	
	private Object queueKey;		//A key used in the synchronized block for either queue
	private Optional<QueueEntry> currEntry;
	private ObservableList<QueueEntry> fulfilledRequests, queueList;	//History and current queue order, respectively
	private ObservableList<String> notifications;			//List of notifications
	private ObservableList<Series<String, Number>> barChartData;	//data container for bar chart
	private Data<String, Number> subData, nonSubData;		//data points for bar chart
	private DoubleProperty progressPercent;		//For progress bar
	private StringProperty progressPercentFraction;
	
	private AtomicBoolean subOnly, allowReentry, queueClosed, subPriority;
	private AtomicInteger queueCap;	//Cap is -1 if there is no limit to the queue size
	
	private Optional<String> kwargKey1, kwargKey2;
	private boolean commentAllowed;
	
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	public SessionState()
	{
		requestQueue = new LinkedList<QueueEntry>();
		requestPriorityQueue = new OrderedPriorityQueue();
		queueKey = new Object();	
		currEntry = Optional.empty();
		fulfilledRequests = FXCollections.observableArrayList();
		queueList = FXCollections.observableArrayList();
		notifications = FXCollections.observableArrayList();
		subData = new Data<String, Number>("Subscriber", 0);
		nonSubData = new Data<String, Number>("Non-Subscriber", 0);
		progressPercent = new SimpleDoubleProperty(0);
		progressPercentFraction = new SimpleStringProperty("0/0");
		
		barChartData = FXCollections.observableArrayList();
		Series<String, Number> barSeries = new Series<String, Number>();
		barSeries.getData().add(subData); barSeries.getData().add(nonSubData);
		barChartData.add(barSeries);
		
		subOnly = new AtomicBoolean();
		allowReentry = new AtomicBoolean();
		queueClosed = new AtomicBoolean(true);
		subPriority = new AtomicBoolean();
		queueCap = new AtomicInteger(-1);
		
		kwargKey1 = Optional.empty();
		kwargKey2 = Optional.empty();
		commentAllowed = false;
	}
	
	public void configure(ConfigurationState config)
	{		
		setSubOnly(config.isSubOnly());
		setAllowReentry(config.isReentry());
		setSubPriority(config.isSubPriority());
		setQueueCap(config.getQueueCap());
		
		kwargKey1 = config.getKwarg1();
		kwargKey2 = config.getKwarg2();
		commentAllowed = config.usesCommentKWArg();
	}
	
	/********* Getters *********/
	public Optional<QueueEntry> getCurrEntry() { return currEntry; }
	public Integer getQueueCap() { return queueCap.get(); }
	public Boolean isSubOnly() { return subOnly.get(); }
	public Boolean reentryAllowed() { return allowReentry.get(); }
	public Boolean isQueueClosed() { return queueClosed.get(); }
	public Boolean subsHavePriority() { return subPriority.get(); }
	public ObservableList<QueueEntry> getQueueAsList() { return queueList; }
	public ObservableList<QueueEntry> getHistoryAsList() { return fulfilledRequests; }
	public ObservableList<String> getNotificationsList() { return notifications; }
	public ObservableList<Series<String, Number>> getBarChartAsList() { return barChartData; }
	public DoubleProperty getProgressPercent() { return progressPercent; }
	public StringProperty getProgressPercentFraction() { return progressPercentFraction; }
	public Optional<String> getKWArgKey1() { return kwargKey1; }
	public Optional<String> getKWArgKey2() { return kwargKey2; }
	public boolean isCommentAllowed() { return commentAllowed; }
	
	/********* Setters *********/
	public void setQueueCap(int i)
	{ 
		queueCap.set(i);
		createNotification("The queue cap was set to "+ (i == -1 ? "∞" : i )); 
	}
	
	public void setAllowReentry(boolean b)
	{
		allowReentry.set(b); 
		createNotification("Viewers "+ (b ? "may " : "may not ") + "re-enter the queue");
	}
	public void setQueueClosed(boolean b) 
	{
		queueClosed.set(b);
		createNotification("The queue has been "+(b ? "closed" : "open"));
	}
	
	public void setSubOnly(boolean b)
	{ 
		subOnly.set(b);
		
		//If Sub-Only mode is enabled, remove all non-subs from the queues
		if(b)
		{
			synchronized(queueKey)
			{
				Iterator<QueueEntry> itr = queueList.iterator();
				while(itr.hasNext())
				{
					QueueEntry qe = itr.next();
					if(!qe.isSub())
					{
						itr.remove();
						requestPriorityQueue.remove(qe);
						requestQueue.remove(qe);
					}
				}
				
				updateProgressPercent();
			}
		}
		
		createNotification("The queue has been " + (b ? "closed to non-subs" : "opened to all veiwers"));
	}
	
	public void setSubPriority(boolean b) 
	{ 
		subPriority.set(b); 
		createNotification((b ? "Subscribers have " : "Noone has ")+ "queuing priority");
		
		synchronized(queueKey)
		{
			queueList.clear();
			
			if(b)
				queueList.addAll(requestPriorityQueue);
			else
				queueList.addAll(requestQueue);
		}
		
		createNotification("Queue order has been sorted according to priority");
	}
	
	
	/********* Public Methods *********/
	public boolean requiresKWArg1() { return kwargKey1.isPresent(); }
	public boolean requiresKWArg2() { return kwargKey2.isPresent(); }
	
	public void progressQueue()
	{
		Queue<QueueEntry> toPoll, toSync;
		QueueEntry polled, removed;
		
		//Poll from the queues
		synchronized(queueKey)
		{
			//Add the current entry to the fulfilled request list
			//Update progress data
			if(currEntry.isPresent())
			{
				fulfilledRequests.add(0, currEntry.get());
				if(currEntry.get().isSub())
					subData.setYValue(subData.getYValue().intValue() + 1);
				else
					nonSubData.setYValue(nonSubData.getYValue().intValue() + 1);
				
				createNotification(currEntry.get() + " has been added to History");
				currEntry = Optional.empty();
				updateProgressPercent();
			}
			
			if(subPriority.get())
			{
				toPoll = requestPriorityQueue;
				toSync = requestQueue;
			}
			else
			{
				toPoll = requestQueue;
				toSync = requestPriorityQueue;
			}
			
			polled = toPoll.poll();
			toSync.remove(polled);
			currEntry = Optional.ofNullable(polled);
			
			if(!queueList.isEmpty())
			{
				removed = queueList.remove(0);
				createNotification(removed + " is the current queue request");
			}
		}
	}
	
	public String addToQueue(QueueEntry qe)
	{
		if(queueClosed.get())
		{
			createNotification(qe + ": denied queue request. Queue closed");
			return "denied: the queue is closed";
		}
		
		synchronized(queueKey)
		{
			if(eligibleForQueuing(qe))
			{	
				//Add the request to the queue at the appropriate index
				requestQueue.add(qe);
				requestPriorityQueue.add(qe);
				queueList.add(indexOfEntry(qe), qe);
				
				updateProgressPercent();
				createNotification(qe + ": queue request accepted");
				return (subPriority.get() && qe.isSub() ? "queued with priority" : "added to the end of the queue");
			}
			
			createNotification(qe + ": denied queue request");
			return "queue request rejected";
		}
	}
	
	public Integer getQueueSize()
	{
		synchronized(queueKey)
		{
			return queueList.size();
		}
	}
	
	public void clearQueue()
	{
		int numCleared;
		synchronized(queueKey)
		{
			numCleared = queueList.size();
			requestPriorityQueue.clear();
			requestQueue.clear();
			queueList.clear();
			currEntry = Optional.empty();
			updateProgressPercent();
			
			createNotification("Queue cleared ("+numCleared+" requests)");
		}
	}
	
	public Integer timesInQueue(QueueEntry qe)
	{
		int occurences = 0;
		
		for(QueueEntry entry : fulfilledRequests)
			if(qe.equals(entry))
				occurences++;
		
		return occurences;
	}
	
	/********* Private Methods *********/
	/**
	 * A method to test if a request is eligible for queuing. This method is expected to
	 * be used inside of a synchronized block, so atomistic behavior is assumed.
	 * @param qe - the QueueEntry request attempting to be queued
	 * @return True if the request is eligible for queuing. False otherwise.
	 */
	private boolean eligibleForQueuing(QueueEntry qe)
	{
		int cap = queueCap.get();
		boolean isCurrEntry = (currEntry.isPresent() && currEntry.get().equals(qe));
		
		return (cap == -1 || requestQueue.size() < cap)		//Assert that the queue is below its capped size
					&& (!queueList.contains(qe) && !isCurrEntry)	//Assert the QueueEntry isn't in the queue or the current entry
					&& (allowReentry.get() ? true : !fulfilledRequests.contains(qe))	//Assert that the QueueEntry isn't a re-entry if they are not allowed
					&& (subOnly.get() ? qe.isSub() : true);	//Assert that the QueueEntry requester is a sub if Sub-Only mode is enabled
	}
	
	private void createNotification(String event)
	{	
		notifications.add(0, event + "\t\t["+LocalDateTime.now().format(formatter)+"]");
	}
	
	/**
	 * A method to update the progress percentage.
	 * The current queue entry (currEntry) is considered an unfulfilled request
	 * This method is expected to be used inside of a synchronized block, so atomistic behavior is assumed.
	 */
	private void updateProgressPercent()
	{
		double plusOne = (currEntry.isPresent() ? 1.0 : 0.0 );	//Consider the current entry an unfulfilled request
		double numerator = fulfilledRequests.size();
		double denominator = (fulfilledRequests.size() + queueList.size() + plusOne);
		
		if(denominator == 0)
			progressPercent.set(1.0);
		
		progressPercent.set(numerator/denominator);
		progressPercentFraction.set(String.valueOf((int)numerator)+"/"+String.valueOf((int)denominator));
	}
	
	private int indexOfEntry(QueueEntry qe)
	{
		Iterator<QueueEntry> itr;
		int currIndex = -1;
		
		if(subPriority.get())
			itr = requestPriorityQueue.iterator();
		else
			itr = requestQueue.iterator();
		
		while(itr.hasNext())
		{
			QueueEntry entry = itr.next();
			currIndex++;
			
			if(qe.equals(entry))
				return currIndex;
		}
		
		return -1;
	}
}
