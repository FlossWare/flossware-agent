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

        // Field assertions — do not rely on equals() alone (identity-based equality).
        assertEquals(id, restored.id());
        assertEquals(TOOL, restored.toolId());
        assertEquals(INTERACTION, restored.interactionId());
        assertEquals("{\"a\":true}", restored.arguments());
        assertEquals(invoked, restored.invokedAt());
        assertEquals("result", restored.result().orElseThrow());
        assertTrue(restored.success());
        assertEquals(completed, restored.completedAt().orElseThrow());
        assertTrue(restored.isComplete());
    }

    @Test
    void equalsIsIdentityOnlyNotStructural() {
        ToolInvocationId id = ToolInvocationId.of("inv-same");
        Instant invoked = Instant.parse("2024-05-01T12:00:00Z");
        ToolInvocation a = ToolInvocation.restore(
                id, TOOL, INTERACTION, "{\"a\":1}", invoked, "r1", true, invoked);
        ToolInvocation b = ToolInvocation.restore(
                id, TOOL, INTERACTION, "{\"a\":2}", invoked, "r2", false, null);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertEquals("{\"a\":1}", a.arguments());
        assertEquals("{\"a\":2}", b.arguments());
        assertTrue(a.isComplete());
        assertFalse(b.isComplete());
    }

    @Test
    void restoreAllowsCompletedWithNullResultPayload() {
        ToolInvocationId id = ToolInvocationId.of("inv-empty");
        Instant invoked = Instant.parse("2024-05-01T12:00:00Z");
        Instant completed = Instant.parse("2024-05-01T12:00:01Z");
        ToolInvocation restored = ToolInvocation.restore(
                id, TOOL, INTERACTION, "{}", invoked, null, true, completed);
        assertTrue(restored.isComplete());
        assertTrue(restored.success());
        assertTrue(restored.result().isEmpty());
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
