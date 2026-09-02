package bg.dalexiev.grumpysenior.chat.domain.ai;

public interface TitleGenerator {

    String generateTitle(String userPrompt, String botAnswer);

}
