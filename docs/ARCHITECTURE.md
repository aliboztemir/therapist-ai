# Therapist-AI — Master Architecture, Business Workflow & Technical Design Document

## Project Vision

Therapist-AI is an AI-powered psychotherapy-inspired conversational platform.

The goal is **not** to replace professional therapists or provide diagnosis.

The goal is to create a structured reflective conversation system that:

- Understands the user's emotional state
- Detects risk situations
- Maintains long-term context
- Uses therapy-inspired approaches
- Produces safe, explainable, and context-aware responses
- Evolves into a personalized psychological support platform

---

## High-Level Architecture

| Dimension          | Choice                                    |
|--------------------|-------------------------------------------|
| Style              | Modular Monolith                          |
| Internal structure | Hexagonal Architecture (Ports & Adapters) |
| Design influence   | Domain-Driven Design                      |
| Processing model   | Event-Driven Internal Processing          |
| Orchestration      | AI-first                                  |

### Current Stack

| Layer        | Technology            |
|--------------|-----------------------|
| Language     | Java 25               |
| Framework    | Spring Boot 4         |
| AI Framework | Spring AI             |
| AI Provider  | OpenAI                |
| Database     | PostgreSQL            |
| Security     | Spring Security + JWT |

### Future Stack Additions

| Capability          | Technology                           |
|---------------------|--------------------------------------|
| Semantic search     | PgVector                             |
| Knowledge retrieval | Hybrid RAG                           |
| Multi-provider AI   | OpenAI, Anthropic, Gemini, Ollama    |
| Operator tooling    | Therapist Dashboard, Admin Dashboard |

---

## Module Structure

### User Module

**Responsibilities:** Registration · Login · Authentication · Authorization · User Profile

**Core Domain — `AppUser`**

| Field          | Type    |
|----------------|---------|
| `id`           | UUID    |
| `username`     | String  |
| `email`        | String  |
| `fullName`     | String  |
| `passwordHash` | String  |
| `role`         | Enum    |
| `createdAt`    | Instant |
| `updatedAt`    | Instant |

---

### Security Module

**Responsibilities:** Form-based authentication · Authorization · User profile resolution · Legal consent management ·
Cloudflare Turnstile bot protection

**Public API — `UserProfileService`** *(used by other modules — never inject `AppUserRepository` directly)*

| Method                               | Description                                             |
|--------------------------------------|---------------------------------------------------------|
| `loadProfile(UUID userId)`           | Returns `UserProfile`; safe fallback on any error       |
| `isOnboardingCompleted(UUID userId)` | Returns whether the user has accepted the legal consent |

**Public API — `ConsentService`**

| Method                        | Description                                       |
|-------------------------------|---------------------------------------------------|
| `isConsentGiven(UUID userId)` | Reads `onboarding_completed` from DB              |
| `acceptConsent(UUID userId)`  | Sets `onboarding_completed = true` via direct SQL |

**Endpoints:**

| Method | Path                  | Description                                                 |
|--------|-----------------------|-------------------------------------------------------------|
| GET    | `/login`              | Login page                                                  |
| POST   | `/login`              | Login form submit                                           |
| GET    | `/register`           | Registration page                                           |
| POST   | `/register`           | Registration form submit                                    |
| GET    | `/api/consent/status` | Returns `{"consentGiven": bool}` for the authenticated user |
| POST   | `/api/consent/accept` | Accepts legal consent — sets `onboarding_completed = true`  |

---

### Chat Module

**Responsibilities:** Main orchestration · Session lifecycle · Conversation ownership · Message persistence

**Main class:** `ChatOrchestrator`

The Chat module is the central coordinator — every other module is called through it.

---

### AI Module

**Responsibilities:** LLM abstraction · OpenAI integration · Future multi-provider support

**Main abstraction:** `AIProviderGateway`

> **Rule:** No module may call OpenAI directly. All calls must go through `AIProviderGateway`.

**Current provider:** OpenAI

**Planned providers:** OpenAI · Anthropic · Gemini · Ollama

---

### Analysis Module

**Purpose:** Analyse incoming user messages.

