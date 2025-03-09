# Anekdoter

Anekdoter is an AI-powered anecdote generator and management system that creates, rates, and shares jokes in Russian. The application uses multiple AI models to generate humorous content based on tags/topics, allows users to rate anecdotes, and automatically shares top-rated jokes to a configured Telegram channel.

## Features

- AI-generated anecdotes using multiple language models (Claude, ChatGPT, DeepSeek)
- Tag-based anecdote generation
- User rating system
- AI-based automated joke quality assessment
- Automatic posting of top-rated anecdotes to Telegram
- Simple web interface for exploring and rating jokes
- Extensible architecture for integrating new LLM providers

## Requirements

- JDK 21 or higher
- Docker and Docker Compose
- API keys for at least one AI service (DeepSeek, OpenAI/ChatGPT, or Anthropic/Claude)
- Telegram Bot Token (if you want to use the Telegram posting functionality)

## Setup Instructions

### 1. Database Setup

The application uses PostgreSQL as its database. Run it with Docker:

```bash
docker run --name anekdoter-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_DB=anekdoter \
  -p 5432:5432 \
  -d postgres:alpine
```

### 2. Telegram Bot Setup

If you want to use the Telegram posting functionality:

1. Create a new Telegram bot by messaging [@BotFather](https://t.me/botfather) on Telegram
2. Follow the instructions to create a new bot and get your bot token
3. Create a channel or group where you want to post anecdotes
4. Add your bot to this channel/group as an administrator with posting privileges
5. Get the chat ID of your channel/group (you can use [@getidsbot](https://t.me/getidsbot) or other methods)

### 3. AI Service API Keys

You'll need API keys for at least one of the supported AI services:

- **DeepSeek**: Register at [DeepSeek API](https://platform.deepseek.com/)
- **ChatGPT/OpenAI**: Register at [OpenAI Platform](https://platform.openai.com/)
- **Claude/Anthropic**: Register at [Anthropic](https://www.anthropic.com/)

### 4. Application Configuration

Create an `application-local.yaml` file in the `src/main/resources` directory or set the following environment variables:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/anekdoter
    username: postgres
    password: postgres

anekdoter:
  # Configure background tasks
  background:
    generation:
      enabled: true  # Set to false if you don't want automatic generation
    chatbots-rater:
      enabled: true  # Set to false if you don't want AI to rate anecdotes
    telegram-poster:
      enabled: true  # Set to false if you don't want to post to Telegram

  # Telegram settings
  telegram:
    bot-token: ${TELEGRAM_BOT_TOKEN}
    chat-id: ${TELEGRAM_CHAT_ID}
```

### 5. Environment Variables

Set the following environment variables with your API keys:

```bash
# Required for at least one of these services
export DEEPSEEK_API_TOKEN=your_deepseek_api_token
export CHATGPT_API_TOKEN=your_openai_api_token
export CLAUDE_API_TOKEN=your_anthropic_api_token

# Required for Telegram functionality (if enabled)
export TELEGRAM_BOT_TOKEN=your_telegram_bot_token
export TELEGRAM_CHAT_ID=your_telegram_chat_id
```

## Running the Application

### From the command line

```bash
mvn spring-boot:run
```

### As a JAR file

```bash
mvn clean package
java -jar target/anekdoter-0.0.1-SNAPSHOT.jar
```

## Accessing the Application

Once running, the application will be available at:

- Web interface: http://localhost:8080
- API: http://localhost:8080/api/v1/

## API Documentation

- `GET /api/v1/tags` - Get all available tags
- `GET /api/v1/anecdote` - Get a new anecdote (add `tag_id` query parameter to filter by tag)
- `POST /api/v1/anecdote/{id}/rate` - Rate an anecdote (body: `{"rate": 1-5}`)

## Extending with Additional LLM Providers

The application is designed to be easily extended with new language models. You can add support for Russian LLMs like YandexGPT, GigaChat, or international ones like Qwen by modifying the configuration.

### Adding a New LLM Provider

To add a new LLM provider, simply add its configuration to the `application.yaml` file:

```yaml
anekdoter:
  chatbots:
    # Existing providers
    deepseek:
      # ...existing config...
    
    # Add new provider - example for YandexGPT
    yandexgpt:
      url: https://llm.api.cloud.yandex.net/foundationModels/v1/completion
      headers:
        Authorization: Api-Key ${YANDEX_API_KEY}
        X-Folder-ID: ${YANDEX_FOLDER_ID}
      models:
        - yandexgpt-lite
      default-model: ${anekdoter.chatbots.yandexgpt.models[0]}
      request-mask: |-
        {"modelUri":"gpt://${YANDEX_FOLDER_ID}/${model}","completionOptions":{"stream":false,"temperature":0.6,"maxTokens":2000},"messages":[{"role":"user","text":"${prompt}"}]}
      response-text-json-path: /result/alternatives/0/message/text
      
    # Example for GigaChat
    gigachat:
      url: https://gigachat.devices.sberbank.ru/api/v1/chat/completions
      headers:
        Authorization: Bearer ${GIGACHAT_API_TOKEN}
        Content-Type: application/json
      models:
        - GigaChat
      default-model: ${anekdoter.chatbots.gigachat.models[0]}
      request-mask: |-
        {"model":"${model}","messages":[{"role":"user","content":"${prompt}"}],"temperature":0.7,"max_tokens":1500}
      response-text-json-path: /choices/0/message/content
    
    # Example for Qwen
    qwen:
      url: https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation
      headers:
        Authorization: Bearer ${QWEN_API_TOKEN}
      models:
        - qwen-max
        - qwen-plus
      default-model: ${anekdoter.chatbots.qwen.models[0]}
      request-mask: |-
        {"model":"${model}","input":{"messages":[{"role":"user","content":"${prompt}"}]},"parameters":{"temperature":0.7,"max_tokens":1500}}
      response-text-json-path: /output/choices/0/message/content
```

### Environment Variables for New Providers

Add the necessary environment variables for your new providers:

```bash
# For YandexGPT
export YANDEX_API_KEY=your_yandex_api_key
export YANDEX_FOLDER_ID=your_yandex_folder_id

# For GigaChat
export GIGACHAT_API_TOKEN=your_gigachat_token

# For Qwen
export QWEN_API_TOKEN=your_qwen_api_token
```

### Provider-Specific Adjustments

Different LLM providers might require specific adjustments:

1. **Authorization Methods**: Some providers like GigaChat might require OAuth tokens or special authentication flows
2. **Request Structure**: The structure of API requests varies between providers
3. **Response Parsing**: The JSON path to extract the model's response varies between providers
4. **Rate Limiting**: Adjust the rate limiter settings based on the provider's limits
5. **Model Parameters**: Fine-tune temperature, top_p, and other parameters per provider

The application is designed to handle these differences through configuration without code changes in most cases.

## Additional Notes and Future Improvements

- Consider adding user authentication for rating management
- Implement a more sophisticated web UI with additional features
- Add API rate limiting for public deployments
- Include a caching layer for frequently accessed anecdotes
- Create a mobile app for easier access
- Implement multi-language support for anecdotes

## Troubleshooting

- If AI services return errors, check your API keys and quotas
- For Telegram posting issues, ensure the bot has admin permissions
- Database connection issues may require checking your PostgreSQL configuration
- Look at the application logs for detailed error information

---

Contributions are welcome! Feel free to submit issues and pull requests to improve the project.