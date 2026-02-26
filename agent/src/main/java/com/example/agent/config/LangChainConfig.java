package com.example.agent.config;

import com.example.agent.agent.BacklogAgent;
import com.example.agent.tools.GitHubMcpTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.List;

@Configuration
@Profile("!ci")
public class LangChainConfig {

	@Bean
	@Profile("!ci")
	public AnthropicChatModel anthropicChatModel(
			@Value("${anthropic.api-key}") String apiKey,
			@Value("${anthropic.model}") String model,
			@Value("${anthropic.max-tokens:800}") Integer maxTokens,
			@Value("${anthropic.timeout-seconds:60}") Integer timeoutSeconds) {
		return AnthropicChatModel.builder()
				.apiKey(apiKey)
				.modelName(model)
				.maxTokens(maxTokens)
				.timeout(Duration.ofSeconds(timeoutSeconds))
				.build();
	}

	//  @Bean
	//  @Profile("!ci")
	//  public OpenAiChatModel openAiChatModel(
	//          @Value("${openai.api-key}") String apiKey,
	//          @Value("${openai.model}") String model,
	//          @Value("${openai.timeout-seconds:60}") Integer timeoutSeconds
	//  ) {
	//    return OpenAiChatModel.builder()
	//            .apiKey(apiKey)
	//            .modelName(model)
	//            .timeout(Duration.ofSeconds(timeoutSeconds))
	//            .build();
	//  }

	@Bean
	@Profile("!ci")
	public List<Object> toolBeans(GitHubMcpTools githubMcpTools) {
		return List.of(githubMcpTools);
	}

	@Bean
	@Profile("!ci")
	public BacklogAgent backlogAgent(ChatModel chatModel,
			ObjectProvider<List<Object>> toolBeansProvider) {
		List<Object> toolBeans = toolBeansProvider.getIfAvailable(List::of);
		return AiServices.builder(BacklogAgent.class)
				.chatModel(chatModel)
				.tools(toolBeans)
				.build();
	}
}
