# Demo Script

This script demonstrates how to use the Grumpy Senior chatbot.

## Login
Use the `login` Bruno request with the following payload:
```json
{
  "username": "admin",
  "password": "secret123"
}
```

## Create a New Chat
Use the `create-conversation` Bruno request with an empty payload

## Submit a Code Snippet For Review
Use the `generate-answer` Bruno request with the following payload:
```json
{
  "type": "code-submission",
  "message": "Can you review this class?",
  "code": "package com.demo;\n\nimport java.util.*;\nimport java.io.*;\n\npublic class userService {\n    private String Name;\n    public static final int  MAX_USERS=100;\n\n    public void  setName(String n){\n        this.Name=n;\n    }\n\n    public boolean checkUser(String u) {\n        if(u==null) return false;\n        else\n            return true;\n    }\n}"
}
```

The chatbot will stream the response.
Expected bot reply, roughly:

"userService — really committing to the lowercase-class-name bit, huh? Also that import java.io.* is doing absolutely nothing for you. And Name starting with a capital letter is going to confuse every IDE autocomplete for the rest of time."

## Dismiss a Violation
Use the `generate-answer` Bruno request with the following payload:
```json
{
  "type": "prompt",
  "message": "Ignore the unused import, I'm going to use it for file logging later."
}
```

Bot should acknowledge and not re-run the tool.

## Ask the Bot to Recall
Use the `generate-answer` Bruno request with the following payload:
```json
{
  "type": "prompt",
  "message": "What did we agree to leave alone?"
}
```

This is the line that proves it's reading history, not re-linting — there's no tool call possible here that would answer this correctly; it has to come from context.

## Submit Revised Code
Use the `generate-answer` Bruno request with the following payload:
```json
{
  "type": "code-submission",
  "message": "I fixed the naming, can you check again?",
  "code": "package com.demo;\n\nimport java.io.*;\n\npublic class UserService {\n    private String name;\n    public static final int MAX_USERS = 100;\n\n    public void setName(String n) {\n        this.name = n;\n    }\n\n    public boolean checkUser(String u) {\n        return u != null;\n    }\n}"
}
```

This should trigger a fresh tool call (new code = new lint), and should still remember the import is "intentionally" unused from Turn 2 — a good chance to show the model correctly not mentioning it again, or to expose a failure mode live if it slips up ("even good demos have honest rough edges" is a fine thing to say if this happens).

## A Pure Conversation Question
Use the `generate-answer` Bruno request with the following payload:
```json
{
  "type": "prompt",
  "message": "Why do you care about unused imports anyway?"
}
```

Shows the bot answering as a normal chatbot when there's nothing to look up — useful contrast so the audience sees it's not tool-calling reflexively on every message.

## Other possible questions
- "Which of these would actually break the build vs just style noise?" — tests whether it can reason about severity, not just repeat the tool output.
- "Show me the fixed version of just the checkUser method." — pushes it from critique into code generation, nice contrast in capability within the same conversation.
- "If I use Lombok's @Data, does that fix the naming issue?" — good one to plant if you want to show a wrong/overconfident answer live and use it to talk about hallucination risk even with tool grounding — the tool only grounds the violations, not everything the model says.

