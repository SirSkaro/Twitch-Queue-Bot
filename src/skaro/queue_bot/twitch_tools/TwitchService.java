package skaro.queue_bot.twitch_tools;

import java.util.Optional;

import me.philippheuer.twitch4j.TwitchClient;
import me.philippheuer.twitch4j.TwitchClientBuilder;
import me.philippheuer.twitch4j.auth.model.OAuthCredential;
import me.philippheuer.twitch4j.endpoints.ChannelEndpoint;
import me.philippheuer.twitch4j.enums.TwitchScopes;
import me.philippheuer.twitch4j.events.EventSubscriber;
import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.model.User;
import me.philippheuer.twitch4j.model.UserSubscriptionCheck;
import skaro.queue_bot.core.ConfigurationState;
import skaro.queue_bot.core.SessionState;

public class TwitchService 
{
	private static TwitchService instance;
	private static TwitchClient twitchClient;
	private static ChannelEndpoint channelEndpoint;

	private TwitchService(ConfigurationState config, SessionState state)
	{
		twitchClient = TwitchClientBuilder.init()
				.withClientId(config.getClientID().get())
				.withClientSecret(config.getClientSecret().get())
				.withAutoSaveConfiguration(false)
				.withCredential(config.getOauth().get())
				.connect();
		
		//Get channel data
		channelEndpoint = twitchClient.getChannelEndpoint(config.getChannel().get());
		
		//Connect to chat and register commands/listeners
		registerCommands(config, state);
		
		//Check if application has permission to check for subscribers
		Optional<OAuthCredential> credentialCheck = twitchClient.getCredentialManager().getTwitchCredentialsForChannel(channelEndpoint.getChannelId());
		if(!credentialCheck.isPresent())
			twitchClient.getCredentialManager().getOAuthTwitch().requestPermissionsFor("CHANNEL", TwitchScopes.CHANNEL_CHECK_SUBSCRIPTION);
	}
	
	public static void initialize(ConfigurationState config, SessionState state)
	{
		instance = new TwitchService(config, state);
	}
	
	public static Optional<TwitchService> getInstance()
	{
		return Optional.ofNullable(instance);
	}
	
	public boolean isSubscriber(User user)
	{
		Optional<UserSubscriptionCheck> check;
		check = twitchClient.getUserEndpoint().getUserSubcriptionCheck(user.getId(), channelEndpoint.getChannelId());
		
		return check.isPresent();
	}
	
	public void sendMessage(String msg)
	{
		twitchClient.getMessageInterface().sendMessage(channelEndpoint.getChannel().getName(), msg);
	}
	
	@SuppressWarnings("deprecation")
	public static void shutdown()
	{
		if(twitchClient != null)
			twitchClient.disconnect();
		else
		{
		    for (Thread t : Thread.getAllStackTraces().keySet()) 
		        if (t.getName().equals("ReadingThread")) 
		        	t.stop();
		}
			
		instance = null;
	}
	
	/**
     * Subscribe to the ChannelMessage Event and write the output to the console
     */
    @EventSubscriber
    public void onChannelMessage(ChannelMessageEvent event) 
    {
        System.out.println("Channel [" +event.getChannel().getDisplayName() + "] - User[" + event.getUser().getDisplayName() + "] - Message [" + event.getMessage() + "]");
    }
    
    private void registerCommands(ConfigurationState config, SessionState state)
    {
    	QueueCommand queueCommand = new QueueCommand(config.getPrefix(), state, this);
		WaitCommand waitCommand = new WaitCommand(config.getPrefix(), state); 
		DropCommand dropCommand = new DropCommand(config.getPrefix(), state);
		
		twitchClient.getCommandHandler().registerCommand(queueCommand);
		twitchClient.getCommandHandler().registerCommand(waitCommand);
		twitchClient.getCommandHandler().registerCommand(dropCommand);
		twitchClient.getDispatcher().registerListener(this);
		channelEndpoint.registerEventListener();
    }
}
