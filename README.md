# TherapistAI

TherapistAI is an AI-powered psychotherapy-inspired conversational platform — and a modern reimplementation of my
university graduation thesis project, originally developed approximately 15 years ago.

The original project was built in Java and focused on Turkish Natural Language Processing (NLP). At the time, large language models did not exist, so the system relied on linguistic analysis, text similarity algorithms, and handcrafted conversational rules to simulate therapist-style interactions.

Fifteen years later, this project revisits the same idea using modern AI technologies. Instead of relying solely on
keyword matching and similarity algorithms, TherapistAI leverages Large Language Models, conversational memory,
structured therapy reasoning, and Retrieval-Augmented Generation (RAG) concepts to create safe, explainable, and
context-aware interactions.

---

## Documentation

| Document                                      | Description                                                               |
|-----------------------------------------------|---------------------------------------------------------------------------|
| [Architecture & Design](docs/ARCHITECTURE.md) | Master architecture, module structure, data flow, principles, and roadmap |

---

## Objectives

* Rebuild the original graduation thesis using modern AI technologies
* Explore conversational AI architectures with Java and Spring Boot
* Implement Retrieval-Augmented Generation (RAG)
* Experiment with vector search and semantic retrieval
* Build conversational memory capabilities
* Create a psychotherapy-inspired conversational assistant
* Evaluate modern AI engineering patterns and architectures

---

## Technology Stack

| Layer            | Technology            |
|------------------|-----------------------|
| Language         | Java 25               |
| Framework        | Spring Boot 4         |
| AI Framework     | Spring AI             |
| AI Provider      | OpenAI                |
| Database         | PostgreSQL            |
| Security         | Spring Security + JWT |
| Containerisation | Docker                |
| Build            | Maven                 |
| Deployment       | Render                |

---

## Project Roadmap

### Milestone 1 — Project Foundation ✅

* Spring Boot project setup
* Maven configuration
* Project structure
* Local development environment
* Docker support

### Milestone 2 — Web Interface ✅

* Chat UI
* Static frontend
* REST integration
* Welcome page

### Milestone 3 — OpenAI Integration ✅

* Spring AI integration
* OpenAI ChatModel integration
* Prompt management
* Psychotherapy-inspired system prompt

### Milestone 4 — Conversation Memory ✅

* Conversation IDs
* Multi-message conversations
* In-memory conversation storage
* Context preservation across messages

### Milestone 5 — Automated Testing ✅

* Unit tests
* Controller tests
* Conversation memory tests
* Validation tests

### Milestone 6 — Cloud Deployment ✅

* Docker containerisation
* Render deployment
* Public demo environment
* Health monitoring

### Milestone 7 — Modular Architecture ✅

* Hexagonal architecture (Ports & Adapters)
* Domain-Driven Design module boundaries
* Analysis module (emotion, sentiment, themes)
* Risk module (crisis detection, safety escalation)
* Therapy module (mode, stage, approach, goal)
* Memory module (extraction, retrieval, persistence)
* Knowledge module (keyword-based retrieval)
* Prompt module (layered prompt assembly)
* Summary module (async session compression)
* Analytics module (usage and therapy metrics)
* Observability module (latency, error tracking)
* Event-driven async post-processing

### Milestone 8 — End-to-End Observability ✅

* TraceId propagation via MDC
* Structured log format `[EVENT_NAME] traceId=X key=value`
* Privacy-safe logging (`LogSanitizer`)
* Per-step latency tracking
* Domain decision result logs for every pipeline step
* DB write logs for all persistence operations
* `[CHAT_FLOW_DECISION_SUMMARY]` per request

### Milestone 9 — Knowledge Base Ingestion ⏳ Planned

* Psychotherapy datasets
* Document ingestion pipeline
* Dataset normalisation
* Chunk generation

### Milestone 10 — Embeddings & Vector Storage ⏳ Planned

* OpenAI embeddings
* PostgreSQL pgvector integration
* Embedding generation pipeline

### Milestone 11 — Semantic Retrieval (RAG) ⏳ Planned

* Similarity search
* Hybrid RAG (keyword + semantic)
* Retrieval-Augmented Generation
* Source grounding

### Milestone 12 — Psychological Profile Engine ⏳ Planned

* Attachment pattern detection
* Recurring fear modelling
* Coping mechanism mapping
* Self-worth pattern tracking

### Milestone 13 — Production Hardening ⏳ Planned

* Security hardening
* CI/CD improvements
* Scalability review
* Multi-provider AI routing

---

## Disclaimer

TherapistAI is an experimental NLP and AI project created for educational, research, and software engineering purposes.

It is not a medical, psychological, psychiatric, or therapeutic service and should not be used as a substitute for professional mental health support.

Any responses generated by the system should be considered informational only.

---

## Live Demo

https://therapist-ai-yzfo.onrender.com/
