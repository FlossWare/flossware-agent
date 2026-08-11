package org.flossware.agent.port;

import org.flossware.agent.domain.tool.ToolDefinition;
import org.flossware.agent.domain.tool.ToolInvocation;

public interface ToolExecutor {
    ToolInvocation execute(ToolDefinition definition, ToolInvocation invocation);
}