**Output:** `MessageAnalysis`

| Field                  | Description                                      |
|------------------------|--------------------------------------------------|
| `primaryEmotion`       | Dominant detected emotion                        |
| `emotions`             | All detected emotions                            |
| `themes`               | Thematic areas (grief, conflict, work stress, …) |
| `sentiment`            | Positive / Neutral / Negative                    |
| `emotionalIntensity`   | 0.0 – 1.0                                        |
| `adviceSeeking`        | Boolean signal                                   |
| `resistance`           | Boolean signal                                   |
| `boundaryTest`         | Boolean signal                                   |
| `patternDetected`      | Boolean signal                                   |
| `insightDetected`      | Boolean signal                                   |
| `crisisSignalDetected` | Boolean signal                                   |
| `confidence`           | 0.0 – 1.0                                        |

> **Rule:** Analysis **never** decides therapy mode. Analysis only *describes* the message.

---

### Risk Module

**Purpose:** Detect crisis and safety situations.

**Output:** `RiskDecision`

**Risk Levels:**

| Level      | Meaning                              |
|------------|--------------------------------------|
| `NONE`     | No risk detected                     |
| `LOW`      | Minor signals, monitor               |
| `MODERATE` | Moderate concern, heightened care    |
| `HIGH`     | Serious risk, safety escalation      |
| `CRISIS`   | Immediate danger, emergency protocol |

**Responsibilities:**

- Suicide detection
- Self-harm detection
- Crisis lock management
- Emergency flow triggering

> **Rule:** Risk detection is always **synchronous**. Risk is never handled asynchronously.

---

### Therapy Module

**Purpose:** Decide therapeutic direction.

**Output:** `TherapyDecision`

#### Mode

| Mode           | Description                            |
|----------------|----------------------------------------|
| `ONBOARDING`   | Collect consent and basic context      |
| `ASSESSMENT`   | Explore and understand the situation   |
| `INTERVENTION` | Deliver a light structured exercise    |
| `RESISTANCE`   | Acknowledge resistance, lower pressure |
| `CLOSURE`      | Summarise and suggest next step        |
| `CRISIS`       | Safety-first crisis response           |
| `META`         | Answer app/process questions, redirect |

#### Stage

| Stage                   | Description                                      |
|-------------------------|--------------------------------------------------|
| `CHECK_IN`              | Opening the session                              |
| `EMOTIONAL_EXPLORATION` | Understanding feelings                           |
| `COGNITIVE_EXPLORATION` | Understanding thoughts                           |
| `PATTERN_DETECTION`     | Identifying recurring patterns                   |
| `REFLECTION`            | Inviting the user to reflect                     |
| `PSYCHOEDUCATION`       | Sharing therapeutic knowledge                    |
| `EXERCISE_SELECTION`    | Choosing an exercise                             |
| `EXERCISE_GUIDANCE`     | Running the exercise                             |
| `REVIEW`                | Reviewing the exercise outcome                   |
| `SAFETY_CHECK`          | Assessing safety                                 |
| `STABILIZATION`         | Grounding and stabilisation after crisis signals |
| `SUMMARY`               | Wrapping up the session                          |
| `NEXT_STEP`             | Suggesting the next action                       |
| `META_RESPONSE`         | Answer app or process-related questions          |

#### Approach

| Approach         | Description                       |
|------------------|-----------------------------------|
| `SUPPORTIVE`     | Empathy-first, validation         |
| `CBT`            | Cognitive Behavioural Therapy     |
| `ACT`            | Acceptance and Commitment Therapy |
| `DBT`            | Dialectical Behaviour Therapy     |
| `SCHEMA`         | Schema Therapy                    |
| `PSYCHODYNAMIC`  | Psychodynamic exploration         |
| `CRISIS_SUPPORT` | Crisis-focused support            |

#### Goal

