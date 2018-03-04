package skaro.queue_bot.controller;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import skaro.queue_bot.core.ConfigurationState;
import skaro.queue_bot.core.QueueEntry;
import skaro.queue_bot.core.SessionState;
import skaro.queue_bot.twitch_tools.TwitchService;

public class QueueGUIController 
{
	/********* FXML Members *********/
	 @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField twitchName;

    @FXML
    private TextField kwarg1Field;

    @FXML
    private TextField kwarg2Field;

    @FXML
    private TextField timesInQueueField;

    @FXML
    private Label kwarg1;

    @FXML
    private Label kwarg2;

    @FXML
    private Label timesInQueue;

    @FXML
    private TextArea comment;

    @FXML
    private TextField isSub;

    @FXML
    private ListView<QueueEntry> historyList;

    @FXML
    private BarChart<String, Number> progressBarChart;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField progressCount;

    @FXML
    private ListView<String> notificationList;

    @FXML
    private ListView<QueueEntry> queueView;

    @FXML
    private TextField queueSize;

    @FXML
    private CheckBox allowReentry;

    @FXML
    private CheckBox subsOnly;

    @FXML
    private CheckBox subsPriority;

    @FXML
    private CheckBox queueClosed;

    @FXML
    private Button progressQueue;

    @FXML
    private Button announce;

    @FXML
    private Button clearQueue;

    @FXML
    private ChoiceBox<Object> queueCapOption;

    /********* Utility Members *********/
    private SessionState state;
    private TwitchService service;
    
    /********* FXML Methods *********/
    @FXML
    void announceAction(ActionEvent event) 
    {
    	if(state.getCurrEntry().isPresent())
    	{
    		service.sendMessage("@"+state.getCurrEntry().get()+" it's your turn!");
    		announce.setDisable(true);
    	}
    }

    @FXML
    void clearQueueAction(ActionEvent event) 
    {
    	state.clearQueue();
    }

    @FXML
    void progressQueueAction(ActionEvent event) 
    {
    	state.progressQueue();
    	
    	updateCurrentTab();
    	updateQueueCount();
    }

    @FXML
    void setAllowReentry(ActionEvent event) 
    {
    	state.setAllowReentry(allowReentry.isSelected());
    }

    @FXML
    void setQueueClosed(ActionEvent event) 
    {
    	state.setQueueClosed(queueClosed.isSelected());
    }

    @FXML
    void setSubOnly(ActionEvent event) 
    {
    	state.setSubOnly(subsOnly.isSelected());
    }

    @FXML
    void setSubPriority(ActionEvent event) 
    {
    	state.setSubPriority(subsPriority.isSelected());
    }

    @FXML
    void initialize() 
    {
        state = new SessionState();
        
        //Set up Queue View
    	queueView.setItems(state.getQueueAsList());
    	queueCapOption.setItems(FXCollections.observableArrayList("No Cap", new Separator(), "5", "10", "15", "20", "25", "50"));
    	queueCapOption.getSelectionModel().selectFirst();
    	queueCapOption.getSelectionModel().selectedItemProperty().addListener(createCapListener());
    	announce.setDisable(true);
    	
    	//Set up History Tab
    	historyList.setItems(state.getHistoryAsList());
    	
    	//Set up Progress Tab
    	progressBarChart.setTitle("Progress Statistics");
    	progressBarChart.getXAxis().setLabel("Subscription Status");
    	progressBarChart.getYAxis().setLabel("Value");
    	progressBarChart.setData(state.getBarChartAsList());
    	progressBar.progressProperty().bind(state.getProgressPercent());
    	progressCount.promptTextProperty().bind(state.getProgressPercentFraction());  	
    	
    	//Set up Notifications Tab
    	notificationList.setItems(state.getNotificationsList());
        
        //Set up Current Tab
        
    }
    
    /********* Public Methods *********/
    public void configureSessionState(ConfigurationState config)
    {
    	state.configure(config);
    	
    	//Set up kwargs
    	if(state.getKWArgKey1().isPresent())
    		kwarg1.setText(state.getKWArgKey1().get());
    	
    	if(state.getKWArgKey2().isPresent())
    		kwarg2.setText(state.getKWArgKey2().get());
    	
    	if(state.isCommentAllowed())
    		comment.setText("");
        
        //Set up check boxes
    	subsOnly.setSelected(state.isSubOnly());
    	allowReentry.setSelected(state.reentryAllowed());
    	subsPriority.setSelected(state.subsHavePriority());
    	queueCapOption.getSelectionModel().select(String.valueOf(state.getQueueCap()));
    	
    	//Connect to Twitch
    	TwitchService.initialize(config, state);
    	service = TwitchService.getInstance().get();
    }
    
    public SessionState getState() { return state; }
    
    /********* Private Methods *********/
    
    private ChangeListener<Object> createCapListener()
    {
    	ChangeListener<Object> listener = new ChangeListener<Object>()
    	{
			@Override
			public void changed(ObservableValue<? extends Object> arg0, Object oldCap, Object newCap) 
			{
				String input = String.class.cast(newCap);
				int cap;
				
				if(input.equals("No Cap"))
					cap = -1;
				else
					cap = Integer.valueOf(input);
				
				state.setQueueCap(cap);
			}	
    	};
    	
    	return listener;
    }
    
    private void updateCurrentTab()
    {
    	Optional<QueueEntry> currEntry = state.getCurrEntry();
    	
    	if(!currEntry.isPresent())
    	{
    		twitchName.setText("[None]");
    		kwarg1Field.setText("[None]");
    		kwarg2Field.setText("[None]");
    		timesInQueueField.setText("[N/A]");
    		isSub.setText("N/A");
    		
    		if(state.isCommentAllowed())
    			comment.setText("[None]");
    		else
    			comment.setText("[disabled]");
    		
    		announce.setDisable(true);
    	}
    	else
    	{
    		QueueEntry qe = currEntry.get();
    		twitchName.setText(qe.getRequester());
    		
    		if(qe.getKWArg1().isPresent())
    			kwarg1Field.setText(qe.getKWArg1().get());
    		
    		if(qe.getKWArg2().isPresent())
    			kwarg2Field.setText(qe.getKWArg2().get());
    		
    		if(qe.getComment().isPresent())
    			comment.setText(qe.getComment().get());
    		
    		timesInQueueField.setText(String.valueOf(state.timesInQueue(qe)));
    		isSub.setText(qe.isSub() ? "Yes" : "No");
    		
    		announce.setDisable(false);
    	}
    }
    
    private void updateQueueCount()
    {
    	queueSize.setText(String.valueOf(state.getQueueSize()));
    }
}
