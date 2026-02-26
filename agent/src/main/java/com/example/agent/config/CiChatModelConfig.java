package com.example.agent.config;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ci")
public class CiChatModelConfig {

	@Bean
	public ChatModel ciChatModel() {
		return new ChatModel() {
			@Override
			public ChatResponse chat(ChatRequest request) {
				return ChatResponse.builder()
						.aiMessage(AiMessage.from("CI OK"))
						.build();
			}
		};
	}
}
