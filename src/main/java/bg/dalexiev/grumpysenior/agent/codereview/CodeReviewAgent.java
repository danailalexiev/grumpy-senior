package bg.dalexiev.grumpysenior.agent.codereview;

import bg.dalexiev.grumpysenior.agent.codereview.agui.AgUiAgentSubscriber;
import bg.dalexiev.grumpysenior.agent.codereview.agui.InputMapper;
import bg.dalexiev.grumpysenior.agent.codereview.agui.AgUiMessageMapper;
import bg.dalexiev.grumpysenior.agent.codereview.tool.LintingTools;
import bg.dalexiev.grumpysenior.chat.domain.ai.AIGateway;
import bg.dalexiev.grumpysenior.chat.domain.Message;
import com.agui.core.agent.AgentSubscriber;
import com.agui.core.agent.RunAgentInput;
import com.agui.core.agent.RunAgentParameters;
import com.agui.core.event.CustomEvent;
import com.agui.core.event.StepFinishedEvent;
import com.agui.core.event.StepStartedEvent;
import com.agui.core.exception.AGUIException;
import com.agui.core.message.BaseMessage;
import com.agui.core.state.State;
import com.agui.server.EventFactory;
import com.agui.server.LocalAgent;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CodeReviewAgent extends LocalAgent implements AIGateway {

    private final ChatClient chatClient;

    private final InputMapper inputMapper;
    private final AgUiMessageMapper agUiMessageMapper;
    private final JsonMapper jsonMapper;

    private final LintingTools lintingTools;

    private static final String SYSTEM_PROMPT = """
            You are a code reviewer with a dry, passive-aggressive sense of humor.
            You review Java code submitted by the user across an ongoing conversation.
            
            ## Tool use — follow this exact decision process
            1. Did the user just submit code, OR say they changed/fixed the code?
               -> YES: call lintJavaCode with the full code.
               -> NO: do not call any tool. Answer from conversation history only.
            2. After calling the tool, only mention violations that appear in its
               output. If the output says "No violations found", say the code is
               clean — do not invent problems to stay in character.
            
            ## Output rules
            Respond with ONLY the final message to the user. Never include your
            reasoning, never say what tool you're calling or why, never label
            sections like "Tool output" or "Response" — just write the message
            as if you're texting it directly.
            
            ## Handling dismissed violations
            If the user says to ignore/skip a violation, treat it as resolved:
            never bring it up again in this conversation, even after new code is
            submitted.
            
            ## Response style
            - Never say the words "Checkstyle" or "linter". Talk about the code
              directly, as if the opinions are your own.
            - One short sentence per violation. No essay-length explanations.
            - Vary your jokes — do not reuse the same punchline structure twice
              in one conversation.
            - Format violations as a Markdown bullet list.
            
            ## Example
            
            If the user submits code with an unused import and a bad method name,
            your entire response should look like this — nothing before it, nothing after it:
            
            A couple of things:
            - Line 12: that `java.io.*` import isn't doing anything. Cut it.
            - Line 14: `Name` should be `name` — capital letters are for classes, not fields.
            """;

    public CodeReviewAgent(ChatClient chatClient, InputMapper inputMapper, AgUiMessageMapper agUiMessageMapper, JsonMapper jsonMapper, LintingTools lintingTools) throws AGUIException {
        super("code-review-agent", new State(), new LinkedList<>());
        this.chatClient = chatClient;
        this.inputMapper = inputMapper;
        this.agUiMessageMapper = agUiMessageMapper;
        this.jsonMapper = jsonMapper;
        this.lintingTools = lintingTools;
    }

    @Override
    public void runAI(Input input, Subscriber subscriber) {
        final RunAgentParameters parameters = inputMapper.mapToRunAgentParameters(input);
        final AgentSubscriber agentSubscriber = AgUiAgentSubscriber.from(subscriber, jsonMapper);
        runAgent(parameters, agentSubscriber);
    }

    @Override
    protected void run(RunAgentInput input, AgentSubscriber subscriber) {
        emitEvent(EventFactory.runStartedEvent(input.threadId(), input.runId()), subscriber);

        combineMessages(input);
        getUserMessage().ifPresent(message -> processUserMessage(message, subscriber));

        emitEvent(EventFactory.runFinishedEvent(input.threadId(), input.runId()), subscriber);
    }

    private @NonNull Optional<BaseMessage> getUserMessage() {
        try {
            return Optional.of(getLatestUserMessage(getMessages()));
        } catch (AGUIException e) {
            return Optional.empty();
        }
    }

    private void processUserMessage(BaseMessage message, AgentSubscriber subscriber) {
        emitEvent(stepStartedEvent("processing"), subscriber);


        final List<org.springframework.ai.chat.messages.Message> previousMessages = getPreviousMessages(message);

        final String answer = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .tools(lintingTools)
                .messages(previousMessages)
                .user(message.getContent())
                .call()
                .content();
        emitEvent(customMessagePayloadEvent(new Message.Payload.Bot(answer)), subscriber);

        emitEvent(stepFinishedEvent("processing"), subscriber);
    }

    private @NonNull List<org.springframework.ai.chat.messages.Message> getPreviousMessages(BaseMessage lastUserMessage) {
        return getMessages().stream()
                .filter(current -> !current.getId().equals(lastUserMessage.getId()))
                .map(agUiMessageMapper::mapToSpringAiMessage)
                .filter(Objects::nonNull)
                .toList();
    }

    private static @NonNull StepStartedEvent stepStartedEvent(String stepName) {
        final StepStartedEvent stepStartedEvent = new StepStartedEvent();
        stepStartedEvent.setStepName(stepName);
        return stepStartedEvent;
    }

    private static @NonNull StepFinishedEvent stepFinishedEvent(String stepName) {
        final StepFinishedEvent stepFinishedEvent = new StepFinishedEvent();
        stepFinishedEvent.setStepName(stepName);
        return stepFinishedEvent;
    }

    private @NonNull CustomEvent customMessagePayloadEvent(Message.Payload messagePayload) {
        final CustomEvent customEvent = new CustomEvent();
        customEvent.setRawEvent(jsonMapper.valueToTree(messagePayload));
        return customEvent;
    }
}
