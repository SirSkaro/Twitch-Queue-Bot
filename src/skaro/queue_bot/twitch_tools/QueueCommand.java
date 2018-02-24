package skaro.queue_bot.twitch_tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.philippheuer.twitch4j.events.event.irc.ChannelMessageEvent;
import me.philippheuer.twitch4j.message.commands.Command;
import me.philippheuer.twitch4j.message.commands.CommandPermission;
import skaro.queue_bot.core.QueueEntry;
import skaro.queue_bot.core.SessionState;

public class QueueCommand extends Command 
{
    private SessionState state;
    private TwitchService service;
    private ArgumentsCase argCase;
    
    public QueueCommand(char prefix, SessionState state, TwitchService service) 
    {
        super();

        this.state = state;
        this.service = service;
        this.argCase = getArgsCase();
        
        if(prefix != '!')
        {
	        setRequiresCommandTrigger(false);
	        setCommand(prefix + "queue");
        }
        else
        {
        	setRequiresCommandTrigger(true);
	        setCommand("queue");
        }
        
        setCommandAliases(new String[]{"join", "battle"});
        setCategory("request");
        setDescription("Request to join the queue!");
        getRequiredPermissions().add(CommandPermission.EVERYONE);
        setUsageExample(constructExample(prefix));
    }

    @Override
    public void executeCommand(ChannelMessageEvent event) 
    {
        super.executeCommand(event);
        String arguments = this.getParsedContent().trim();
        String entrantName = event.getUser().getName();
        String channelName = event.getChannel().getName();
        boolean isSub = service.isSubscriber(event.getUser());
        
        if(!isValidInput(arguments))
        {
        	sendMessageToChannel(channelName, "@"+event.getUser().getName()+" invalid input. Usage: "+this.getUsageExample());
        	return;
        }
        
        List<String> argList = inputToList(arguments);
        QueueEntry entry = new QueueEntry(entrantName, isSub, argList.get(0), argList.get(1), argList.get(2));
        String response = state.addToQueue(entry);
        response = "@"+event.getUser().getName() +" "+ response;
        
        // Send Response
        sendMessageToChannel(channelName, response);
        
        for(String s : argList)
        	System.out.println(s);
        System.out.println("is subscriber: " +isSub);
    }
    
    private boolean isValidInput(String input)
    {
    	//Check if all required or optional arguments are present
		Matcher m = argCase.regexPatern.matcher(input);
		return m.matches();
    }
    
    private List<String> inputToList(String input)
    {
    	String[] args = input.split(",", argCase.maxArgs);
    	List<String> result = new ArrayList<String>();
    	
    	switch(argCase)
    	{
			case NO_ARGS:
				result.add(null); result.add(null); result.add(null);
				break;
			case NO_ARGS_WITH_COMMENT:
				result.add(null); result.add(null); result.add(args[0].trim());
				break;
			case ONE_ARG:
				result.add(args[0].trim()); result.add(null); result.add(null);
				break;
			case ONE_ARG_WITH_COMMENT:
				result.add(args[0].trim()); result.add(null);
				result.add(args.length > 1 ? args[1].trim() : null);
				break;
			case TWO_ARGS:
				result.add(args[0].trim()); result.add(args[1].trim()); result.add(null);
				break;
			case TWO_ARGS_WITH_COMMENT:
				result.add(args[0].trim()); result.add(args[1].trim()); 
				result.add(args.length > 2 ? args[2].trim() : null);
				break;
    	}
    	
    	return result;
    }
    
    private String constructExample(char prefix)
    {
    	StringBuilder builder = new StringBuilder();
    	builder.append(prefix + "queue ");
    	
    	
    	if(state.requiresKWArg1())
    		builder.append(state.getKWArgKey1().get());
    	
    	if(state.requiresKWArg2())
    		builder.append(", "+ state.getKWArgKey2().get());
    	
    	if(state.isCommentAllowed())
    		if(state.requiresKWArg1())
    			builder.append(", <optional comment>");
    		else
    			builder.append("<optional comment>");
    		
    	return builder.toString();
    }
    
    private ArgumentsCase getArgsCase()
    {
    	if(state.requiresKWArg1() && state.requiresKWArg2()) //check if both arguments are required
		{
			if(state.isCommentAllowed())
				return ArgumentsCase.TWO_ARGS_WITH_COMMENT;
			else
				return ArgumentsCase.TWO_ARGS;
		}
		else if(state.requiresKWArg1())						//check if one argument is required
		{
			if(state.isCommentAllowed())
				return ArgumentsCase.ONE_ARG_WITH_COMMENT;
			else
				return ArgumentsCase.ONE_ARG;
		}
		else												//check if no arguments are required
		{
			if(state.isCommentAllowed())
				return ArgumentsCase.NO_ARGS_WITH_COMMENT;
			else
				return ArgumentsCase.NO_ARGS;
		}
    }
    
    private enum ArgumentsCase
    {
    	NO_ARGS("",0),
    	NO_ARGS_WITH_COMMENT("(.*)?",1),
    	ONE_ARG("[^,]+",1),
    	ONE_ARG_WITH_COMMENT("[^,]+(,.+)?",2),
    	TWO_ARGS("[^,]+,[^,]+",2),
    	TWO_ARGS_WITH_COMMENT("[^,]+,[^,]+(,.+)?",3);
    	
    	public Pattern regexPatern;
    	public int maxArgs;
    	
    	ArgumentsCase(String regex, int max)
    	{
    		this.regexPatern = Pattern.compile(regex);
    		this.maxArgs = max;
    	}
    }
}
