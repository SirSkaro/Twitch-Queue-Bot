# Twitch Queue Bot

Twitch Queue Bot is an application that allows streamers to easily create custome queues for their Twitch channel. The application includes a GUI for ease of use, real-time control of the queue, subscriber-only features, and much more. See below for a larger list of features.

What is unique about this bot is that it gives the user/streamer 100% control over the bot while providing a real-time, intuitive, and compact interface. The application runs on the user's machine and uses credentials fed to it by the user. This means that the user must  register their own Twitch application (guide provided in the application interface), which grants them control over even the account the bot uses.

Queue Bot has room for improvement. One such improvement is that every time the user starts the application, the application requests permission to use their Twitch account information (subscriber data) instead of storing the authorization data. I plan on coming back to this project periodically to make improvements. If you have issues with it, you can contact me on [Twitter](twitter.com/sirskaro).

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

## Getting Started

If you want to use the bot, you can download the latest version on [MediaFire](http://www.mediafire.com/file/pwh9m2g4d0rdqan/queue-bot-1.0.0.jar). Simply double click the .jar file, follow the instructions, and you're good to go.

If you want to develop your version of this bot, just download the repository and set it up as a Maven project.

### Prerequisites

If you are trying to use the bot, make sure to have Java 8 installed on your machine. You may encounter problems if you only have Java 9.

Same goes for developers. I'd all recommend having Scene Builder set up and installed.

## Releases

Whenever I create new versions, I will upload the download link.

## Built With

* [Scene Builder 2](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html) - GUI creation (with the  e(fx)clipse plugin)
* [Maven](https://maven.apache.org/) - Dependency Management
 
 ## Dependencies
 * Ini4J
 * Twitch4J

## Authors

* **Benjamin Churchill** - *Everything* - [SirSkaro](https://github.com/SirSkaro)

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE.md](LICENSE.md) file for details. tl;dr, if you're going to make your own queue Twitch bot based on this one, also use this lisence. Also, you can't copy this code then sell "your" bot. 

## Acknowledgments

* Thanks to [PokeaimMD](https://www.youtube.com/user/pokeaimMD), for whom this bot was originally built. Happy belated birthday!
