package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import app.agent.EventAgent;
import app.tools.UserDetailsTool;
import app.tools.AllEventsTool;
import app.tools.UserEventsTool;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

/**
 * Configuration for the AI Agent with tool use capabilities.
 * This enables true agentic behavior where the LLM autonomously decides which tools to call.
 */
@Configuration
public class AgentConfig {

    private final UserDetailsTool userDetailsTool;
    private final AllEventsTool allEventsTool;
    private final UserEventsTool userEventsTool;

    public AgentConfig(UserDetailsTool userDetailsTool,
                      AllEventsTool allEventsTool,
                      UserEventsTool userEventsTool) {
        this.userDetailsTool = userDetailsTool;
        this.allEventsTool = allEventsTool;
        this.userEventsTool = userEventsTool;
    }

    @Bean
    public OllamaChatModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.1:8b")
                .temperature(0.7)
                .build();
    }

    /**
     * Creates an AI Agent that can autonomously use tools to complete tasks.
     * The agent will:
     * 1. Receive a user request
     * 2. Analyze what information it needs
     * 3. Decide which tools to call (and in what order)
     * 4. Execute the tools
     * 5. Synthesize the results into a response
     *
     * This is true agentic behavior with multi-step reasoning and tool use.
     */
    @Bean
    public EventAgent eventAgent(OllamaChatModel chatModel) {
        return AiServices.builder(EventAgent.class)
                .chatLanguageModel(chatModel)
                .tools(userDetailsTool, allEventsTool, userEventsTool)
                .build();
    }
}
