# Homework 02: Test Plan Creation

## Testing objectives
- Verify user authentication and authorization (AuthService)
- Ensure bar station functions correctly (BarStationService)
- Ensure categories are correctly assigned and retrieved (CategoryService)
- Validate Jwt token generation and validation (JwtService)
- Validate accurate inventory management (InventoryService)
- Verify organization data functions correctly (OrganizationService)
- Ensure products are correctly assigned and retrieved (ProductService)
- Ensure sales service processes transactions accurately (SalesService)

## Testing levels (e.g. unit, integration, system)
### Unit Testing:
Focus on individual services such (e.g. AuthServiceTest, BarStationTest, and CategoryServiceTest).

#### UI testing
- Utility functions tests
- Form validation tests

### Integration Testing:
1. Focus on sending requests and validating responses (Controller package)
2. Test interactions between services (e.g. AuthService with JwtService).
3. Validate data flow between services (e.g. CategoryService with ProductService).
4. Test database interactions (e.g. InventoryService with OrganizationService).
5. Ensure proper error handling and edge cases across services.

**Backend Integration Tests:**

**Scenarios to Test:**

1. **Authentication Flow:**
  - OAuth2 login → JWT generation → Cookie setting → User creation/update
  - Test with `@SpringBootTest` and `MockMvc`

2. **Sales Transaction Flow:**
  - Product selection → Stock validation → Price adjustment → Inventory deduction → Transaction recording
  - Verify database state after each step

3. **Multi-Tenancy Isolation:**
  - Ensure Organization A cannot access Organization B's data
  - Test with multiple organizations in parallel

4. **Dynamic Pricing Logic:**
  - Sale → Price increase → Max price boundary → Category with/without dynamic pricing
  - Verify price history correctness

**Frontend Integration Tests:**

**Scenarios:**

1. **API Route Integration:**
  - Test Next.js API routes with mocked backend responses
  - Verify proper error handling and status codes

2. **State Management:**
  - Test data flow between parent/child components
  - Test context providers (if implemented)


### System Testing:
1. End-to-end testing of the entire application workflow.
2. Validate overall system performance and reliability.
3. UI testing to ensure user interface functions correctly.

### Acceptance Testing:
1. Validate that the application meets business requirements.

### Performance Testing

**Load Testing Scenarios:**

1. **Concurrent POS Operations:**
  - Simulate 10+ cashiers processing sales simultaneously
  - Target: < 500ms response time for POST `/api/sales`

2. **Dashboard Analytics:**
  - Load user/station statistics with 1000+ transactions
  - Target: < 2s page load time

3. **Inventory Queries:**
  - Fetch inventory with 500+ products across 20+ categories
  - Target: < 1s response time
   
### **Stress Testing:**

- Identify breaking point for concurrent users
- Test database connection pool limits
- Monitor memory usage and garbage collection


### Security Testing

**Authentication & Authorization:**

- Test JWT token expiration and refresh
- Verify role-based access control (USER vs ADMIN)
- Test for unauthorized access attempts
- Validate OAuth2 flow security

**Input Validation:**

- SQL injection attempts on search/filter endpoints
- XSS attacks on user inputs (product names, notes)
- CSRF protection verification

**Data Security:**

- Test multi-tenancy data isolation
- Verify sensitive data is not exposed in responses
- Test for broken authentication/session management


## Test scope
- In Scope:
  - User login and authentication 
  - Point-of-sale (POS) workflows 
  - Client-facing interfaces 
  - Inventory management (front-end and back-end)
  - API integrations
  - Error handling and edge cases.

Examples of in-scope features:
**Backend API Endpoints (28 total):**

- Authentication (`/auth/login/success`, `/auth/logout`)
- Account management (`/api/account`, `/api/account/onboarding`)
- Organizations (CRUD operations)
- Categories (CRUD operations)
- Products (CRUD operations)
- Inventory (stock operations, history, statistics)
- Bar Stations (CRUD, user assignment)
- Sales (transaction processing)
- Users (listing)

**Frontend Pages (8 total):**

- Login page (`/login`)
- Onboarding page (`/onboarding`)
- Dashboard (`/dashboard`)
- Inventory management (`/inventory`)
- POS station management (`/pos`)
- POS interface (`/pos/[stationId]`)
- Client display (`/client`)

**Business Logic:**

- Dynamic pricing algorithm
- Inventory transaction auditing
- Multi-tenancy isolation
- Role-based permissions
- OAuth2 + JWT authentication

**Database:**

- Liquibase migrations
- Entity relationships and constraints
- Transaction integrity
- Data validation (e.g., quantity >= 0)

