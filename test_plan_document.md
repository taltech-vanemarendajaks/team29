---
name: Test Plan Document

# Test Plan Document - Borsibaar Application
---

## 1. Testing Objectives

### 1.1 Primary Objectives

**Ensure Quality and Reliability:**

- Verify that all functional requirements are met and working correctly
- Validate dynamic pricing mechanism accuracy and edge cases
- Confirm secure authentication and authorization flows

**Prevent Regressions:**

- Detect defects early in the development cycle
- Ensure existing functionality remains intact when new features are added
- Maintain high code quality standards

**Performance and Scalability:**

- Verify the system can handle concurrent POS operations
- Ensure database transactions maintain data integrity under load
- Validate API response times meet acceptable thresholds

**User Experience:**

- Ensure UI components render correctly across browsers
- Validate smooth user flows from login to checkout
- Confirm accessibility standards are met

### 1.2 Success Criteria

- **Backend:** Minimum 80% code coverage for services and controllers
- **Frontend:** Minimum 70% code coverage for components and API routes
- **Critical Paths:** 100% test coverage for sales, inventory, and authentication flows

- **CI/CD:** All tests must pass before deployment

---

## 2. Testing Levels

### 2.1 Unit Testing

**Backend (Spring Boot):**

**Current State:** ✅ Strong coverage

- Controllers: 9/9 tested (~45 test methods)
- Services: 8/8 tested (~70 test methods)
- Security: JWT filter tested (11 test methods)
- **Total:** ~129 test methods

**Gaps to Address:**

- ❌ Repository layer (0/6+ repositories)
- ❌ MapStruct mappers (0/6+ mappers)
- ❌ Scheduled jobs (`PriceCorrectionJob`)
- ❌ Custom validators and utilities

**Frontend (Next.js):**

**Current State:** ❌ No tests

- 0 test files found
- No test framework configured

**To Implement:**

- React components unit tests (Jest + React Testing Library)
- Utility functions tests
- Custom hooks tests
- Form validation tests

**Tools:**

- Backend: JUnit 5, Mockito, Spring Test, H2 in-memory DB
- Frontend: Jest/Vitest, React Testing Library

---

### 2.2 Integration Testing

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

2. **Component Integration:**
   - Test CartSidebar + ProductCard interaction
   - Test StationDialog form submission flow

3. **State Management:**
   - Test data flow between parent/child components
   - Test context providers (if implemented)

---

### 2.3 System Testing

**End-to-End User Flows:**

**Critical Paths:**

1. **New User Onboarding:**

   ```
   Login → OAuth callback → Onboarding page → Select organization → Dashboard
   ```

   - Verify first user gets ADMIN role
   - Verify subsequent users get USER role

2. **POS Sales Flow:**

   ```
   Login → POS page → Select station → Add products → Checkout → Verify inventory update
   ```

   - Test with dynamic pricing enabled/disabled
   - Verify transaction history

3. **Inventory Management:**

   ```
   Login → Inventory page → Add product → Add stock → View history → Remove stock
   ```

   - Verify negative quantity prevention
   - Verify transaction audit trail

4. **Admin Station Management:**

   ```
   Login as ADMIN → POS page → Create station → Assign users → Verify access
   ```

   - Test with different user roles
   - Verify permissions

**Browser Compatibility:**

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

**Responsive Design:**

- Desktop (1920x1080, 1366x768)
- Tablet (iPad, 768x1024)
- Mobile (iPhone, 375x667)

**Tools:**

- Playwright or Cypress for E2E tests
- BrowserStack/Sauce Labs for cross-browser testing

---

### 2.4 Performance Testing

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

**Stress Testing:**

- Identify breaking point for concurrent users
- Test database connection pool limits
- Monitor memory usage and garbage collection

**Tools:**

- JMeter or Gatling for load testing
- Backend: Spring Boot Actuator metrics
- Database: PostgreSQL `pg_stat_statements`

---

### 2.5 Security Testing

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

---

## 3. Test Scope

### 3.1 In Scope

**Backend API Endpoints (28 total):**

- ✅ Authentication (`/auth/login/success`, `/auth/logout`)
- ✅ Account management (`/api/account`, `/api/account/onboarding`)
- ✅ Organizations (CRUD operations)
- ✅ Categories (CRUD operations)
- ✅ Products (CRUD operations)
- ✅ Inventory (stock operations, history, statistics)
- ✅ Bar Stations (CRUD, user assignment)
- ✅ Sales (transaction processing)
- ✅ Users (listing)

**Frontend Pages (8 total):**

- ✅ Login page (`/login`)
- ✅ Onboarding page (`/onboarding`)
- ✅ Dashboard (`/dashboard`)
- ✅ Inventory management (`/inventory`)
- ✅ POS station management (`/pos`)
- ✅ POS interface (`/pos/[stationId]`)
- ✅ Client display (`/client`)

**Business Logic:**

- ✅ Dynamic pricing algorithm
- ✅ Inventory transaction auditing
- ✅ Multi-tenancy isolation
- ✅ Role-based permissions
- ✅ OAuth2 + JWT authentication

**Database:**

- ✅ Liquibase migrations
- ✅ Entity relationships and constraints
- ✅ Transaction integrity
- ✅ Data validation (e.g., quantity >= 0)

---

## 4. Test Approach

### 4.1 Testing Strategy

**Shift-Left Approach:**

