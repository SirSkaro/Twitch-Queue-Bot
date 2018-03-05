package skaro.queue_bot.core;

import java.util.Date;
import java.util.Optional;

/**
 * A POJO to represent an entry in the queue. Includes data members representing the 
 * user who made the request for joining the queue and key word arguments.
 * 
 * @author Benjamin Churchill
 *
 */
public class QueueEntry
{
	private final String requester;
	private final boolean isSub;
	private final Optional<String> kwarg1, kwarg2, comment;
	private final Date creationTime;
	
	public QueueEntry(String requester, boolean isSub, String arg1, String arg2, String comment)
	{	
		this.requester = requester;
		this.isSub = isSub;
		this.kwarg1 = Optional.ofNullable(arg1);
		this.kwarg2 = Optional.ofNullable(arg2);
		this.comment = Optional.ofNullable(comment);
		
		this.creationTime = new Date();
	}
	
	/**
	 * A constructor to make a bare instance. Meant for temporary instances used for the {@link QueueEntry#equals(Object)} method.
	 * @param requester
	 */
	public QueueEntry(String requester)
	{
		this.requester = requester;
		this.isSub = false;
		this.kwarg1 = Optional.empty();
		this.kwarg2 = Optional.empty();
		this.comment = Optional.empty();
		
		this.creationTime = null;
	}
	
	/********* Getters *********/
	public String getRequester( ) { return this.requester; }
	public boolean isSub() { return this.isSub; }
	public Optional<String> getKWArg1() { return this.kwarg1; }
	public Optional<String> getKWArg2() { return this.kwarg2; }
	public Optional<String> getComment() { return this.comment; }
	public Date getCreationTime() { return this.creationTime; }
	
	
	/********* Public Methods *********/
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof QueueEntry))
			return false;
		
		QueueEntry obj = QueueEntry.class.cast(o);
		
		return this.requester.equals(obj.requester);	//Assumes all Twitch user names are unique (which is currently true)
	}
	
	@Override
	public int hashCode()
	{
		return requester.hashCode();
	}
	
	@Override
	public String toString()
	{
		return requester;
	}
}
