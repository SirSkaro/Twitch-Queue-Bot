package skaro.queue_bot.core;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import skaro.queue_bot.twitch_tools.TwitchService;

public class App extends Application 
{
    public static void main(String[] args) 
    {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) throws Exception
    {
    	URL url = getClass().getResource("/GUIs/StartMenu.fxml");
    	FXMLLoader loader = new FXMLLoader(url);
    	Parent root = loader.load();
		
		Scene scene = new Scene(root, 600, 400);
		setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
		
		stage.setTitle("Sir Skaro's Twitch Queue Interface");
		stage.setScene(scene);
		stage.show();
    }
    
    @Override
    public void stop() 
    {
        TwitchService.shutdown();
        System.exit(1);
    }
}
