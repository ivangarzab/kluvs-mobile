#!/bin/bash
# PreToolUse hook: kills idle Gradle & Kotlin daemons before new Gradle builds.
# Prevents RAM bloat from daemon accumulation across multiple Claude instances.
# Exit 0 = allow (always allows; cleanup is a side effect).

set -uo pipefail

INPUT=$(cat)
CMD=$(echo "$INPUT" | jq -r '.tool_input.command // empty')

# Fast path: only act on Gradle commands
echo "$CMD" | grep -qE '(gradlew|gradle)\b' || exit 0

KILLED=0
RAM_FREED_KB=0

# Gradle daemons: use Gradle's own BUSY/IDLE bookkeeping via --status instead of
# a %cpu snapshot. An instantaneous CPU read can misclassify a daemon that's
# mid-build (GC pause, I/O wait) as idle, which matters more here since several
# daemons often run concurrently across parallel Claude sessions.
GRADLEW="${CLAUDE_PROJECT_DIR:-.}/gradlew"
if [ -x "$GRADLEW" ]; then
	while IFS= read -r line; do
		pid=$(echo "$line" | awk '{print $1}')
		status=$(echo "$line" | awk '{print $2}')
		[[ "$pid" =~ ^[0-9]+$ ]] || continue
		[ "$status" = "IDLE" ] || continue

		rss_kb=$(ps -o rss= -p "$pid" 2>/dev/null | tr -d ' ')
		if kill -SIGTERM "$pid" 2>/dev/null; then
			KILLED=$((KILLED + 1))
			RAM_FREED_KB=$((RAM_FREED_KB + ${rss_kb:-0}))
		fi
	done < <("$GRADLEW" --status --console=plain --quiet 2>/dev/null || true)
fi

# Kotlin compile daemons don't expose a BUSY/IDLE status via CLI, so keep the
# %cpu heuristic for those only.
CPU_IDLE_THRESHOLD=3.0
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
done < <(ps -eo pid,%cpu,rss,command 2>/dev/null | grep -E 'KotlinCompileDaemon' | grep -v grep || true)

if [ "$KILLED" -gt 0 ]; then
	RAM_MB=$((RAM_FREED_KB / 1024))
	MSG="Cleaned up $KILLED idle Gradle/Kotlin daemon(s), freed ~${RAM_MB}MB of RAM."
	# Plain stdout from a successful PreToolUse hook is swallowed by the UI;
	# systemMessage is what actually surfaces it to the user.
	jq -n --arg msg "$MSG" '{systemMessage: $msg}'
fi

exit 0
