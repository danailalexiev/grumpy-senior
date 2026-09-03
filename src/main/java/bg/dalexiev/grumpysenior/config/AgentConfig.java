package bg.dalexiev.grumpysenior.config;

import bg.dalexiev.grumpysenior.agent.CodeReviewAgent;
import bg.dalexiev.grumpysenior.agent.agui.ChatMapper;
import bg.dalexiev.grumpysenior.agent.agui.SpringAiMapper;
import bg.dalexiev.grumpysenior.agent.tool.LintingTools;
import bg.dalexiev.grumpysenior.chat.domain.AIGateway;
import com.agui.core.exception.AGUIException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class AgentConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
//                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();
    }

    @Bean
    public AIGateway aiGateway(ChatClient chatClient, JsonMapper jsonMapper) throws AGUIException {
        return new CodeReviewAgent(chatClient, ChatMapper.newInstance(), SpringAiMapper.newInstance(), jsonMapper, LintingTools.newInstance());
    }
}
