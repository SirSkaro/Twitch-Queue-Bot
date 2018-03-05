package skaro.queue_bot.twitch_tools;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import skaro.queue_bot.core.SessionState;

public class DropCommand extends FXQueueableCommand
{
	private SessionState state;
	
	public DropCommand(char prefix, SessionState state)
	{
		this.state = state;
		
		if(prefix != '!')
        {
	        setRequiresCommandTrigger(false);
	        setCommand(prefix + "drop");
        }
        else
        {
        	setRequiresCommandTrigger(true);
	        setCommand("drop");
        }
		
		setCommandAliases(new String[]{"quit", "remove"});
		setCategory("request");
		setDescription("Ask to be removed from the queue");
		getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample(prefix+"drop");
	}

	@Override
	protected void createExecution(ChannelMessageEvent event) 
	{
		execution = new Runnable() 
        {
            @Override
            public void run() 
            {
            	String displayName = event.getUser().getName();
            	boolean didRemove = state.removeFromQueue(displayName);

            	//Build response
            	StringBuilder response = new StringBuilder();
            	response.append("@"+displayName +" ");
            	response.append(didRemove ? "successfully removed from the queue" : "you aren't even in the queue!");
            	sendMessageToChannel(event.getChannel().getName(), response.toString());
            }
        };
	}
}
