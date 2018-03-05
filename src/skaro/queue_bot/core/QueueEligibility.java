package skaro.queue_bot.core;

public enum QueueEligibility 
{
	
	ELIGIBLE(null, true),
	
	ELIGIBLE_PRIORITY("added to the queue with priority", true),
	ELIGIBLE_NO_PRIORITY("added to the end of the queue", true),
	
	CLOSED("denied: the queue is closed", false),
	SUB_ONLY("denied: subscriber only mode is enabled", false),
	FULL("denied: the queue is full", false),
	NO_REENTRY("denied: reentry into the queue is not allowed", false),
	IN_QUEUE("denied: you are already in the queue", false),
	CURRENT("denied: you are the current queue request", false),
	;
	
	private String message;
	private boolean eligible;
	
	QueueEligibility(String message, boolean allowed)
	{
		this.message = message;
		this.eligible = allowed;
	}
	
	@Override
	public String toString() { return message; }
	public boolean isEligible() { return eligible; }
}