| Goal                      | Description                               |
|---------------------------|-------------------------------------------|
| `ASK_CHECK_IN`            | Open the session with a check-in question |
| `VALIDATE`                | Validate the user's feelings              |
| `EXPLORE_EMOTION`         | Deepen emotional exploration              |
| `EXPLORE_THOUGHT`         | Explore underlying thoughts               |
| `REFLECT_PATTERN`         | Surface a recurring pattern               |
| `ASK_CLARIFYING_QUESTION` | Gather more context                       |
| `SUMMARIZE`               | Summarise the session                     |
| `PSYCHOEDUCATE`           | Share therapeutic knowledge               |
| `GUIDE_EXERCISE`          | Guide a therapeutic exercise              |
| `SET_BOUNDARY`            | Gently set conversational boundaries      |
| `REDIRECT`                | Redirect off-topic conversations          |
| `CRISIS_RESPONSE`         | Deliver a crisis-safe response            |
| `META_RESPONSE`           | Answer process or app-related questions   |

---

### Memory Module

**Purpose:** Store user-specific long-term context.

> **Rule:** Memory is **not** chat history.

**Examples of memory items:**

- Recurring fears ("fear of abandonment")
- Relationship conflicts ("ongoing conflict with father")
- Important life events ("job loss last year")
- Recurring beliefs ("I am not good enough")
- Personal goals ("I want to stop catastrophising")

**Memory Types:** `BELIEF` · `FEAR` · `GOAL` · `STRESSOR` · `TRIGGER` · `RELATIONSHIP` · `PERSON` · `COPING_MECHANISM` ·
`THERAPY_TOPIC` · `EVENT` · `PREFERENCE`

**Memory Lifecycle:**

```
Extraction → Validation → Persistence → Retrieval
```

---

### Knowledge Module

**Purpose:** Provide therapy knowledge to the prompt.

> **Rule:** Knowledge is **not** memory.

**Examples of knowledge chunks:**

- CBT techniques (cognitive restructuring, thought records)
- ACT exercises (defusion, values clarification)
- DBT grounding (TIPP, STOP skills)
- Psychoeducation (fight/flight response, window of tolerance)
- Crisis support guidance (safety planning, de-escalation)

**Current retrieval:** Keyword-based

**Future retrieval:** Hybrid RAG · PgVector · Embeddings · Semantic Search

---

### Prompt Module

**Purpose:** Build the final prompt from all context signals.

**Input:**

| Source           | Object                  |
|------------------|-------------------------|
| Analysis module  | `MessageAnalysis`       |
| Risk module      | `RiskDecision`          |
| Therapy module   | `TherapyDecision`       |
| Memory module    | `MemorySnapshot`        |
| Knowledge module | `KnowledgeSearchResult` |
| Conversation     | `RecentHistory`         |

**Output:** `FinalPrompt`

**Prompt Layers (assembled in priority order):**

| Layer             | Type   | Description                       |
|-------------------|--------|-----------------------------------|
| `BASE`            | System | Core identity and behaviour rules |
| `SAFETY`          | System | Safety guardrails from risk level |
| `MODE`            | System | Therapy mode instructions         |
| `STAGE`           | System | Session stage instructions        |
| `APPROACH`        | System | Therapeutic approach instructions |
| `GOAL`            | System | Response goal instructions        |
| `OUTPUT_RULES`    | System | Output format constraints         |
| `USER_PROFILE`    | User   | User context and profile          |
| `ANALYSIS`        | User   | Current message analysis          |
| `RISK`            | User   | Risk context                      |
| `MEMORY`          | User   | Long-term memory snapshot         |
| `SUMMARY`         | User   | Compressed conversation summary   |
| `HISTORY`         | User   | Recent conversation history       |
| `CURRENT_MESSAGE` | User   | The user's current message        |

> **Rule:** The Prompt Module **never** calls OpenAI or any AI provider directly.

---

### Summary Module

**Purpose:** Generate compressed conversation summaries.

> **Rule:** Summary is **not** memory.

**Summary Types:**

| Type              | Description                                       |
|-------------------|---------------------------------------------------|
| `RUNNING_SUMMARY` | Compressed rolling summary of the ongoing session |
| `SESSION_SUMMARY` | End-of-session recap                              |
| `SAFETY_SUMMARY`  | Crisis/safety context summary                     |

**Why summaries exist:**