- Write tests during development (TDD where applicable)
- Run tests locally before committing code
- Automated tests in CI/CD pipeline

### 4.2 Test Automation

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

### 4.3 Manual Testing

**Exploratory Testing:**

- Testing for new features
- UI/UX validation
- Usability testing with real users

---

## 5. Test Environment

### 5.1 Development Environment

**Local Setup:**

- Docker Compose with PostgreSQL, Backend, Frontend

**Configuration:**

- `.env` files for local settings

### 5.2 Testing Environments

**1. Local Environment**

- Developer machines
- Docker containers
- Purpose: Unit and integration tests during development

**2. CI/CD Environment**

- GitHub Actions runners
- Ephemeral containers per build
- Purpose: Automated test execution on every commit

**3. Staging Environment**

- Mirrors production setup
- Separate database instance
- Purpose: E2E tests, UAT, performance tests
- URL: `staging.borsibaar.example.com`

**4. Production Environment**

- Live application
- Purpose: Smoke tests post-deployment, monitoring
- URL: `borsibaar.example.com`

---

## 6. Entry and Exit Criteria

### 6.1 Entry Criteria

**Before Testing Phase Begins:**

- ✅ All planned features are code-complete
- ✅ Development environment is stable and accessible
- ✅ Test cases are written and reviewed
- ✅ Test data is prepared
- ✅ Test environments are configured
- ✅ Developers have completed smoke testing

### 6.2 Exit Criteria

**Before Release to Production:**

- ✅ All test cases executed (100% execution rate)
- ✅ Code coverage meets threshold (80% backend, 70% frontend)
- ✅ Zero P0 (critical) bugs
- ✅ All P1 (high) bugs resolved or accepted by stakeholders
- ✅ Performance benchmarks met (response times < targets)
- ✅ Security scan shows no high/critical vulnerabilities
- ✅ Regression tests pass
- ✅ Documentation is updated

**Bug Severity Definitions:**

- **P0 (Critical):** Application crash, data loss, security breach
- **P1 (High):** Core functionality broken, major feature unusable
- **P2 (Medium):** Feature partially broken, workaround available
- **P3 (Low):** Minor UI issues, cosmetic problems

---

## 7. Roles and Responsibilities

### 7.1 Team Roles

**QA Lead**

- Define testing strategy and test plan
- Coordinate testing activities
- Review test cases and results
- Report test metrics to stakeholders

**Backend QA Engineer**

- Write and execute backend unit/integration tests
- Test API endpoints
- Database testing
- Performance testing

**Frontend QA Engineer**

- Write and execute frontend unit/component tests
- E2E test automation (Playwright/Cypress)
- UI/UX testing
- Cross-browser compatibility testing

**DevOps Engineer**

- Set up CI/CD test pipelines
- Manage test environments
- Configure monitoring and alerting
- Test deployment automation

**Developers**

- Write unit tests for new code (TDD)
- Fix bugs identified during testing
- Code reviews focusing on testability
- Support QA team with technical questions

**Product Owner**

- Define acceptance criteria
- Participate in UAT
- Prioritize bug fixes
- Sign off on releases

---

## 8. Risks and Assumptions

### 8.1 Risks

**High Priority:**

1. **No Frontend Tests**
   - Problem: Frontend has 0% test coverage
   - Impact: Bugs not detected before production
   - Solution: Add Jest + React Testing Library, start with critical components

2. **Tests Disabled in CI/CD**
   - Problem: Tests currently skipped during deployment (`-DskipTests`)
   - Impact: Broken code can reach production
   - Solution: Enable tests in pipeline, make them required

3. **Data Security Between Organizations**
   - Problem: Multiple organizations share same database
   - Impact: Data leak between organizations (critical security issue)
   - Solution: Write tests to verify organization isolation

**Medium Priority:**

4. **Google OAuth Dependency**
   - Problem: If Google is down, users cannot login
   - Impact: Application unusable
   - Solution: Use mock OAuth for tests, monitor Google status

5. **Dynamic Pricing Bugs**
   - Problem: Complex price calculation logic
   - Impact: Wrong prices, lost revenue
   - Solution: Add comprehensive tests with edge cases

### 8.2 Assumptions

**We assume that:**

- ✅ Test environments are available when needed
- ✅ All developers have Docker installed
- ✅ Team has time to write tests alongside features
- ✅ Critical bugs (P0/P1) will be fixed quickly
- ✅ PostgreSQL in production behaves like in tests
- ✅ Mock OAuth is similar enough to real Google OAuth

---

## 9. Test Deliverables

### 9.1 Test Artifacts

**Planning Phase:**

- ✅ Test Plan Document
- ✅ Test Strategy Document

**Design Phase:**

- ✅ Test Case Specifications
- ✅ Test Data Requirements

**Execution Phase:**

- ✅ Test Execution Reports (per sprint/release)
- ✅ Bug Reports (tracked in GitHub Issues)
- ✅ Test Coverage Reports (JaCoCo for backend, Istanbul/NYC for frontend)
- ✅ Performance Test Results (JMeter HTML reports)

**Closure Phase:**

- ✅ Test Summary Report
- ✅ Defect Metrics Dashboard
- ✅ Release Notes (QA section)

### 9.2 Metrics and Reporting

**Key Metrics to Track:**

**Code Coverage:**

- Backend: Current ~80%, Target: 80%+ (services/controllers), 70%+ (repositories/mappers)
- Frontend: Current 0%, Target: 70%+

---
