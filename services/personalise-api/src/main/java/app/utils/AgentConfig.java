package app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import app.agent.EventAgent;
import app.tools.UserDetailsTool;
import app.tools.AllEventsTool;
import app.tools.UserEventsTool;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

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

    @Bean
    public EventAgent eventAgent(OllamaChatModel chatModel) {
        return AiServices.builder(EventAgent.class)
                .chatLanguageModel(chatModel)
                .tools(userDetailsTool, allEventsTool, userEventsTool)
                .build();
    }
}
