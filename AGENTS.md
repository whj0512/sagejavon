# Repository Guidelines

## Project Structure & Module Organization

SageJavon has three services. `frontend/` contains the Vue 3 and TypeScript app: pages live in `src/views/`, shared UI in `src/components/`, API clients in `src/api/`, and assets in `src/assets/` and `public/`. `backend/` is a Java 16 Spring Boot and MyBatis-Plus service; code is under `src/main/java/com/springboot/cli/`, with configuration in `src/main/resources/`. `algorithm/` contains the Flask AI service, including `server/app/` endpoints and `server/rag/` retrieval code.

## Build, Test, and Development Commands

Run commands from the named service directory:

- `cd frontend && npm install && npm run dev` starts the Vite development server.
- `cd frontend && npm run build` creates a production frontend bundle; `npm run preview` serves that bundle locally.
- `cd frontend && npm run lint && npm run type-check` checks frontend style and Vue/TypeScript types.
- `cd backend && mvn spring-boot:run` runs the API after configuring its database connection; `mvn test` runs Maven tests.
- `cd algorithm && pip install -r requirements.txt && python create_sqlite_db.py && python rag_gpt_app.py` installs Python dependencies, initializes SQLite, and starts the AI service.

## Coding Style & Naming Conventions

Frontend `.editorconfig` requires two spaces for JS, TS, and Vue files, a final newline, and no trailing whitespace. Follow `frontend/.prettierrc.json`: no semicolons, single quotes, and an 80-character print width. Run `npm run lint` before submitting frontend changes. Follow existing Vue component and Java class naming; use `snake_case` for Python modules and functions. Avoid broad formatting changes in unrelated files.

## Testing Guidelines

The repository has no frontend test script or backend `src/test/` suite. Add focused tests for changed behavior when practical: Java tests under `backend/src/test/`, and Python tests named `test_*.py` beside the affected module or in a dedicated test directory. Existing Python `test_*.py` files include integration-style scripts that may require credentials or external services; run those only with the required configuration. Report which checks ran and any checks you could not run.

## Commit & Pull Request Guidelines

Recent commits mostly use terse `add` messages, while `frontend/.commitlintrc.json` and its Husky hook require Conventional Commits. Use descriptive subjects such as `fix(frontend): correct exercise history loading`. In pull requests, describe the affected service and behavior, list validation commands, link the relevant issue when available, and include screenshots for UI changes. Keep generated files, local databases, logs, and secrets out of commits; review staged files before pushing.
