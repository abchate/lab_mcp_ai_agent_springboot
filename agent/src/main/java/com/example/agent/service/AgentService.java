package com.example.agent.service;

import com.example.agent.agent.BacklogAgent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!ci")
public class AgentService {

	private final BacklogAgent backlogAgent;

	public AgentService(@Lazy BacklogAgent backlogAgent) {
		this.backlogAgent = backlogAgent;
	}

	public String run(String prompt) {
		return backlogAgent.handle(prompt);
	}
}
