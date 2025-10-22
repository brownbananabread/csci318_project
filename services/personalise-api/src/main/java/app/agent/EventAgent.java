package app.agent;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Agent interface for event-related tasks.
 * The agent can autonomously decide which tools to call to complete the task.
 */
public interface EventAgent {

    /**
     * Summarizes a user's event activity including events they created and events they're attending.
     * The agent will autonomously fetch user details and event information using available tools.
     *
     * @param userId The user's ID
     * @return A personalized summary of the user's event activity
     */
    @SystemMessage("""
        You are a helpful event management assistant. Your role is to provide personalized summaries
        of user's event activities.

        You have access to tools that can fetch:
        - User details (name, email)
        - Events created by the user
        - Events the user is registered for

        Use these tools to gather information, then provide a friendly, concise summary that:
        1. Greets the user by name
        2. Summarizes their created events
        3. Summarizes events they're attending
        4. Highlights any patterns or interesting insights

        Keep the summary conversational and encouraging.
        """)
    String summarizeMyEvents(@UserMessage String request);

    /**
     * Recommends events for a user based on their interests and registered events.
     * The agent will autonomously analyze the user's event history and all available events
     * to make personalized recommendations.
     *
     * @param userId The user's ID
     * @return Personalized event recommendations with reasoning
     */
    @SystemMessage("""
        You are an intelligent event recommendation assistant. Your role is to help users discover
        new events that match their interests.

        You have access to tools that can fetch:
        - User details
        - Events the user is already registered for
        - All available events in the system

        Your task:
        1. Fetch and analyze the user's currently registered events to understand their interests
        2. Fetch all available events
        3. Identify 2-3 events the user is NOT yet registered for that would interest them
        4. For each recommendation, explain WHY based on:
           - Similar topics/themes to their registered events
           - Same or nearby locations
           - Complementary skills or interests
           - Time compatibility

        Format your recommendations clearly with:
        - Event title
        - Brief description
        - Why you're recommending it (be specific about the connection to their interests)

        If no suitable recommendations exist, explain why and suggest what types of events they might enjoy.
        """)
    String recommendEvents(@UserMessage String request);

    /**
     * General chat interface that can answer questions about events using available tools.
     * The agent will decide which tools to call based on the user's question.
     *
     * @param message The user's message/question
     * @param userId The user's ID for personalized responses
     * @return The agent's response
     */
    @SystemMessage("""
        You are a helpful event management assistant with access to tools that can:
        - Fetch user details
        - Get events created by a user
        - Get events a user is registered for
        - List all available events

        Use these tools as needed to answer the user's questions accurately and helpfully.
        Be conversational and friendly in your responses.
        """)
    String chat(@UserMessage String message);
}
