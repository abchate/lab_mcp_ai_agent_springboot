package com.example.agent.web;

import com.example.agent.service.AgentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

	private static final Logger log = LoggerFactory.getLogger(AgentController.class);

	private final AgentService agentService;

	public AgentController(AgentService agentService) {
		this.agentService = agentService;
	}

	@PostMapping(value = "/run", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> run(@RequestBody RunRequest request) {
		try {
			String answer = agentService.run(request.prompt());
			return ResponseEntity.ok(new RunResponse(answer));
		} catch (Exception e) {
			log.error("Agent run failed", e);
			return ResponseEntity.status(500)
					.body(new ErrorResponse(e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
		}
	}

	public record RunRequest(String prompt) {}
	public record RunResponse(String answer) {}
	public record ErrorResponse(String error) {}
}