- Reduce token usage in long conversations
- Compress context without losing important signals
- Keep older history accessible without full history replay

**Summary generation runs asynchronously** — it never blocks the user response.

---

### Events Module

**Purpose:** Internal event-driven architecture for post-processing.

**Current approach:** Spring Application Events + Async Handlers (no Kafka, no RabbitMQ)

**Published Events:**

| Event                             | Trigger                               |
|-----------------------------------|---------------------------------------|
| `MessageProcessedEvent`           | After pipeline context is built       |
| `AssistantResponseGeneratedEvent` | After assistant response is persisted |
| `AIProviderCallCompletedEvent`    | After AI provider call completes      |

---

### Analytics Module

**Purpose:** Business metrics and product intelligence.

**Examples of tracked signals:**

- Therapy mode distribution per session
- Emotion distribution across users
- Risk level distribution
- Daily / weekly active users
- Conversation counts
- AI provider usage and latency

---

### Observability Module

**Purpose:** Technical monitoring, traceability, and diagnostics.

**Examples of tracked signals:**

- End-to-end request latency
- Per-step pipeline latency
- AI provider call timing
- Error rates by type
- Request traceability via `traceId`

**Implementation:**

| Mechanism             | Usage                                      |
|-----------------------|--------------------------------------------|
| `traceId` (UUID)      | Set by `TraceIdFilter`, propagated via MDC |
| MDC                   | Thread-local context for structured logs   |
| Structured log format | `[EVENT_NAME] traceId=X key=value ...`     |
| `LatencyTracker`      | Per-step timing utility                    |
| `LogSanitizer`        | Privacy-safe log output (no PII in logs)   |

---

## Logging Strategy

All log lines follow the pattern:

```
[EVENT_NAME] traceId={} key=value key=value durationMs={}
```

**Log Levels:**

| Level   | When to use                                   |
|---------|-----------------------------------------------|
| `INFO`  | Major pipeline decisions and step completions |
| `DEBUG` | Detailed module outputs, sanitised previews   |
| `WARN`  | Risk escalation, fallback conditions          |
| `ERROR` | Unrecoverable failures with full stack trace  |

**Key structured event names:**

| Event                        | Level       | Description                          |
|------------------------------|-------------|--------------------------------------|
| `CHAT_REQUEST_RECEIVED`      | INFO        | Incoming request logged              |
| `MESSAGE_ANALYSIS_RESULT`    | INFO        | Analysis output fields               |
| `RISK_DECISION_RESULT`       | INFO / WARN | Risk level, types, action            |
| `THERAPY_DECISION_RESULT`    | INFO        | Mode, stage, approach, goal          |
| `MEMORY_EXTRACTION_RESULT`   | DEBUG       | Extracted types and keys             |
| `MEMORY_RETRIEVAL_RESULT`    | DEBUG       | Bucket breakdown, maxImportance      |
| `KNOWLEDGE_RETRIEVAL_RESULT` | INFO        | Chunk titles, categories             |
| `PROMPT_ASSEMBLY_RESULT`     | INFO        | Layer types, chars, estimated tokens |
| `AI_PROVIDER_RESULT`         | DEBUG       | Provider, model, response preview    |
| `CHAT_FLOW_DECISION_SUMMARY` | INFO        | One-line full-turn summary           |
| `DB_WRITE_CHAT_MESSAGE`      | DEBUG       | Per-message persistence              |
| `DB_WRITE_CONVERSATION`      | DEBUG       | Conversation upsert                  |
| `DB_WRITE_MEMORY`            | DEBUG       | Memory item persistence              |
| `DB_WRITE_ANALYTICS`         | DEBUG       | Analytics event persistence          |
| `DB_WRITE_OBSERVABILITY`     | DEBUG       | Observability metric persistence     |

> **Privacy rule:** Raw user messages, full prompts, API keys, and passwords are **never** logged.
> Use `LogSanitizer.previewText()`, `maskEmail()`, `safeUserId()` for any sensitive values.

---

## Synchronous Request Flow

