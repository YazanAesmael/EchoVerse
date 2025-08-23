package com.jetpackages.echoverse.core.ai.embedder

import ai.koog.embeddings.base.Vector
import ai.koog.embeddings.local.LLMEmbedder
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.clients.openai.OpenAILLMClient
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import com.jetpackages.echoverse.core.ai.mainModel

/**
 * A dedicated, reusable component for generating text embeddings using Google's model.
 * This class directly uses the GoogleLLMClient's internal embedding capabilities.
 */
class EchoEmbedder(
    openAIClient: OpenAILLMClient
) {
    // We create the embedder once, as it can be reused.
    private val embedder = LLMEmbedder(
        client = openAIClient,
        model = OpenAIModels.Embeddings.TextEmbeddingAda002
    )

    /**
     * Takes a single piece of text and returns its vector representation as a FloatArray.
     *
     * @param text The text to embed.
     * @return A FloatArray representing the semantic vector of the text.
     */
    suspend fun embed(text: String): FloatArray {
        // 1. Call the official embedder to get a Vector object.
        val vector: Vector = embedder.embed(text)
        // 2. The Vector object contains a List<Double>, which we convert to a FloatArray.
        return vector.values.map { it.toFloat() }.toFloatArray()
    }

    /**
     * Takes a list of texts and returns a list of their vector representations.
     *
     * @param texts The list of texts to embed.
     * @return A List of FloatArrays.
     */
    suspend fun embed(texts: List<String>): List<FloatArray> {
        // The embedder in the documentation only supports single strings,
        // so we map over the list and call it for each one.
        return texts.map { embed(it) }
    }
}