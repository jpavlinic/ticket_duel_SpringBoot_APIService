# Ticket Duel - Spring Boot API Service

## Overview
Ticket Duel is an RIT project sponsored by "Plavi Tim" and is currently in development. This project introduces a gamified ticketing system that enables competitive, turn-based challenge solving within a structured workflow. I am responsible for developing the backend using **Spring Boot**.

## Features
- **RESTful API Development**: Focused on secure coding practices.
- **Spring Framework Stack**: Utilizing Spring MVC, Spring JPA, Spring Security, and Hibernate.
- **Role-Based Access Control**: Secure user authentication system with login and registration mechanisms.
- **Dynamic Leaderboard**: Ranks users based on challenge performance.
- **Real-time Collaboration**: Allows solvers to communicate and strategize using live chat via WebSockets.

## Current Progress
The following features have been developed:
- **Authentication System**
- **Public Leaderboard Endpoint**
- **WebSockets for Live Chat**
- **Initial Ticket Endpoints** (Further development in progress)

The backend is hosted on the **Solace server** and is connected to the **React frontend**.

## Tech Stack
- **Spring Security & JWT**: Used for authentication.
- **Spring JPA**: Handles database communication and management.
- **Spring Validation**: Ensures input validation and sanitization.
- **Spring WebSockets**: Implements real-time chat functionality.
- **Aspect-Oriented Programming (AOP)**: Used for logging.
- **Global Exception Handling**: Provides centralized error management.
