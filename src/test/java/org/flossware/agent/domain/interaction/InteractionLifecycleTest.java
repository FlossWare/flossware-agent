package org.flossware.agent.domain.interaction;

import static org.junit.jupiter.api.Assertions.*;

import org.flossware.agent.domain.id.Identifiers.SessionId;
import org.flossware.agent.domain.message.Message;
import org.flossware.agent.domain.message.MessageRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InteractionLifecycleTest {

    private static Interaction start() {
        return Interaction.start(SessionId.random());
    }

    @Test
    void startBeginsInStartedWithEmptyMessages() {
        Interaction i = start();
        assertEquals(InteractionState.STARTED, i.state());
        assertTrue(i.messages().isEmpty());
        assertNull(i.completedAt());
    }

    @Test
    void startedCanCompleteFailOrCancel() {
        assertDoesNotThrow(() -> start().complete());
        assertDoesNotThrow(() -> start().fail());
        assertDoesNotThrow(() -> start().cancel());
    }

    @Test
    void terminalStatesRejectFurtherTransitionsAndAppend() {
        Message msg = Message.of(MessageRole.USER, "x");
        for (Interaction terminal : new Interaction[] {
                start().complete(), start().fail(), start().cancel() }) {
            assertTrue(terminal.state().isTerminal());
            assertNotNull(terminal.completedAt());
            assertThrows(IllegalStateException.class, terminal::complete);
            assertThrows(IllegalStateException.class, terminal::fail);
            assertThrows(IllegalStateException.class, terminal::cancel);
            assertThrows(IllegalStateException.class, () -> terminal.appendMessage(msg));
        }
    }

    @Test
    void appendPreservesOrderAndIdentity() {
        Interaction i = start();
        Message m1 = Message.of(MessageRole.USER, "one");
        Message m2 = Message.of(MessageRole.ASSISTANT, "two");
        Interaction updated = i.appendMessage(m1).appendMessage(m2);
        assertEquals(i.id(), updated.id());
        assertEquals(2, updated.messages().size());
        assertEquals(m1, updated.messages().get(0));
        assertEquals(m2, updated.messages().get(1));
        assertEquals(0, i.messages().size()); // original unchanged
    }

    @Test
    void appendRejectsNullMessage() {
        assertThrows(NullPointerException.class, () -> start().appendMessage(null));
    }

    @ParameterizedTest
    @CsvSource({
            "STARTED, COMPLETED, true",
            "STARTED, FAILED, true",
            "STARTED, CANCELLED, true",
            "STARTED, STARTED, false",
            "COMPLETED, FAILED, false",
            "COMPLETED, STARTED, false",
            "FAILED, COMPLETED, false",
            "CANCELLED, STARTED, false"
    })
    void transitionMatrix(InteractionState from, InteractionState to, boolean allowed) {
        assertEquals(allowed, from.canTransitionTo(to));
    }

    @Test
    void nullSessionIdRejected() {
        assertThrows(NullPointerException.class, () -> Interaction.start(null));
    }
}
