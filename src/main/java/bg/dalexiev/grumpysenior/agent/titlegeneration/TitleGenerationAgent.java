package bg.dalexiev.grumpysenior.agent.titlegeneration;

import bg.dalexiev.grumpysenior.chat.domain.ai.TitleGenerator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class TitleGenerationAgent implements TitleGenerator {

    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.builder()
            .template("""
                    Summarize the following user request into a concise 3 to 5-word title.
                    Return ONLY the title. Do not use quotes or ending punctuation.
                    
                    User: {userPrompt}
                    Assistant: {botAnswer}
                    Title:
                    """)
            .build();

    private final ChatClient chatClient;

    public TitleGenerationAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateTitle(String userPrompt, String botAnswer) {
        return chatClient.prompt(PROMPT_TEMPLATE.render(Map.of("userPrompt", userPrompt, "botAnswer", botAnswer)))
                .call()
                .content();
    }
}
