# Comprehensive Milestone Summary & Project Workflow
_Last updated: May 19, 2025_

## Initial Planning, DevOps Setup & Sprint Workflow
**Completion:** Mar 01, 2025  
**Contributors:** Lukas Ott

**Key Activities:**
- Outlined milestone goals and timelines for phase 1.
- Allocated teams: Frontend, User Management, Game Management.
- Configured GitLab board with ticket system and labels.
- Established weekly sprint planning and retros for continuous alignment.

---

## Backend Infrastructure & Authentication
**Completion:** Mar 05, 2025  
**Contributors:** Lukas Ott, Leonardo Melodia

**Key Activities:**
- Implemented Singleton DB connector and UserRepository.
- Created UserHandler and UserAPI endpoints for user creation and deletion.
- Improved validation logic for signup/login with error handling.

**Modifications/Improvements:**
- Added connection pooling and default DB URI.
- Refined error handling, logging mechanisms, and basic backend tests.

---

## Mockups & UI Planning
**Completion:** Mar 14, 2025  
**Contributors:** Jan Walter, Noyan Morali

**Key Activities:**
- Designed mockups for login, lobby, and game setup views.
- Established design direction for medieval-inspired interface.

---

## Websocket Integration & Lobby Communication
**Completion:** Mar 20, 2025  
**Contributors:** Enrico Naumann

**Key Activities:**
- Implemented websocket structure for real-time lobby communication.
- Started client session identification and room join logic.

---

## Artefacts Creation
**Completion:** Mar 22, 2025
**Contributors:** Lukas Ott, Enrico Naumann, Leon Kürsch, Noyan Morali, Jan Walter, Leonardo Melodia
**Key Activities:**
- Created artefacts like requirements, architecture, and design documents.

---

## Game Engine Foundation & Turn Management
**Completion:** Mar 25, 2025  
**Contributors:** Leon Kürsch, Enrico Naumann

**Key Activities:**
- Laid foundation for turn-based logic and state transitions.
- Defined player actions and move validation checks.

---

## Frontend Functionality & Flow Wiring
**Completion:** Apr 06, 2025  
**Contributors:** Noyan Morali, Jan Walter

**Key Activities:**
- Hooked up frontend actions to backend calls.
- Implemented navigation flow from Register → Lobby.

---

## Game Data Modeling & Database Expansion
**Completion:** April 29, 2025  
**Contributors:** Leonardo Melodia, Lukas Ott

**Key Activities:**
- Added table to store game results
- Extended database layer and API

---

## Frontend–Backend Integration
**Completion:** May 13, 2025  
**Contributors:** Lukas Ott

**Key Activities:**
- Connected Register/Login UI with backend via REST API.
- Verified user creation through UI to ensure handshake success.
- Connected AI playing with websockets and ensured legal and correct move execution.

---

## Testing Infrastructure Setup
**Completion:** May 18, 2025  
**Contributors:** Leonardo Melodia, Lukas Ott, Leon Kürsch

**Key Activities:**
- Added unit tests for repository and API functions.
- Implemented mock database for backend testing.
- Configured CI/CD pipeline for automated testing.
- Added unit tests for game engine and websocket communication.

---

## End-to-End Gameplay Implementation
**Completion:** May 19, 2025  
**Contributors:** Leon Kürsch

**Key Activities:**
- Implemented game engine logic for player turns and actions.
- Integrated game state management with UI updates.
- Implemented user enhancing features like graying-out impossible moves, etc.

---

## AI Development
**Completion:** May 19, 2025  
**Contributors:** Lukas Ott, Leonardo Melodia, Jan Walter

**Key Activities:**
- Implemented GUI to join game sessions and select AI difficulty.
- Designed decision-making logic for AI players.
- Integrated AI with game engine for real-time play.

---

## Network enablement
**Completion:** May 19, 2025  
**Contributors:** Leon Kürsch, Lukas Ott

**Key Activities:**
- Implemented network communication for game state updates.
- Ensured real-time synchronization of game state across clients.

---

## Final Testing & Bug Fixes
**Completion:** May 19, 2025  
**Contributors:** Lukas Ott, Leon Kürsch

**Key Activities:**
- Conducted end-to-end testing of the game flow.
- Identified and resolved critical bugs in game state management.

---
