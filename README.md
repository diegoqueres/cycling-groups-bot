# cycling-groups-bot

Telegram bot to find cycling groups near you.

This is a Proof of Concept (PoC) Telegram bot developed in Kotlin using rubenlagus/TelegramBots. 

## Motivation

While writing the article [Developing Telegram Bots with Spring Boot and TelegramBots Abilities](#), I felt inspired to develop a PoC that could practically utilize the features I presented in the article. Additionally, I was motivated to create a project using the Kotlin language _(which I recently learned)_.

## Project Features

- **Registration:** Register cycling groups.
- **Search:** Find cycling groups in the user's region. It find by location or by city name.
- **Help:** Get assistance on using the bot.

| Search groups by location | Help | Search groups by city name | 
|----------|:-------------:|------:|
| <img src="doc/imgs/Screenrecorder-2024-06-08-18-28-24-293.gif" alt="Search groups by location" width="auto" height="550">| <img src="doc/imgs/Screenrecorder-2024-06-08-18-29-14-374.gif" alt="Help" width="auto" height="550"> | <img src="doc/imgs/Screenrecorder-2024-06-08-18-32-57-500.gif" alt="Search groups by city name" width="auto" height="550"> |

## Running the Project

To run the Kotlin Spring Boot project using Gradle from the command line, follow these steps:

1. **Build the Project:**
Open your terminal and navigate to the project directory.
Run the following command to build the project:
```bash
./gradlew build
```
2. **Set Up Environment Variables:**
Before running the application, ensure that you have configured the necessary environment variables. These variables typically include credentials, API keys, or any other sensitive information required by the application. You can use the provided `.env.example` file as a template.

3. **Run the Application:**
After configuring the environment variables, you can run the application using the following command:
```bash
./gradlew bootRun
```
This command will start the Spring Boot application, and you should see the logs indicating that the application has started successfully.

4. **Access the Bot:**
Once the application is running, you can access the Telegram bot by searching for it in the Telegram app and starting a conversation with it. You can then interact with the bot using the available commands and functionalities.
Available commands:
- /start
- /find
- /help
- /register _(only bot admins)_

By following these steps, you can easily run the Kotlin Spring Boot project using Gradle, configure the necessary environment variables, and interact with the Telegram bot. If you encounter any issues during the setup or execution process, refer to the project documentation or seek assistance from the project contributors.

## Internationalization

I opted to create a bot that caters to more than one language. While this brings flexibility for the bot to serve multiple languages, there are trade-offs involved:
- Telegram resources are geared towards single-language bots (commands, info, etc.). As more languages are added, maintaining simplicity for the user becomes more challenging. I believe it would be feasible for 2-3 languages at most.
- The code becomes slightly more complex to accommodate multiple languages.

An alternative would be to have multiple instances of the application, each serving a specific language. If we find it worthwhile, we can consider this approach in the future.

## Authors

| [<img loading="lazy" src="https://avatars.githubusercontent.com/u/2144655?v=4" width=115><br><sub>Diego Queres</sub>](https://github.com/diegoqueres) |
| :---: |

## License

Copyright [2024] [Diego Queres]

All rights reserved. The source code in this repository is available for reading and studying purposes only. Any reproduction, redistribution, or creation of derivative works based on this code, whether in whole or in part, is strictly prohibited without the express permission of the author.

## References
* [Article I wrote about Telegram Bot Abilities](https://medium.com/@diegoqueres81/desenvolvendo-bots-para-telegram-com-spring-boot-e-telegrambots-abilities-5270e574423d)
* ['Creating a Telegram Bot with Spring Boot', an article that provides fundamentals to utilize TelegramBots Abilities with Spring Boot: _https://www.baeldung.com/spring-boot-telegram-bot_](https://www.baeldung.com/spring-boot-telegram-bot)
* [Repository with an extensive list of bot implementations for various purposes: _https://github.com/stdmk/jtelebot_](https://github.com/stdmk/jtelebot)
]()
