#!/bin/bash
# PreToolUse hook: kills idle Gradle & Kotlin daemons before new Gradle builds.
# Prevents RAM bloat from daemon accumulation across multiple Claude instances.
# Exit 0 = allow (always allows; cleanup is a side effect).

set -uo pipefail

INPUT=$(cat)
CMD=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

# Fast path: only act on Gradle commands
echo "$CMD" | grep -qE '(gradlew|gradle)\b' || exit 0

CPU_IDLE_THRESHOLD=3.0
KILLED=0
RAM_FREED_KB=0

while IFS= read -r line; do
	[ -z "$line" ] && continue
	pid=$(echo "$line" | awk '{print $1}')
	cpu=$(echo "$line" | awk '{print $2}')
	rss_kb=$(echo "$line" | awk '{print $3}')

	is_idle=$(awk -v cpu="$cpu" -v threshold="$CPU_IDLE_THRESHOLD" 'BEGIN {print (cpu + 0 < threshold + 0) ? "1" : "0"}')
	if [ "$is_idle" = "1" ]; then
		if kill -SIGTERM "$pid" 2>/dev/null; then
			KILLED=$((KILLED + 1))
			RAM_FREED_KB=$((RAM_FREED_KB + rss_kb))
		fi
	fi
done < <(ps -eo pid,%cpu,rss,command 2>/dev/null | grep -E 'GradleDaemon|KotlinCompileDaemon' | grep -v grep || true)

if [ "$KILLED" -gt 0 ]; then
	RAM_MB=$((RAM_FREED_KB / 1024))
	echo "Cleaned up $KILLED idle Gradle/Kotlin daemon(s), freed ~${RAM_MB}MB of RAM."
fi

exit 0
