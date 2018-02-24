package skaro.queue_bot.core;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConfigurationState 
{
	//Main Configuration members
	private Optional<String> kwarg1, kwarg2, oauth, channel, clientID, clientSecret;
	private boolean commentKWArg;
	private Character prefix;
	private ObservableList<String> twitchView;
	private boolean reentry, subOnly, subPriority;
	private int queueCap;
	private Optional<File> configFile;
	
	//Observable list elements
	private final String comment = "[comment]";
	
	//Other Utility members
	private Character[] legalPrefixes = new Character[] {'!', '#', '%', '+', '.'};
	private Integer[] legalCaps = new Integer[] {-1, 5, 10, 15, 20, 25, 50};
	
	public ConfigurationState(Character defaultPrefix)
	{
		//Set default  values
		oauth = Optional.empty();
		channel = Optional.empty();
		kwarg1 = Optional.empty();
		kwarg2 = Optional.empty();
		clientID = Optional.empty();
		clientSecret = Optional.empty();
		configFile = Optional.empty();
		commentKWArg = false;
		prefix = defaultPrefix;
		twitchView = FXCollections.observableArrayList();
		reentry = false;
		subOnly = false;
		subPriority = false;
		queueCap = -1;
		
		//Populate observable lists
		twitchView.add("sub_viewer: "+prefix+"queue");
		twitchView.add("[bot name]: @sub_viewer queued!");
		twitchView.add("not_sub_viewer: "+prefix+"queue");
		twitchView.add("[bot name]: @not_sub_viewer queued!");
	}
	
	/********* Getters *********/
	public boolean isOauthFilled() { return oauth.isPresent(); }
	public Optional<String> getKwarg1() { return kwarg1; }
	public Optional<String> getKwarg2() { return kwarg2; }
	public Optional<String> getClientID() { return clientID; }
	public Optional<String> getClientSecret() { return clientSecret; }
	public Optional<String> getOauth() { return oauth; }
	public Optional<String> getChannel() { return channel; }
	public boolean usesCommentKWArg() { return commentKWArg; }
	public Character getPrefix() { return prefix; }
	public ObservableList<String> getTwitchView() { return twitchView; }
	public boolean isReentry() { return reentry; }
	public boolean isSubOnly() { return subOnly; }
	public boolean isSubPriority() { return subPriority; }
	public int getQueueCap() { return queueCap; }
	
	/********* Setters *********/
	public void settwitchView(ObservableList<String> twitchView) { this.twitchView = twitchView; }
	public void setReentry(boolean reentry) { this.reentry = reentry; }
	public void setQueueCap(int queueCap) { this.queueCap = queueCap; }
	
	
	public void setConfigFile(File configFile)
	{
		if(configFile == null || !configFile.isFile())
			this.configFile = Optional.empty();
		else
			this.configFile = Optional.of(configFile);
	}
	
	public void setCommentKWArg(boolean commentKWArg)
	{ 
		this.commentKWArg = commentKWArg; 
		updateManualView();
	}
	
	public void setPrefix(Character prefix)
	{ 
		this.prefix = prefix;
		updateManualView();
	}
	
	public void setSubOnly(boolean subOnly) 
	{ 
		this.subOnly = subOnly; 
		updateManualView();
	}
	
	public void setSubPriority(boolean subPriority)
	{ 
		this.subPriority = subPriority; 
		updateManualView();
	}
	
	public void setOauth(String oauth)
	{
		if(oauth == null || oauth.isEmpty())
			this.oauth = Optional.empty();
		else
			this.oauth = Optional.of(oauth);
	}
	
	public void setChannel(String channel)
	{
		if(channel == null || channel.isEmpty())
			this.channel = Optional.empty();
		else
			this.channel = Optional.of(channel);
	}
	
	public void setClientID(String clientID)
	{
		if(clientID == null || clientID.isEmpty())
			this.clientID = Optional.empty();
		else
			this.clientID = Optional.of(clientID);
	}
	
	public void setClientSecret(String clientSecret)
	{
		if(clientSecret == null || clientSecret.isEmpty())
			this.clientSecret = Optional.empty();
		else
			this.clientSecret = Optional.of(clientSecret);
	}
	
	public void setKwarg1(String kwarg1) 
	{
		if(kwarg1 == null || kwarg1.isEmpty())
			this.kwarg1 = Optional.empty();
		else
			this.kwarg1 = Optional.of(kwarg1);
		
		updateManualView();
	}
	
	public void setKwarg2(String kwarg2)
	{
		if(kwarg2 == null || kwarg2.isEmpty())
			this.kwarg2 = Optional.empty();
		else
			this.kwarg2 = Optional.of(kwarg2);
		
		updateManualView();
	}
	
	/********* Public Methods *********/
	public boolean shouldGreyManualLoaders()
	{
		return oauth.isPresent() && clientID.isPresent() && clientSecret.isPresent() && channel.isPresent();
	}
	
	public void exportToFile(File file) throws InvalidFileFormatException, IOException
	{
		Ini ini = new Ini(file);
		
		ini.put("TWITCH DATA", "oauth", oauth.get());
		ini.put("TWITCH DATA", "channel", oauth.get());
		ini.put("TWITCH DATA", "id", clientID.get());
		ini.put("TWITCH DATA", "secret", clientSecret.get());
		
		ini.put("INPUT FORMAT", "prefix", prefix);
		ini.put("INPUT FORMAT", "kwarg1", (kwarg1.isPresent() ? kwarg1.get() : ""));
		ini.put("INPUT FORMAT", "kwarg2", (kwarg2.isPresent() ? kwarg2.get() : ""));
		ini.put("INPUT FORMAT", "comment", commentKWArg);
		
		ini.add("QUEUE OPTIONS", "sub_only", subOnly);
		ini.add("QUEUE OPTIONS", "queue_cap", queueCap);
		ini.add("QUEUE OPTIONS", "sub_priority", subPriority);
		ini.add("QUEUE OPTIONS", "reentry", reentry);
		
		ini.store();
	}
	
	public void importFromFile() throws InvalidFileFormatException, IOException, IllegalArgumentException, IllegalStateException
	{
		if(!configFile.isPresent())
			throw new IllegalStateException("No configuration file has been specified.");
		
		parseConfigFile();
	}
	
	/********* Private Methods *********/
	private void updateManualView()
	{
		StringBuilder line1Builder = new StringBuilder();
		StringBuilder line2Builder = new StringBuilder();
		StringBuilder line4Builder = new StringBuilder();
		
		line1Builder.append("sub_viewer: "+prefix+"queue");
		line1Builder.append((kwarg1.isPresent() ? ","+" arg1" : "" ));
		line1Builder.append((kwarg2.isPresent() ? ","+" arg2" : "" ));
		line1Builder.append((commentKWArg) ? "," + " "+comment : "");
		if(line1Builder.indexOf(",") != -1)
			line1Builder.replace(line1Builder.indexOf(","), line1Builder.indexOf(",") + 1, "");	//remove first comma
		twitchView.set(0, line1Builder.toString());
		
		line2Builder.append("[bot name]: ");
		line2Builder.append("@sub_viewer queued");
		line2Builder.append(subPriority ? " with priority!" : "!");
		twitchView.set(1, line2Builder.toString());
		
		line1Builder.insert(0, "not_");
		twitchView.set(2, line1Builder.toString());
		
		line4Builder.append("[bot name]: ");
		line4Builder.append("@not_sub_viewer ");
		line4Builder.append(subOnly ? "denied - subs only" : "queued!");
		twitchView.set(3, line4Builder.toString());
	}
	
	/**
	 * Parses the config file and assigns data members accordingly
	 * @param file - the configuration file
	 * @throws InvalidFileFormatException - thrown if the config file is not in the a valid format or if expected key is missing
	 * @throws IOException
	 * @throws IllegalArgumentException - thrown if argument in the config file is illegal
	 */
	private void parseConfigFile() throws InvalidFileFormatException, IOException, IllegalArgumentException
	{
		Ini ini = new Ini(configFile.get());
		Section twitchData = ini.get("TWITCH DATA");
		Section inputFormat = ini.get("INPUT FORMAT");
		Section queueOptions = ini.get("QUEUE OPTIONS");
		String emptyCheck;
		
		//Set Twitch Data
		emptyCheck = twitchData.get("oauth");
		if(emptyCheck.isEmpty())
			throw new IllegalArgumentException("Illegal OAuth (empty)");
		oauth = Optional.of(emptyCheck);
		
		emptyCheck = twitchData.get("channel");
		if(emptyCheck.isEmpty())
			throw new IllegalArgumentException("Illegal Channel (empty)");
		channel = Optional.of(emptyCheck);
		
		emptyCheck = twitchData.get("id");
		if(emptyCheck.isEmpty())
			throw new IllegalArgumentException("Illegal client ID (empty)");
		clientID = Optional.of(emptyCheck);
		
		emptyCheck = twitchData.get("secret");
		if(emptyCheck.isEmpty())
			throw new IllegalArgumentException("Illegal client secret (empty)");
		clientSecret = Optional.of(emptyCheck);
		
		//Set Input Format
		Character pre = inputFormat.get("prefix", Character.class);
		if(!isLegalPrefix(pre))
			throw new IllegalArgumentException("Illegal prefix");
		prefix = pre;
		
		emptyCheck = inputFormat.get("kwarg1");
		if(emptyCheck.isEmpty())
			kwarg1 = Optional.empty();
		else
			kwarg1 = Optional.of(emptyCheck);
		
		emptyCheck = inputFormat.get("kwarg2");
		if(emptyCheck.isEmpty())
			kwarg2 = Optional.empty();
		else
			kwarg2 = Optional.of(emptyCheck);
		
		commentKWArg = inputFormat.get("comment", Boolean.class);
		
		//Set Queue Options
		queueCap = queueOptions.get("queue_cap", Integer.class);
		if(!isLegalCap(queueCap))
			throw new IllegalArgumentException("Illegal queue cap");
		
		subOnly = queueOptions.get("sub_only", Boolean.class);
		subPriority = queueOptions.get("sub_priority", Boolean.class);
		reentry = queueOptions.get("reentry", Boolean.class);
	}
	
	private boolean isLegalPrefix(Character pre)
	{
		if(pre == null)
			return false;
		
		for(Character c : legalPrefixes)
			if(pre.equals(c))
				return true;
		
		return false;
	}
	
	private boolean isLegalCap(Integer cap)
	{	
		for(int i : legalCaps)
			if(i == cap)
				return true;
		
		return false;
	}
}
