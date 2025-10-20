Postman assets for Product API
================================

Structure
---------

- `product-api.postman_collection.json`: Full suite covering No Security and Security profiles.
- `env.no-security.postman_environment.json`: Safe environment with base URL for the no-security profile.
- `env.security.postman_environment.json`: Safe environment for the security profile (no secrets).

Usage
-----

1. Import the collection and the environment JSON file in Postman.
2. Select the appropriate environment:
   - `Product API - No Security` when running default profile (`no-security`).
   - `Product API - Security` when running with security enabled.
3. For the Security profile:
   - Run `With Security/Auth/Login (set token)` first to populate `{{token}}`.
   - Subsequent requests will use `Authorization: Bearer {{token}}` automatically.

Variables
---------

- `{{baseUrl}}`: Defaults to `http://localhost:8080/api`. Adjust if your host/port/context changes.
- `{{token}}`: Left empty in VCS. Set via login or manually for local/testing.

CI
--

You can run the collection in CI with Postman CLI/Newman against a test deployment. Provide secrets via CI variables and avoid committing real tokens.

Local overrides
---------------

Create a local environment file (e.g., `env.local.postman_environment.json`) for personal tokens/hosts and add it to `.gitignore`.


