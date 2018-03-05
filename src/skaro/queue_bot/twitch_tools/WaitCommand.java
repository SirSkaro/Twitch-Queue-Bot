package skaro.queue_bot.twitch_tools;

import java.util.Optional;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import skaro.queue_bot.core.SessionState;

public class WaitCommand extends FXQueueableCommand 
{
	private SessionState state;
	private char prefix;
	
	public WaitCommand(char prefix, SessionState state)
	{
		this.state = state;
		this.prefix = prefix;
		
		if(prefix != '!')
        {
	        setRequiresCommandTrigger(false);
	        setCommand(prefix + "wait");
        }
        else
        {
        	setRequiresCommandTrigger(true);
	        setCommand("wait");
        }
		
		setCommandAliases(new String[]{"place", "when"});
		setCategory("user info");
		setDescription("Ask how long your wait is");
		getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample(prefix+"wait");
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
                Optional<Integer> userIndex = state.placeInQueue(displayName);
                
                ///Build response
                StringBuilder response = new StringBuilder();
                response.append("@"+displayName +" ");
                response.append(userIndex.isPresent() ? 
                		"you are current number "+userIndex.get() + " in the queue" 
                		: " you're not in the queue! Join with the \""+prefix+"queue\" command!");
                
                sendMessageToChannel(event.getChannel().getName(), response.toString());
            }
        };
	}
}
