# TherapistAI

TherapistAI is a modern reimplementation of my university graduation thesis project, originally developed approximately 15 years ago.

The original project was built in Java and focused on Turkish Natural Language Processing (NLP). At the time, large language models did not exist, so the system relied on linguistic analysis and similarity matching techniques to simulate therapist-style conversations.

The application analyzed user messages using Turkish NLP tools such as Zemberek and searched a collection of psychotherapy-inspired dialogue datasets to identify similar conversations and retrieve relevant responses.

The original datasets included psychotherapy dialogue examples, therapist-patient conversations, and educational resources related to psychotherapy and counseling techniques.

Fifteen years later, this project revisits the same idea using modern AI technologies.

Instead of relying solely on keyword matching and similarity algorithms, TherapistAI combines Large Language Models (LLMs), Retrieval-Augmented Generation (RAG), vector embeddings, conversational memory, and semantic search to create more natural and context-aware interactions.

This project is both a technical modernization of an old academic project and an exploration of how conversational AI has evolved over the last decade and a half.

## Objectives

* Rebuild the original graduation thesis using modern AI technologies
* Explore conversational AI architectures with Java and Spring Boot
* Implement Retrieval-Augmented Generation (RAG)
* Experiment with vector search and semantic retrieval
* Build long-term conversational memory
* Create a psychotherapy-inspired conversational assistant

## Technology Stack

* Java 25
* Spring Boot 4
* Spring AI
* OpenAI
* PostgreSQL
* pgvector
* Docker
* Maven

## Current Status

Implemented:

* Spring Boot application
* OpenAI chat integration
* Web-based chat interface
* Prompt management
* PostgreSQL and pgvector infrastructure

Planned:

* Conversation memory
* Dataset ingestion
* Embedding generation
* Vector search
* Retrieval-Augmented Generation (RAG)
* Automated testing

## Disclaimer

TherapistAI is an experimental NLP and AI project created for educational and research purposes.

It is not a medical, psychological, psychiatric, or therapeutic service and should not be used as a substitute for professional mental health support.
