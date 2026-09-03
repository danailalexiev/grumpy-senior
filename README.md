# grumpy-senior

## Description
This is a demo project that showcases one possible approach to building AI-powered chatbots in Java using Spring Boot and Spring AI.

## Goal

The demo covers the following topics:
- Reusing existing knowledge
  - Standard JWT authentication using Spring Security
  - Persistence layer implemented in Spring Data JDBC
  - REST API endpoints for chatbot interactions using Spring Web
- Recipes for common chatbot use cases
  - Managing user and bot messages
  - API design techniques
  - Chat name generation
- Integrating AI
  - AI entry points in your business logic layer
  - Agent implementation using Spring AI
  - AG-UI for streaming

## Structure

### Package structure
- [agent](src/main/java/bg/dalexiev/grumpysenior/agent) – Only available in certain branches. AI agent implementation (agents, tools)
- [auth](src/main/java/bg/dalexiev/grumpysenior/auth) – Authentication and authorization related code (JWT auth)
- [chat](src/main/java/bg/dalexiev/grumpysenior/chat) – Chat-related code (chat history persistence, business logic, chat REST API)
- [config](src/main/java/bg/dalexiev/grumpysenior/config) – Spring configuration and plumbing
- [user](src/main/java/bg/dalexiev/grumpysenior/user) – User management (currently limited persistence only)

### Branch structure
- `main` - The starting point for an AI integration. No AI agent implementation, no AG-UI, only auth, user and chat management.
- `feat/plugging-in-ai` - AI agent implementation with AG-UI, tools and a streaming API endpoint
- `feat/generate-chat-title` - AI-powered chat title generation using Application Events

## Running Locally
1. Clone the repository
2. Run the docker services using `docker-compose up`
3. Pull the `llama3.1:8b` model from `ollama` container
4. Start the Spring app
5. Use the [Bruno](https://www.usebruno.com/) collection in [this folder](bruno) to test the endpoints. Use admin / secret123 to log in.