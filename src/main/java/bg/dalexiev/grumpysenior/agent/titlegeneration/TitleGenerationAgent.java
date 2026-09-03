package bg.dalexiev.grumpysenior.agent.titlegeneration;

import bg.dalexiev.grumpysenior.agent.mapper.MessagePayloadMapper;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import bg.dalexiev.grumpysenior.chat.domain.ai.TitleGenerator;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

public class TitleGenerationAgent implements TitleGenerator {

    private static final PromptTemplate PROMPT_TEMPLATE = PromptTemplate.builder()
            .template("""
                    Summarize the following user request into a concise 3 to 5-word title.
                    Return ONLY the title. Do not use quotes or ending punctuation.
                    
                    User:
                    {userPrompt}
                    
                    Assistant:
                    {botAnswer}
                    
                    Title:
                    """)
            .build();

    private final ChatClient chatClient;

    private final MessagePayloadMapper messagePayloadMapper;

    public TitleGenerationAgent(ChatClient chatClient, MessagePayloadMapper messagePayloadMapper) {
        this.chatClient = chatClient;
        this.messagePayloadMapper = messagePayloadMapper;
    }

    @Override
    public String generateTitle(Message.Payload.@NonNull User userPrompt, Message.Payload.@NonNull Bot botAnswer) {
        final String userPromptContent = messagePayloadMapper.toAiReadableContent(userPrompt);
        final String botAnswerContent = messagePayloadMapper.toAiReadableContent(botAnswer);
        return chatClient.prompt(PROMPT_TEMPLATE.render(Map.of("userPrompt", userPromptContent, "botAnswer", botAnswerContent)))
                .call()
                .content();
    }
}
