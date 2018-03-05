package skaro.queue_bot.twitch_tools;

import javafx.application.Platform;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;

/**
 * An abstract class to wrap me.philippheuer.twitch4j.message.commands.Command.
 * The point of this class is to automate executing commands on a non-JavaFX Application thread.
 * Any command that could alter children of the the current javafx.stage.Stage must be ran via the  
 * javafx.application.Platform instance in order to not throw an exception.
 * @author Benjamin Churchill
 *
 */
public abstract class FXQueueableCommand extends Command
{
	protected Runnable execution;
	
	protected abstract void createExecution(ChannelMessageEvent event);
	
	@Override
    public void executeCommand(ChannelMessageEvent event) 
    {
        super.executeCommand(event);
        createExecution(event);
        queueCommandExecution();
    }
	
	protected void queueCommandExecution()
	{
		if(execution == null)
			throw new IllegalStateException("Runnable is null");
		
		Platform.runLater(execution);
	}
}