Each user message travels through the following 16-step pipeline.  
Steps marked **[TODO]** are architecturally designed but not yet wired in `ChatOrchestrator`.

```
 0. Controller Request Validation
        │  @NotBlank message, @Size(max=4000), UUID format for conversationId
        │  Turnstile token extracted and forwarded to security layer
        ▼
 1. Security / User Access Guard  (ChatController)
        │  Resolve AuthenticatedUser from Spring Security principal
        │  Check onboarding/consent via UserProfileService
        │  Conversation ownership check (if conversationId supplied)
        │  → 403 AccessDeniedException("ONBOARDING_REQUIRED") if not consented
        ▼
 2. Load Runtime State  (ChatOrchestrator.buildContext)
        │  Conversation load / create
        │  User profile load  (UserProfileService)
        │  Latest summary load  (SummaryQueryService)
        │  AssessmentState load / create  [TODO — stub]
        │  Recent history load
        ▼
 3. Memory Retrieval
        │  MemoryService.retrieve()  →  MemorySnapshot
        │  Synchronous; returns MemorySnapshot.empty() if DB is empty
        ▼
 4. Message Analysis
        │  MessageAnalysisService.analyze()  →  MessageAnalysis
        │  LLM call (temperature=0.1)
        │  Returns MessageAnalysis.safeDefault() on any error — pipeline never breaks
        ▼
 5. Memory Extraction
        │  MemoryService.extract()  →  List<MemoryCandidate>
        │  LLM call (temperature=0.2)
        │  Returns empty list on error — pipeline never breaks
        ▼
 6. Assessment Update  [TODO — stub]
        │  Uses: MessageAnalysis + MemoryCandidate[] + current AssessmentState
        │  Updates AssessmentState section completion flags
        ▼
 7. Risk Detection
        │  RiskDetectionService.assess()  →  RiskDecision
        │  Rule-based, deterministic, synchronous — no LLM
        │  crisisSignalDetected=true → overrides to HIGH
        ▼
 8. Crisis Override
        │  If action == ENTER_CRISIS_MODE | STAY_IN_CRISIS_MODE | ESCALATE_TO_EMERGENCY_GUIDANCE:
        │    → CrisisResponseProvider returns safe fallback text
        │    → Skip steps 9–12, jump directly to Persistence
        ▼
 9. Therapy Decision  [TODO — stub]
        │  Uses: informedConsentSigned + AssessmentState + RiskDecision
        │  Produces: TherapyDecision { mode, stage, approach, goal, reason }
        ▼
10. Knowledge Retrieval
        │  KnowledgeRetrievalService.retrieve()  →  KnowledgeSearchResult
        │  Skipped for: CHECK_IN / GATHER_INFORMATION / ONBOARDING flows  [TODO — skip logic]
        ▼
11. Prompt Assembly
        │  PromptAssemblyService.assemble()  →  FinalPrompt
        │  Stateless — only converts context to prompt layers
        │  conversationHistory passed separately in AIRequest (never duplicated in prompt)
        ▼
12. AI Gateway
        │  AIProviderGateway.generate()  →  AIResponse
        │  OpenAI dependency isolated here only
        │  timeout, retry, and log masking managed here
        ▼
13. Persistence
        │  Append-only: only new user + assistant messages written
        │  MemoryService.persist() — shouldPersist=true candidates only
        │  AssessmentState saved  [TODO]
        ▼
14. Event Publishing
        │  MessageProcessedEvent
        │  AssistantResponseGeneratedEvent
        │  AIProviderCallCompletedEvent
        ▼
15. Return ChatResponse to client
```

---

## Asynchronous Workflow

Triggered after the response is returned to the user.

```
AssistantResponseGeneratedEvent
        │
        ├──▶ AnalyticsEventHandler  (@Async)
        │         └── Save usage / risk / AI provider metrics
        │
        └──▶ SessionSummaryAsyncHandler  (@Async)
                  └── Threshold check → LLM summarise → save ConversationSummary
                      First summary: totalMessageCount >= 10
                      Updates:      (totalMessageCount - lastCovered) >= 6
```

