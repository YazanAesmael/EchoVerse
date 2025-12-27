# 🧠 EchoVerse - A Deeply Personal AI Companion

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-brightgreen.svg?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**EchoVerse is a deeply personal AI companion app that transforms your existing chat histories into an interactive "Echo"—a digital twin of a person that communicates with their unique style, humor, and personality.**

This is a private repository for the development of EchoVerse, a KMM application built with a clean, modular architecture designed for scalability and the integration of advanced AI agents.

---

## ✨ Core Concept & How It Works

The primary goal of EchoVerse is to experience a new form of connection by interacting with an AI that genuinely feels and sounds like a specific person, based on their real conversations. This is achieved through a sophisticated, multi-stage AI workflow:

1.  **📥 Ingestion & Pre-Analysis**: A user exports a chat log (e.g., from WhatsApp) and shares it with EchoVerse. A lightweight Koog agent runs to extract the names of the participants.
2.  **🧬 Personality Synthesis**: After the user selects a participant, a powerful Koog agent performs a deep analysis of their messages. It doesn't just summarize; it generates a structured **Personality Profile**, capturing everything from tone and emoji frequency to signature slang and the nature of their relationship with the user.
3.  **💬 Dynamic Chat Interaction**: When the user chats with an Echo, a third Koog agent comes online. For every single message, it performs a **"Dynamic Context Funnel"**:
    *   It uses the static **Personality Profile** as its core DNA.
    *   It retrieves relevant **long-term memories** from a local database (RAG).
    *   It considers the **short-term history** of the current conversation.
    *   It uses this rich, multi-layered context to generate a reply that is nuanced, in-character, and feels alive.

## 🏗️ Architectural Overview

This project is MultiPlatform built on a feature-based, multi-module clean architecture. This ensures high cohesion, low coupling, and makes the codebase easy to maintain and scale.


## 🛠️ Tech Stack & Key Libraries

*   **Platform**: Kotlin Multiplatform Mobile (KMM)
*   **UI**: Compose Multiplatform (for both Android & iOS)
*   **AI Framework**: JetBrains Koog
*   **LLM Provider**: Google Gemini (easily swappable)
*   **Architecture**: MVVM / MVI using a modular, clean architecture approach.
*   **Dependency Injection**: Koin
*   **Navigation & Lifecycle**: Decompose
*   **Database**: SQLDelight
*   **Networking**: Ktor

## 🚀 Getting Started

The project is fully configured and ready to build.

### Prerequisites
*   Android Studio (Jellyfish or newer recommended)
*   Xcode and its Command Line Tools (for building the iOS target)
*   A Google AI API Key for the Gemini model.

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [your-repo-url]
    ```

2.  **Get your API Key:**
    *   Visit the [Google AI for Developers](https://ai.google.dev/) page to generate your free API key.

3.  **Add your API Key:**
    *   In the root of the project, create a file named `local.properties` (this file is already in `.gitignore`).
    *   Add your API key to this file. The key name must be exact:
        ```properties
        GEMINI_API_KEY="YOUR_API_KEY_HERE"
        ```

4.  **Build and Run:**
    *   Open the project in Android Studio.
    *   Wait for Gradle to sync all the modules and dependencies.
    *   Select the `androidApp` configuration and run it on an emulator or a physical device.

## ✅ Project Status (Current)

The full MVP loop is complete and functional on Android.
*   ✅ App successfully appears in the Android share sheet.
*   ✅ Handles both `.txt` and `.zip` chat exports from WhatsApp.
*   ✅ **Phase 1 (Birth of the Echo):** The `KoogChatAnalyzer` successfully generates a structured, multi-part `PersonalityProfile`.
*   ✅ **Phase 2 (Living Memory):** The data layer is equipped to store and retrieve both persistent chat history and long-term "episodic" memories. The chat agent is using the RAG pattern to recall context.
*   ✅ Full navigation between the Home, Create Echo, and Chat screens is implemented with Decompose.
*   ✅ Chatting with an Echo is fully functional.
