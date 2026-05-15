---
title: Hermes Agent Workspace - Multi-Agent Team Setup
aliases: [Hermes Workspace, Hermes Agent Swarm]
tags:
  - hermes-agent
  - multi-agent
  - workspace
  - swarm
  - status/active
  - type/tutorial
source:
  - "https://www.youtube.com/watch?v=fUem4KS572c"
author: YouTube Creator (French AI content channel)
created: 2025-05-15
updated: 2025-05-15
description: Step-by-step guide to building an AI agent team using Hermes Agent Workspace - operators, task pipelines, scheduling, and swarm mode.
level: intermediate
stars: 3
---

# Hermes Agent Workspace - Multi-Agent Team Setup

> Hermes Agent Workspace is the native Web UI for Hermes Agent (by Nous Research). It provides a visual interface for chat, terminal, memory management, skill editing, and — critically — **multi-agent orchestration**. This note covers the full workflow: deploying on VPS, creating specialized sub-agents (operators), managing task pipelines, scheduling cron jobs, and launching autonomous swarm missions.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Deployment on VPS](#deployment-on-vps)
- [Step-by-Step Setup](#step-by-step-setup)
  - [1. Main Agent Configuration](#1-main-agent-configuration)
  - [2. Creating Operators](#2-creating-operators)
  - [3. Task Management Pipeline](#3-task-management-pipeline)
  - [4. Task Scheduling (Cron)](#4-task-scheduling-cron)
  - [5. Swarm / Mission Mode](#5-swarm--mission-mode)
  - [6. Warm Agents (Autopilot)](#6-warm-agents-autopilot)
- [The 4-Agent YouTube Team Example](#the-4-agent-youtube-team-example)
- [Security Considerations](#security-considerations)
- [Comparison: Three Orchestration Modes](#comparison-three-orchestration-modes)
- [Key Takeaways](#key-takeaways)
- [References](#references)

---

## Architecture Overview

Hermes Workspace organizes AI agents in a hierarchy:

```
+------------------------------------------+
|          MAIN AGENT (Orchestrator)        |
|  - Receives user instructions            |
|  - Coordinates sub-agents                |
|  - Manages task lifecycle                |
+------------------------------------------+
         |              |              |
    +---------+    +---------+    +---------+
    |Operator A|    |Operator B|    |Operator C|
    |(Scout)   |    |(Forge)   |    |(Pulse)   |
    |Research  |    |Scripting |    |SEO/Optim.|
    +---------+    +---------+    +---------+
```

**Three orchestration modes:**

| Mode | How it works | Best for |
|------|-------------|----------|
| **Operators** | You manually create sub-agents and assign tasks | Controlled, step-by-step workflows |
| **Swarm/Mission** | Main agent auto-discovers agents, breaks down mission, assigns work | Complex end-to-end missions |
| **Warm Agents** | Autonomous agents on autopilot, no conversation needed | Recurring background tasks |

---

## Deployment on VPS

### Why VPS (Not Local Machine)

> ⚠️ **Security first**: AI agents have filesystem access. Prompt injection could leak passwords, photos, files. Always isolate on a VPS with no personal data.

```
+------------------+     +------------------+
|   Local Machine  |     |   VPS (Hostinger)|
|   - Personal data|     |   - Hermes only  |
|   - Passwords    |     |   - No personal  |
|   - Photos       |     |     files        |
+------------------+     +------------------+
```

### Recommended Specs

| Component | Minimum | Recommended |
|-----------|---------|-------------|
| CPU | 2 cores | 2+ cores (KVM) |
| RAM | 4 GB | **8 GB** |
| Storage | 20 GB SSD | 40 GB+ SSD |
| Provider | Any VPS | Hostinger (1-click deploy) |

### Deployment Steps

1. **Choose Hostinger KVM2 plan** (2 CPU, 8 GB RAM)
2. **Select server location** (closest to your audience)
3. **Set admin password** — save securely
4. **Provide LLM API key** — Anthropic, OpenAI, OpenRouter, Mistral, etc.
5. **One-click deploy** via Hostinger's Docker marketplace

> 💡 Hostinger offers a 30-day trial. Use coupon `GO MSI` for 10% off (new customers only).

---

## Step-by-Step Setup

### 1. Main Agent Configuration

The main agent is your orchestrator. Define its role with a structured prompt:

```
You are the Lead Agent Manager and head of my [DOMAIN] team.

MY PROFILE:
- [Your profession]
- [Your tools/platforms]
- [Your goals]

YOUR MISSION:
1. Confirm connection and operational status
2. Propose N specialized sub-agents with:
   - Agent name
   - Specific role
   - Main mission
3. Create an action plan with priority tasks
4. Respond in a structured and direct manner
```

**Key principle**: The main agent should understand your business context, not just be a generic assistant. Give it your real profile and goals.

### 2. Creating Operators

Operators are specialized sub-agents. Each gets:

| Field | Description | Example |
|-------|-------------|---------|
| Name | Identifier (emoji allowed) | 🔍 Scout |
| Model | LLM to use | Anthropic Claude |
| Description | What it specializes in | "Competitive intelligence on AI topics" |
| Mission Prompt | Detailed instructions | See below |

**Mission prompt template:**

```
ROLE: [Specific role description]

TOPICS TO MONITOR:
- [Topic 1]
- [Topic 2]
- [Topic 3]

OBJECTIVE:
- [What it should achieve]

DELIVERABLE:
- [Expected output format]
- [Where to save results]

CONSTRAINTS:
- [Language, tone, format requirements]
```

> ✅ **Best practice**: Ask the main agent to generate optimized prompts for each sub-agent. It knows the system best and can tailor prompts to Workspace's capabilities.

### 3. Task Management Pipeline

Tasks flow through a Kanban-style pipeline:

```
  DRAFT    →    READY    →    IN PROGRESS    →    REVIEW    →    DONE
   (plan)       (queued)      (executing)       (checking)     (complete)
     |                                                              |
     |          +---- BLOCKED (needs attention) ----+              |
     +----------→                                   ←-------------+
```

**Creating a task:**

| Field | Purpose | Best Practice |
|-------|---------|---------------|
| Title | Short description | Action-oriented: "Analyze top 5 trending AI topics" |
| Description | Detailed instructions | Include objective, context, expected deliverable |
| Priority | Urgency level | Highest for time-sensitive tasks |
| Assignee | Which operator | Match to agent specialty |
| Tags | Categorization | e.g., `youtube`, `seo`, `research` |

**Workflow:**
1. Create tasks in **Draft** column
2. Move to **Ready** when ready for execution
3. Agent picks up and moves to **In Progress**
4. Results go to **Review** for quality check
5. Approved → **Done**

> ⚠️ **Pitfall**: Don't just chat with the main agent for everything. As sessions get long, the agent loses focus. Use specialized operators for specific tasks — they develop deep expertise in their domain.

### 4. Task Scheduling (Cron)

Automate recurring tasks with scheduled execution:

```
+---------------------------+
|   SCHEDULED TASKS          |
|                            |
|  [9:00 AM Daily]           |
|  → Scout: Research topics  |
|  → Forge: Write script     |
|  → Pulse: Optimize SEO     |
|                            |
|  [After video published]   |
|  → Bridge: Engage community|
+---------------------------+
```

**Setup process:**
1. Define the recurring schedule (e.g., every day at 9:00 AM)
2. Specify which agents participate
3. Set the task sequence
4. Hermes handles the rest automatically

> 💡 **Use case**: A YouTube creator can have agents research topics, write scripts, and optimize SEO every morning — ready for review when they wake up.

### 5. Swarm / Mission Mode

Swarm mode lets the main agent autonomously manage the entire workflow:

```
TRADITIONAL (Operators):
  You → assign task to Agent A → review → assign to Agent B → review → ...

SWARM (Mission):
  You → define mission → Main Agent auto-manages:
    → Breaks down mission into sub-tasks
    → Assigns to appropriate agents
    → Monitors progress
    → Handles dependencies
    → Synthesizes final result
```

**How to launch:**
1. Go to **Missions** section
2. Select **Auto** mode (system finds agents automatically)
3. Define the end-to-end mission
4. Launch — Hermes handles orchestration

**Mission prompt example:**
```
Create a complete YouTube video production pipeline:
1. Scout: Research top 5 trending AI topics, score them, pick the best
2. Forge: Write a full video script based on the chosen topic
3. Pulse: Generate optimized title, description, tags, thumbnail ideas
4. Bridge: Create community engagement strategy for the video

Return all deliverables in organized files.
```

> ⚠️ **Activation required**: Swarm agents must be activated first via terminal. Without activation, tasks show as "blocked" because agents lack execution permissions in the Docker container.

### 6. Warm Agents (Autopilot)

Warm agents are autonomous agents that execute without conversation:

```
WARM AGENT vs OPERATOR:

Operator:  You chat → it responds → you guide → it executes
Warm Agent: System triggers → it auto-executes → returns result
```

**Setting up a warm agent:**

| Field | Description |
|-------|-------------|
| ID | Unique identifier |
| Name | Display name |
| Model | LLM to use |
| Specialty | Short description of expertise |
| Mission | Full prompt with instructions |
| File Access | Write permissions for output files |

**Common warm agent types:**

```
+-------------------+     +-------------------+     +-------------------+
|    RESEARCHER     |     |     WRITER        |     |    REVIEWER       |
|                   |     |                   |     |                   |
| - Tech monitoring |     | - Script writing  |     | - Quality check   |
| - Trend analysis  |     | - Content creation|     | - Consistency     |
| - Source collection|    | - Documentation   |     | - Accuracy review |
+-------------------+     +-------------------+     +-------------------+
```

**File permission fix** (common issue):
```bash
# Docker containers run as root by default
# Change ownership to workspace user for file write access
chown -R workspace:workspace /path/to/output/directory
```

---

## The 4-Agent YouTube Team Example

The video demonstrates a complete YouTube content production team:

| Agent | Role | Tasks |
|-------|------|-------|
| 🔍 **Scout** | Content Intelligence | Monitor trends, analyze top 20 channels, score topics (0-100), deliver top 5 |
| 🔨 **Forge** | Script Architect | Transform topics into structured video scripts, section-by-section breakdown |
| 📊 **Pulse** | Growth Strategy | SEO optimization, title/description/tags, thumbnail A/B variations |
| 🌉 **Bridge** | Community Manager | Comment engagement, collaboration requests, performance analysis |

**Pipeline flow:**

```
Scout (Research)     Forge (Create)      Pulse (Optimize)     Bridge (Engage)
     |                    |                     |                    |
[Topic Discovery] → [Script Writing] → [SEO Optimization] → [Community]
     |                    |                     |                    |
 Top 5 Topics         Full Script         Title/Tags/Desc      Engagement
 with Scores          Section by Section  3 Variations         Strategy
```

**Scout output example:**

| Topic | Score | Test Angle | Real Angle |
|-------|-------|-----------|------------|
| AI Video Creation A-Z | 92 | Can AI fully automate YouTube? | Tools + workflow for AI video |
| Claude vs GPT Comparison | 88 | Which is better? | Task-specific comparison |
| AI Agents for Business | 85 | What can they do? | Real ROI case studies |

---

## Security Considerations

| Risk | Mitigation |
|------|-----------|
| Prompt injection leaking data | Deploy on VPS with no personal files |
| API key exposure | Use environment variables, never hardcode |
| Unrestricted agent access | Limit filesystem permissions per agent |
| Internet exposure | Use auth, HTTPS, restrict network bindings |
| Docker container escape | Keep Docker updated, use non-root user |

**Security checklist:**

```
✅ VPS has NO personal data (photos, passwords, documents)
✅ API keys stored as environment variables
✅ Workspace behind authentication (not public)
✅ HTTPS enabled for remote access
✅ Regular dependency updates
✅ Agent file permissions are minimal
✅ Firewall restricts unnecessary ports
```

---

## Comparison: Three Orchestration Modes

```
                    CONTROL                 AUTONOMY               COMPLEXITY
                    ~~~~~~~                 ~~~~~~~~~               ~~~~~~~~~~

Operators:     ████████████░░░░░░     ████░░░░░░░░░░░░░░     ████░░░░░░░░░░░░░░
               You assign everything   Agents wait for you    Easy to understand

Swarm:         ██████░░░░░░░░░░░░     ████████████░░░░░░     ██████████░░░░░░░░
               You define mission      Agents self-manage    Setup + activation

Warm Agents:   ██░░░░░░░░░░░░░░░░     ██████████████████     ██████████████████
               Set once, forget       Fully autonomous      File perms + Docker
```

| Decision Factor | Choose Operators | Choose Swarm | Choose Warm Agents |
|----------------|-----------------|--------------|-------------------|
| Need fine control | ✅ | ❌ | ❌ |
| End-to-end mission | ❌ | ✅ | ❌ |
| Recurring background | ❌ | ❌ | ✅ |
| Multi-step pipeline | Manual | Auto | Auto |
| Human in the loop | Always | Start/end | Never |
| Setup complexity | Low | Medium | High |

---

## Key Takeaways

1. **Specialization over versatility** — Create multiple expert agents rather than one generalist. Agents are "free" to create.
2. **VPS isolation is non-negotiable** — Never run AI agents with filesystem access on your personal machine.
3. **Use the right orchestration mode** — Operators for control, Swarm for complex missions, Warm Agents for recurring tasks.
4. **Structured prompts matter** — Define clear roles, objectives, deliverables, and constraints for each agent.
5. **Task pipeline prevents chaos** — Draft → Ready → In Progress → Review → Done keeps work organized.
6. **Scheduling enables passive productivity** — Set up cron jobs so agents work while you sleep.
7. **File permissions are a common gotcha** — Docker containers need explicit write permissions (`chown`).

---

## References

- [I Created My AI Agent Team with Hermes Workspace (YouTube)](https://www.youtube.com/watch?v=fUem4KS572c)
- [Hermes Agent Workspace V2 Complete Guide](https://aisuccesslabjuliangoldie.com/blog/hermes-agent-workspace/)
- [Hermes Agent Swarm Feature Guide](https://juliangoldie.com/hermes-agent-swarm/)
- [Hermes Agent GitHub (NousResearch)](https://github.com/nousresearch/hermes-agent)
- [Hostinger Hermes Workspace One-Click Deploy](https://www.hostinger.com/vps/docker/hermes-workspace)

## Related Notes

- [[Hermes Agent]]
- [[Multi-Agent Systems]]
- [[AI Agent Security]]
