# Twitch Queue Bot

Twitch Queue Bot is an application that allows streamers to easily create and manage custom queues for their Twitch channel. The application includes a Graphical User Interface for ease of use, real-time control of the queue, subscriber features, and more. See below for a larger list of features.

Twitch Queue Bot grants the streamer complete control over their chat bot, from choosing the bot's account name to storing sensitive credentials. It provides a real-time, intuitive, and compact management interface so that the streamer can effortlessly interact with their audience without missing a beat.

![Start menu](https://i.imgur.com/hTAZxci.png)
![Queue menu](https://i.imgur.com/dYvRXOc.png)

(Note: sub only was turned off after start up)

## Features
* Platform independent (runs on all OSes)
* Interactive graphic user interface (GUI)
* Subscriber only options
* Subscriber priority options
* Allow for up to two custom arguments and an optional comment as input from viewers (example: "!queue arg1, arg2, gl hf PogChamp")
* Custom prefix options so as to not conflict with existing bots
* Ability to cap the queue size
* Ability to close the queue at will
* Notifications list with time stamps when an event occurs
* Real-time statistics of a queueing session
* Announcing a viewer's turn
* Records the number of times a user has entered a queue (or disallows viewers from re-entering)
* Runs on your machine so that you don't have to depend on someone's website

### Commands
* !queue - arguments are customizable - adds a viewer to the queue if the viewer is eligible for queueing
* !wait - no arguments - tells the viewer their placing in the queue
* !drop - no arguments - removes a user from the queue unless they are the current entry

## Getting Started/Download

### Setting up the bot for your Twitch channel

Download the latest build (v1.0.4) of the bot on [MediaFire](http://www.mediafire.com/file/rs5wdvd36xpzjgg/queue-bot-1.0.4.jar) and double click to run. You can follow the instructions in the Welcome tab or follow the steps below.

#### Step 1 - Register an Application
Go to https://dev.twitch.tv/ and sign into your streaming account. Go to `My Applications` and and click `Register Your Application`. You'll see three fields you need to fill: `Name`, `OAuth Redirect URI`, and `Application Category`.

* Set `Name` to whatever you want.
* Set `OAuth Redirect URI` to http://127.0.0.1:7090/oauth_authorize_twitch
* Set `Application Category` to Application Integration via the drop down menu

Click `Register`. Generate a new Client Secret. Grab the ``Client Secret`` and ``Client ID`` and put it in the appropriate fields in the Manual Configuration tab in the application.

#### Step 2 - Create Your Bot's Twitch Account
Create a new Twitch account for your bot to use. Go to https://twitchapps.com/tmi/ and sign in with this new account. Click `Connect with Twitch`. Grab the `OAuth Token` and put it in the appropriate field in the Manual Configuration tab in the application.

From here you can finish filling out the rest of the configuration.

#### Step 3 (Optional) - Export Your Configuration
Unless you want to fill out these fields every time you start the bot, click the `Export to File` button and save a configuration file. In the future when you want to use this bot, you can go to the Load Configuration File tab, load the file, and get started in 3 clicks.

*NOTE: If you fail to grant the bot permissions to look at your channel's subscription data or you are not partnered, subscriber features will not work.*

### Cloning the Project
If you want to develop your version of this bot, just download the repository and set it up as a Maven project.

### Prerequisites

If you are trying to use the bot, make sure to have Java 8 or 9 installed on your machine.

Same goes for developers. I'd also recommend having Scene Builder set up and installed.

## Built With

* [Scene Builder 2](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html) - GUI creation (with the  e(fx)clipse plugin)
* [Maven](https://maven.apache.org/) - Dependency Management
 
 ## Dependencies
 * [Ini4J](ini4j.sourceforge.net)
 * [Twitch4J](https://github.com/twitch4j/twitch4j)

## Authors

* **Benjamin Churchill** - [SirSkaro](https://github.com/SirSkaro)

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE.md](LICENSE.md) file for details. tl;dr, if you're going to make your own queue Twitch bot based on this one, also use this lisence. Also, you can't copy this code then sell "your" bot. 

## Acknowledgments

Thanks to [PokeaimMD](https://www.youtube.com/user/pokeaimMD), for whom this bot was originally built. Happy belated birthday!
