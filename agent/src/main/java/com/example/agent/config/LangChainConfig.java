package com.example.agent.config;

import com.example.agent.agent.BacklogAgent;
import com.example.agent.tools.GitHubMcpTools;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
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
	public OpenAiChatModel openAiChatModel(
			@Value("${openai.api-key}") String apiKey,
			@Value("${openai.model}") String model,
			@Value("${openai.timeout-seconds:60}") Integer timeoutSeconds) {
		return OpenAiChatModel.builder()
				.apiKey(apiKey)
				.modelName(model)
				.timeout(Duration.ofSeconds(timeoutSeconds))
				.build();
	}

	@Bean
	@Profile("!ci")
	public BacklogAgent backlogAgent(ChatModel chatModel, GitHubMcpTools githubMcpTools) {
		return AiServices.builder(BacklogAgent.class)
				.chatModel(chatModel)
				.tools(List.of(githubMcpTools))
				.build();
	}
}
