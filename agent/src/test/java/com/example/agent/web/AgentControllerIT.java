package com.example.agent.web;

import com.example.agent.mcp.McpHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("ci")
class AgentControllerIT {

	@Autowired
	WebTestClient web;

	@MockitoBean
	McpHttpClient mcp;

	@Test
	void should_call_endpoint() {
		when(mcp.callTool(eq("create_issue"), anyMap()))
				.thenReturn(Mono.just(Map.of("number", 1, "html_url", "https://github.com/o/r/issues/1")));

		web.post().uri("/api/agent/run")
				.bodyValue(Map.of("prompt", "Create a task to add OpenTelemetry"))
				.exchange()
				.expectStatus().isOk();
	}
}
