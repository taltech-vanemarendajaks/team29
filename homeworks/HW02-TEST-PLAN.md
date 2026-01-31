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
1. Focus on individual services such (e.g. AuthServiceTest, BarStationTest, and CategoryServiceTest).

### Integration Testing:
1. Focus on sending requests and validating responses (Controller package)
2. Test interactions between services (e.g. AuthService with JwtService).
3. Validate data flow between services (e.g. CategoryService with ProductService).
4. Test database interactions (e.g. InventoryService with OrganizationService).
5. Ensure proper error handling and edge cases across services.

### System Testing:
1. End-to-end testing of the entire application workflow.
2. Validate overall system performance and reliability.
3. UI testing to ensure user interface functions correctly.

### Acceptance Testing:
1. Validate that the application meets business requirements.

## Test scope
- In Scope:
  - All services listed in the testing objectives.
  - Database interactions and data integrity.
  - User interface functionality.
  - Error handling and edge cases.

## Test approach


## Test environment
### TEST/DEV environment
This environment is used for all features and workflows that are developed locally, pushed to the remote repository (GitHub), and then deployed to the TEST/DEV environment.
### UAT or User Acceptance Testing environment
This environment is used for a bunch of features and workflows (release branch) that have been successfully tested in the TEST/DEV environment and are ready for final validation by end-users or stakeholders before being deployed to production.
### PROD
This environment is used for the scope of features (release branch) that has been successfully tested and validated in the UAT environment and are ready for live use by end-users.

## Entry and exit criteria
### Entry Criteria:
- Test environment is set up and configured.
- Test data is prepared and available.
- Test cases are reviewed and approved.
- Necessary tools and resources are available.
- Development work is complete and code is deployed to the test environment.

### Exit Criteria:
- All planned test cases have been executed.
- All critical and high-severity defects have been resolved.
- Stakeholders have signed off on the testing phase.

## Roles and responsibilities
| Role                 | Responsibilities                                                                                                              |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------|
| Full-Stack Developer | Write unit tests, fix defects, support integration testing<br>Maintain test environment, deploy builds, manage infrastructure |
| PO                   | Validate acceptance criteria, approve release                                                                                                                              |


## Risks and assumptions   
### Risks:
  - Incomplete or unclear requirements may lead to inadequate test coverage.
  - Limited test data may affect the accuracy of test results.
### Assumptions:
  - Requirements remain stable during the testing cycle.
  - Development work will be completed on schedule to allow sufficient time for testing.

## Test deliverables 
- Test Plan Document
- Test Cases and Test Scripts
- Test Data Sets
- Test Execution Reports
- Defect Reports
- Test Summary Report