> **Rule:** Async handlers **never** block or affect the user response.  
> **Rule:** Events are published only from `ChatOrchestrator`, never from inner services.

---

## Database Schema Overview

| Table                    | Purpose                                                                      |
|--------------------------|------------------------------------------------------------------------------|
| `app_users`              | Account data, authentication, `onboarding_completed` flag                    |
| `conversations`          | Session metadata, ownership, `current_mode`/`current_stage` as plain Strings |
| `chat_messages`          | User and assistant message history                                           |
| `memories`               | Long-term user knowledge graph                                               |
| `knowledge_documents`    | Therapy knowledge content                                                    |
| `conversation_summaries` | Compressed session summaries                                                 |
| `analytics_events`       | Product metric events                                                        |
| `usage_metrics`          | Generic metric time series                                                   |
| `ai_usage_metrics`       | AI provider call metrics                                                     |
| `risk_metrics`           | Risk detection metrics                                                       |
| `therapy_metrics`        | Therapy decision metrics                                                     |
| `operation_metrics`      | Technical latency / success metrics                                          |
| `error_events`           | Error tracking                                                               |

---

## Architectural Principles

| #  | Principle                                                                                                                                                                                                                                      |
|----|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1  | **Domain is pure Java.** No Spring, JPA, or provider SDK in domain classes.                                                                                                                                                                    |
| 2  | **OpenAI is only accessible through `AIProviderGateway`.** No direct SDK calls from services.                                                                                                                                                  |
| 3  | **JPA exists only in infrastructure.** Domain layers never reference JPA entities or repositories.                                                                                                                                             |
| 4  | **Controllers remain thin.** No business logic in controllers.                                                                                                                                                                                 |
| 5  | **Business decisions belong to application/domain services.**                                                                                                                                                                                  |
| 6  | **Async handlers must never affect response generation.** Failures are silent and logged.                                                                                                                                                      |
| 7  | **Risk detection remains synchronous.** Crisis situations require immediate pipeline awareness.                                                                                                                                                |
| 8  | **Memory and Knowledge remain separate concepts.** Memory = user facts. Knowledge = therapy content.                                                                                                                                           |
| 9  | **Summary and Memory remain separate concepts.** Summary = compressed history. Memory = structured facts.                                                                                                                                      |
| 10 | **All AI interactions are provider-agnostic.** Use `AIProviderGateway` abstraction.                                                                                                                                                            |
| 11 | **Modular Monolith first.** Microservices only if proven necessary by scale.                                                                                                                                                                   |
| 12 | **Long-term maintainability is prioritised over MVP shortcuts.**                                                                                                                                                                               |
| 13 | **Legal consent is enforced at the controller layer.** `ChatController.verifyOnboarding()` throws 403 before entering the orchestrator. The consent popup is served by the frontend and stored via `POST /api/consent/accept`.                 |
| 14 | **Module public APIs are the only cross-module contract.** `UserProfileService`, `ConsentService`, `MemoryService`, `SummaryQueryService` are the published interfaces; internal repositories must never be injected outside their own module. |

---

## Future Roadmap

| Phase       | Name                         | Description                                                                                                                 |
|-------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| **Phase 1** | **Current**                  | Auth · Chat · Analysis · Risk · Therapy · Memory · Prompt · Knowledge · Summary · Analytics · Observability                 |
| **Phase 2** | Psychological Profile Engine | Build structured psychological models (attachment patterns, coping mechanisms, self-worth patterns) from memory and history |
| **Phase 3** | Vector Search                | PgVector · Embeddings · Hybrid RAG — replace keyword-based knowledge retrieval with semantic search                         |
| **Phase 4** | Therapist Dashboard          | Supervised review, session insights, flag escalation                                                                        |
| **Phase 5** | Admin Dashboard              | Platform monitoring, user management, content management                                                                    |
| **Phase 6** | Multi-Provider AI            | OpenAI + Anthropic + Gemini + Ollama with provider routing                                                                  |
| **Phase 7** | Recommendation Engine        | Personalised exercise and technique recommendations                                                                         |
| **Phase 8** | Mobile Application           | iOS / Android native apps                                                                                                   |

