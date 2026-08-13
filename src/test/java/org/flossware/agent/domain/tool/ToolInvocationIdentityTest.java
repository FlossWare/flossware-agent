package org.flossware.agent.domain.tool;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.flossware.agent.domain.id.Identifiers.InteractionId;
import org.flossware.agent.domain.id.Identifiers.ToolId;
import org.flossware.agent.domain.id.Identifiers.ToolInvocationId;
import org.junit.jupiter.api.Test;

class ToolInvocationIdentityTest {

    private static final ToolId TOOL = ToolId.of("echo");
    private static final InteractionId INTERACTION = InteractionId.of("ix-1");

    @Test
    void concurrentIdenticalInvocationsHaveDistinctIds() {
        ToolInvocation a = ToolInvocation.start(TOOL, INTERACTION, "{}");
        ToolInvocation b = ToolInvocation.start(TOOL, INTERACTION, "{}");
        assertNotEquals(a.id(), b.id());
        assertNotEquals(a, b);
        assertEquals(TOOL, a.toolId());
        assertEquals(TOOL, b.toolId());
        assertEquals("{}", a.arguments());
        assertEquals("{}", b.arguments());
    }

    @Test
    void completePreservesIdentity() {
        ToolInvocation started = ToolInvocation.start(TOOL, INTERACTION, "{\"x\":1}");
        ToolInvocation completed = started.complete("ok", true);
        assertEquals(started.id(), completed.id());
        assertEquals(started, completed);
        assertTrue(completed.isComplete());
        assertEquals("ok", completed.result().orElseThrow());
        assertTrue(completed.success());
        assertThrows(IllegalStateException.class, () -> completed.complete("again", false));
    }

    @Test
    void restoreRoundTripPreservesIdentityAndFields() {
        ToolInvocationId id = ToolInvocationId.of("inv-1");
        Instant invoked = Instant.parse("2024-05-01T12:00:00Z");
        Instant completed = Instant.parse("2024-05-01T12:00:01Z");
        ToolInvocation restored = ToolInvocation.restore(
                id, TOOL, INTERACTION, "{\"a\":true}", invoked, "result", true, completed);
        assertEquals(id, restored.id());
        assertEquals(TOOL, restored.toolId());
        assertEquals(INTERACTION, restored.interactionId());
        assertEquals("{\"a\":true}", restored.arguments());
        assertEquals(invoked, restored.invokedAt());
        assertEquals("result", restored.result().orElseThrow());
        assertTrue(restored.success());
        assertEquals(completed, restored.completedAt().orElseThrow());
        assertTrue(restored.isComplete());

        ToolInvocation sameId = ToolInvocation.restore(
                id, TOOL, INTERACTION, "other", invoked, null, false, null);
        assertEquals(restored, sameId);
        assertEquals(restored.hashCode(), sameId.hashCode());
    }

    @Test
    void restoreRejectsNullRequiredFields() {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        assertThrows(NullPointerException.class,
                () -> ToolInvocation.restore(null, TOOL, INTERACTION, "{}", now, null, false, null));
        assertThrows(NullPointerException.class,
                () -> ToolInvocation.restore(ToolInvocationId.of("i"), null, INTERACTION, "{}", now, null, false, null));
        assertThrows(NullPointerException.class,
                () -> ToolInvocation.restore(ToolInvocationId.of("i"), TOOL, null, "{}", now, null, false, null));
        assertThrows(NullPointerException.class,
                () -> ToolInvocation.restore(ToolInvocationId.of("i"), TOOL, INTERACTION, "{}", null, null, false, null));
    }

    @Test
    void toolInvocationIdValidation() {
        assertThrows(IllegalArgumentException.class, () -> ToolInvocationId.of(""));
        assertThrows(NullPointerException.class, () -> ToolInvocationId.of(null));
        assertEquals(ToolInvocationId.of("x"), ToolInvocationId.of("x"));
    }
}
