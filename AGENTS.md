# Overview
This service is built with Java 11 and Spring Boot, using Gradle as its build tool. It connects to Elasticsearch to monitor error logs, uses Git for source code context, leverages an LLM for reasoning, and creates JIRA issues.

# Problem
The service automates error handling:
1. Listen for error events in Elasticsearch and trigger a webhook.
2. Analyze stack traces to pinpoint the failing line of code.
3. Query Git for commit information and the surrounding 20 lines of the faulty code, identifying the author.
4. Use a language model to infer potential causes.
5. Generate JIRA issues with the collected details and notify the relevant developer.

Goal: automatically route errors to the right developer, provide insight, and automate issue creation and notifications.
