package bg.dalexiev.grumpysenior.chat.domain.event;

import bg.dalexiev.grumpysenior.chat.domain.ai.TitleGenerator;
import bg.dalexiev.grumpysenior.chat.persistence.ConversationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TitleGenerationListener {

    private final TitleGenerator titleGenerator;

    private final ConversationRepository conversationRepository;

    public TitleGenerationListener(TitleGenerator titleGenerator, ConversationRepository conversationRepository) {
        this.titleGenerator = titleGenerator;
        this.conversationRepository = conversationRepository;
    }

    @Transactional
    @EventListener
    @Async("titleGenerationExecutor")
    public void handleFirstTurnCompleted(FirstTurnCompletedEvent event) {
        String generatedTitle = titleGenerator.generateTitle(event.userPrompt(), event.botAnswer());

        if (generatedTitle.isBlank()) return;
        if (generatedTitle.length() > 100) {
            generatedTitle = generatedTitle.substring(0, 100);
        }

        conversationRepository.updateTitle(event.conversationId(), generatedTitle);
    }
}
