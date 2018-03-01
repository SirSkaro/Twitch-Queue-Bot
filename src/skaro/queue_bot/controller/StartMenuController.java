package skaro.queue_bot.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import skaro.queue_bot.core.ConfigurationState;
import skaro.queue_bot.twitch_tools.TwitchService;

public class StartMenuController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextFlow logoFlow, welcomeFlow;

    @FXML
    private ToggleButton reentryButton, priorityButton, subOnlyButton;

    @FXML
    private ChoiceBox<Object> queueCapBox;

    @FXML
    private TextField oauthField, channelField, kwarg2, kwarg1, clientIDField, clientSecretField, chooseFileField;

    @FXML
    private ListView<String> chatPreview;

    @FXML
    private CheckBox commentKWArg;

    @FXML
    private ChoiceBox<Character> prefixChoice;

    @FXML
    private Button exportButton, startConfManual, startConfImport, chooseFileButton;

    @FXML
    void clientIDAction(KeyEvent event)
    {
    	String text = clientIDField.getText();
    	config.setClientID(text);
    	testForGreySubmissionManual();
    }
    
    @FXML
    void clientSecretAction(KeyEvent event)
    {
    	String text = clientSecretField.getText();
    	config.setClientSecret(text);
    	testForGreySubmissionManual();
    }

    @FXML
    void chooseFileAction(ActionEvent event) 
    {
    	FileChooser fileChooser = setUpFileChooser();

    	fileChooser.setTitle("Open Configuration File");
    	File file = fileChooser.showOpenDialog(new Stage());
    	config.setConfigFile(file); 
    	
    	if(file != null)
    	{
    		chooseFileField.setText(file.getPath());
    		startConfImport.setDisable(false);
    	}
    	else
    	{
    		chooseFileField.setText("");
    		startConfImport.setDisable(true);
    	}
    }

    @FXML
    void exportAction(ActionEvent event)
    {
    	 FileChooser fileChooser = setUpFileChooser();
    	 
         fileChooser.setTitle("Save Configuration");
         File confFile = fileChooser.showSaveDialog(new Stage());
         
         if(confFile != null)
         {
        	 try
        	 {
        		 saveAsEmptyFile(confFile);
        		 config.exportToFile(confFile);
        	 }
        	 catch(Exception ex) { showExportAlert(ex); }
         }
    }
    
    @FXML
    void startConfImportAction(ActionEvent event)
    {
		try 
		{
			config.importFromFile();
			setUpNextScene();
		} 
		catch (Exception e) 
		{
			showErrorAlert(e);
		}
    }

    @FXML
    void startConfManualAction(ActionEvent event) 
    {
		try 
		{
			setUpNextScene();
		} 
		catch (Exception e) 
		{
			showErrorAlert(e);
		}
    }

    @FXML
    void oauthAction(KeyEvent event) 
    {
    	String text = oauthField.getText();
    	config.setOauth(text);
    	testForGreySubmissionManual();
    }
    
    @FXML
    void channelAction(KeyEvent event) 
    {
    	String text = channelField.getText();
    	config.setChannel(text);
    	testForGreySubmissionManual();
    }

    @FXML
    void priorityAction(ActionEvent event) 
    {
    	config.setSubPriority(priorityButton.isSelected());
    }

    @FXML
    void reentryAction(ActionEvent event) 
    {
    	config.setReentry(reentryButton.isSelected());
    }

    @FXML
    void subOnlyAction(ActionEvent event) 
    {
    	config.setSubOnly(subOnlyButton.isSelected());
    }
    
    @FXML
    void kwarg1Action(KeyEvent event)
    {
    	config.setKwarg1(kwarg1.getText());
    }
    
    @FXML
    void kwarg2Action(KeyEvent event)
    {
    	config.setKwarg2(kwarg2.getText());
    }
    
    @FXML
    void commentKWArgAction(ActionEvent event) 
    {
    	config.setCommentKWArg(commentKWArg.isSelected());
    }
    
    /********* Utility Members *********/
    private ConfigurationState config;

    @FXML
    void initialize()
    {
        config = new ConfigurationState('!');

        //Set up Manual tab
        queueCapBox.setItems(FXCollections.observableArrayList("No Cap", new Separator(), "5", "10", "15", "20", "25", "50"));
        queueCapBox.getSelectionModel().selectFirst();
        queueCapBox.getSelectionModel().selectedItemProperty().addListener(createCapListener());
        
        prefixChoice.setItems(FXCollections.observableArrayList('!', '#', '%', '+', '.'));
        prefixChoice.getSelectionModel().selectFirst();
        prefixChoice.getSelectionModel().selectedItemProperty().addListener(createPrefixListener());
        
        chatPreview.setItems(config.getTwitchView());
        
        //Set up Welcome tab
        welcomeFlow.setTextAlignment(TextAlignment.CENTER);
        Text title = new Text("Weclome to Sir Skaro's Twitch Queue!\n\n");
        title.setFont(new Font("Tahoma",30));
        ArrayList<Node> manualInfo = new ArrayList<Node>();
        
        Text intro = new Text("The Twitch Queue bot comes equiped with features to help streamers easily customize and manage a queue in their Twitch chat. "
        		+ "Please read the following instructions to get started.\n\n");
        intro.setFont(new Font("Tahoma",14));
        
        Text manualTitle = new Text("Manual Configuration\n");
        manualTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
        
        Text manualStep1_1 = new Text("1.) Register an application at ");
        Hyperlink step1Link = createHyperlink("https://dev.twitch.tv/");
        Text manualStep1_2 = new Text("(set \"OAuth Redirect URI\" to \"http://127.0.0.1:7090/oauth_authorize_twitch\" "
        		+ "and \"Application Category\" to \"Application Integration\"). Get the Client ID and Client Secret.\n\n");
        manualInfo.add(manualStep1_1);
        manualInfo.add(step1Link);
        manualInfo.add(manualStep1_2);
        
        Text manualStep2_1 = new Text("2.) Create a new Twitch account - this will be your bot's account. Once created and signed in, go to ");
        Hyperlink step2Link = createHyperlink("https://twitchapps.com/tmi/");
        Text manualStep2_2 = new Text(" and create an OAuth Token. Get said OAuth Token. Sign back into your account if you wish.\n\n");
        manualInfo.add(manualStep2_1);
        manualInfo.add(step2Link);
        manualInfo.add(manualStep2_2);
        
        Text manualStep3 = new Text(
        		"3.) Fill out the rest of the form and export your configuration file if you don't want to go through this process again. If you ever want to create a different "
        		+ "configuration, you can reuse these tokens and do another manual configuration or edit the intuitive configuration file.\n\n");
        manualInfo.add(manualStep3);
        		
        Text manualStep4 = new Text(
        		"4.) Upon start up, you will be prompted to give permissions to your application to view subscription data in your default web browser. "
        		+ "If you do not grant permissions (or if you are not partnered), none of the subscriber features will work.\n\n");
        manualInfo.add(manualStep4);
        
        for(Node n : manualInfo)
        {
        	if(n instanceof Text)
        		((Text)n).setFont(new Font("Tahoma",14));
        	else
        		((Hyperlink)n).setFont(new Font("Tahoma",14));
        }
        
        Text loadTitle = new Text("Load Configuration\n");
        loadTitle.setFont(Font.font("Tahoma", FontWeight.BOLD, 18));
        
        Text loadInfo = new Text("Select a configuration file and get started!");
        loadInfo.setFont(new Font("Tahoma",14));
        
        welcomeFlow.getChildren().addAll(title, intro, manualTitle); 
        welcomeFlow.getChildren().addAll(manualInfo);
        welcomeFlow.getChildren().addAll(loadTitle, loadInfo);
        
        //Set up backdrop
        //someday...
    }
    
    /********* Private Methods *********/
    
	private ChangeListener<Object> createCapListener()
	{
		ChangeListener<Object> listener = new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> arg0, Object oldCap, Object newCap) 
			{
				String old = String.class.cast(newCap);
				int cap;
				
				if(old.equals("No Cap"))
					cap = -1;
				else
					cap = Integer.valueOf(old);
				
				config.setQueueCap(cap);
			}	
		};
		
		return listener;
	}
	
	private ChangeListener<Object> createPrefixListener()
	{
		ChangeListener<Object> listener = new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<? extends Object> arg0, Object oldPrefix, Object newPrefix) 
			{
				Character c = Character.class.cast(newPrefix);
				config.setPrefix(c);
			}	
		};
		
		return listener;
	}
	
	private void testForGreySubmissionManual()
	{
		boolean shouldDisable = !( config.shouldGreyManualLoaders() );
		startConfManual.setDisable(shouldDisable);
		exportButton.setDisable(shouldDisable);
	}
	
	private void saveAsEmptyFile(File file) throws IOException
	{
		FileWriter fileWriter = new FileWriter(file);
        fileWriter.close();
	}
	
	private void showExportAlert(Exception ex)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().setPrefSize(320, 240);
		alert.setTitle("Export Erorr");
		alert.setHeaderText("Configuration file could not be saved");
		alert.setContentText("Some error occured and your configuration could not "
				+ "be saved. Please make sure you have writing permissions in "
				+ "the file destination specified.\n"
				+ "\n"
				+ "If you are certain that this is a bug, please report the following "
				+ "error: \""+ ex.toString() + "\"");
		
		alert.show();
	}
	
	private void showErrorAlert(Exception ex)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.getDialogPane().setPrefSize(320, 240);
		alert.setTitle("Configuration Erorr");
		alert.setHeaderText("There is a problem with your configurations");
		alert.setContentText("Some error occured and your configuration could not "
				+ "be loaded. Please make sure your configuration data is correct.\n"
				+ "\n"
				+ "If you are certain that this is a bug, please report the following "
				+ "error: \""+ ex.toString() + "\"");
		
		
		alert.show();
		TwitchService.shutdown();
	}
	
	private FileChooser setUpFileChooser()
	{
		FileChooser fileChooser = new FileChooser();
    	ExtensionFilter filter = new ExtensionFilter("Configuration File (*.conf)", "*.conf");
    	File pwd = new File(System.getProperty("user.dir"));
    	
    	fileChooser.setInitialDirectory(pwd);
   	 	fileChooser.getExtensionFilters().add(filter);
   	 	
   	 	return fileChooser;
	}
	
	private void setUpNextScene() throws IOException
	{
		URL url = getClass().getResource("/GUIs/QueueGUI.fxml");
		FXMLLoader loader = new FXMLLoader(url);
    	Parent root = loader.load();
		Stage stage = (Stage) startConfImport.getScene().getWindow();
		Scene scene = new Scene(root, 600, 400);
		QueueGUIController controller = loader.getController();
		
		//Configure initial session state and connect to Twitch
		if(config.getKwarg2().isPresent() && !config.getKwarg1().isPresent())
		{
			config.setKwarg1(config.getKwarg2().get());
			config.setKwarg2(null);
		}
		
		controller.configureSessionState(config);
		
		stage.setScene(scene);
	}
	
	private Hyperlink createHyperlink(String url)
	{
		Hyperlink result = new Hyperlink();
		result.setText(url);
		result.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent e) {
            	try {
            	    Desktop.getDesktop().browse(new URL(url).toURI());
            	} catch (Exception ex) {}
            }
        });
        
        return result;
	}
}