## Out of Scope:
- User acceptance testing - no real users to test with, team members will perform acceptance-style testing internally instead.
- Testing on mobile devices

## Test approach

### Testing Strategy

**Shift-Left Approach:**

- Write tests during development (TDD where applicable)
- Run tests locally before committing code
- Automated tests in CI/CD pipeline

### Test Automation

**CI/CD Integration:**

**Current State:** Tests are skipped in production build (`-DskipTests`)

**Target Implementation:**

```yaml
# .github/workflows/ci-test.yml
jobs:
  backend-tests:
    - Run unit tests (./mvnw test)
    - Run integration tests (./mvnw verify)
    - Generate coverage report

  frontend-tests:
    - Run unit tests (npm test)
    - Run E2E tests (npm run test:e2e)
    - Generate coverage report
```

**Test Execution Schedule:**

- **On every commit:** Unit tests + linting
- **On PR creation:** Full test suite (unit + integration)
- **Pre-deployment:** Full suite + E2E tests
- **Nightly:** Performance tests + security scans

### Manual Testing

**Exploratory Testing:**

- Testing for new features
- UI/UX validation
- Usability testing with real users

### Defect Management:
Defects will be logged directly to GitHub issues.

## Test environment

### Tools:
Backend: JUnit 5, Mockito, Spring Test, H2 in-memory DB<br>
Frontend: Jest/Vitest, React Testing Library

### Testing Environments

**1. Local Environment**

- Developer machines
- Docker containers
- Purpose: Unit and integration tests during development

**2. CI/CD Environment**

- GitHub Actions runners
- Ephemeral containers per build
- Purpose: Automated test execution on every commit
  - Example,TEST/DEV environment<br>
  This environment is used for all features and workflows that are developed locally, pushed to the remote repository (GitHub), and then deployed to the TEST/DEV environment.

**3. Staging Environment**

- Mirrors production setup
- Separate database instance
- Purpose: E2E tests, UAT, performance tests

**4. Production Environment**

- Live application
- This environment is used for the scope of features (release branch) that has been successfully tested and validated in the UAT environment and are ready for live use by end-users.
- Purpose: Smoke tests post-deployment, monitoring

## Entry and exit criteria
### Entry Criteria:
- Test environment is set up and configured.
- Test data is prepared and available.
- Test cases are reviewed and approved.
- The main functionality and requirements are mapped and clear for all team members.
- Development work is complete and code is deployed to the test environment.

### Exit Criteria:
- All planned test cases have been executed.
- All critical and high-severity defects have been resolved.
- Stakeholders/Team members have signed off on the testing phase.
- Performance benchmarks met (response times < targets)
- Security scan shows no high/critical vulnerabilities
- Regression tests pass
- Documentation is updated

## Roles and responsibilities
The team will operate as a cross-functional unit. All team members are responsible for performing unit testing, fixing defects, executing system and regression testing, reviewing test results, and approving the release.

| Role                 | Responsibilities                                                                                                                |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------|
| Full-Stack Developer | Write unit tests, fix defects, support integration testing, UI/UX testing<br>Maintain test environment, deploy builds, manage infrastructure |
| Product owner        | Validate acceptance criteria, participate in UAT, prioritize bug fixes, approve release                                         |


## Risks and assumptions   
### Test plan risks:
- Limited testing time
- Limited manpower
- Environment instability
- Incomplete or unclear requirements may lead to inadequate test coverage.
- Limited test data may affect the accuracy of test results.

### Project risks:
**High Priority:**

1. **No Frontend Tests**
  - Problem: Frontend has 0% test coverage
  - Impact: Bugs not detected before production
  - Solution: Add Jest + React Testing Library, start with critical components

**Medium Priority:**

1. **Google OAuth Dependency**
  - Problem: If Google is down, users cannot login
  - Impact: Application unusable
  - Solution: Use mock OAuth for tests, monitor Google status

2. **Dynamic Pricing Bugs**
  - Problem: Complex price calculation logic
  - Impact: Wrong prices, lost revenue
  - Solution: Add comprehensive tests with edge cases

### Assumptions:
  - Requirements are clear and remain stable during the testing cycle.
  - Development work (Enough manpower) will be completed on schedule to allow sufficient time for testing. 
  - Team has time to write tests alongside features
  - Critical bugs will be fixed quickly

## Test deliverables
- Test Plan Document
- Test Cases and Test Scripts
- Test Data Sets
- Test Execution Reports
- Bug Reports (tracked in GitHub Issues)
- Test Summary Report
- Release Notes (QA section)
