# AGENTS.md

## 固定规则

1. 先制定 `plan`，明确步骤与范围后再执行。
2. 严格限制变更范围，只处理当前明确要求的内容，不扩展到无关文件或任务。
3. 先说明再动手，在开始修改前简要解释拟执行的操作。
4. 主动检查并更新 `plan` 状态，确保计划和实际进度一致，不要遗漏已完成项。
5. 更改内容后必须严格测试，测试用例要贴近真实浏览器和真实业务提交场景，不能只用“刚好能过”的简化参数代替实际交互；如果影响面较大，还要补充回归验证。
6. 提交前必须总结，说明修改内容、影响范围以及验证结果。
7. 危险命令必须先确认，涉及删除、重置、强制覆盖等操作前必须征得同意。

## Skills

A skill is a set of local instructions to follow that is stored in a `SKILL.md` file. Below is the list of skills that can be used. Each entry includes a name, description, and file path so you can open the source for full instructions when using a specific skill.

### Available skills

- skill-creator: Guide for creating effective skills. This skill should be used when users want to create a new skill (or update an existing skill) that extends Codex's capabilities with specialized knowledge, workflows, or tool integrations. (file: C:/Users/Administrator/AppData/Local/JetBrains/IntelliJIdea2026.1/aia/codex/skills/.system/skill-creator/SKILL.md)
- skill-installer: Install Codex skills into $CODEX_HOME/skills from a curated list or a GitHub repo path. Use when a user asks to list installable skills, install a curated skill, or install a skill from another repo (including private repos). (file: C:/Users/Administrator/AppData/Local/JetBrains/IntelliJIdea2026.1/aia/codex/skills/.system/skill-installer/SKILL.md)

### How to use skills

- Discovery: The list above is the skills available in this session (name + description + file path). Skill bodies live on disk at the listed paths.
- Trigger rules: If the user names a skill (with `$SkillName` or plain text) OR the task clearly matches a skill's description shown above, you must use that skill for that turn. Multiple mentions mean use them all. Do not carry skills across turns unless re-mentioned.
- Missing/blocked: If a named skill isn't in the list or the path can't be read, say so briefly and continue with the best fallback.
- How to use a skill (progressive disclosure):
  1. After deciding to use a skill, open its `SKILL.md`. Read only enough to follow the workflow.
  2. When `SKILL.md` references relative paths (e.g. `scripts/foo.py`), resolve them relative to the skill directory listed above first, and only consider other paths if needed.
  3. If `SKILL.md` points to extra folders such as `references/`, load only the specific files needed for the request; don't bulk-load everything.
  4. If `scripts/` exist, prefer running or patching them instead of retyping large code blocks.
  5. If `assets/` or templates exist, reuse them instead of recreating from scratch.
- Coordination and sequencing:
  - If multiple skills apply, choose the minimal set that covers the request and state the order you'll use them.
  - Announce which skill(s) you're using and why (one short line). If you skip an obvious skill, say why.
- Context hygiene:
  - Keep context small: summarize long sections instead of pasting them; only load extra files when needed.
  - Avoid deep reference-chasing: prefer opening only files directly linked from `SKILL.md` unless you're blocked.
  - When variants exist (frameworks, providers, domains), pick only the relevant reference file(s) and note that choice.
- Safety and fallback: If a skill can't be applied cleanly (missing files, unclear instructions), state the issue, pick the next-best approach, and continue.
