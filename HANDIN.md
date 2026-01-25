# Homework 01: Team Workflow with Git & GitHub

## Pull requests

| Link                                                      | Author   | Reviewer |
|-----------------------------------------------------------|----------|----------|
| https://github.com/taltech-vanemarendajaks/team29/pull/7  | Karme    | Veronika |
| https://github.com/taltech-vanemarendajaks/team29/pull/9  | Veronika | Karme    |
| https://github.com/taltech-vanemarendajaks/team29/pull/10 | Karme    | Veronika |
| https://github.com/taltech-vanemarendajaks/team29/pull/12 | Veronika | Karme    |

## Merge conflict

### Explanation of the conflict 
We changed the same SalesService class (pull requests 9 and 10) while refactoring the code there.
- Constant with same name was created, but different value.
- Same lines in processSaleItem method were changed

### How conflict was resolved
- Constant with value "SALE-" was accepted and for the transactionType an enum was used instead
- Changes from pull request 9 was accepted (the usage of transactionType enam was included there)

### Final cleanup 
-  Repository hygiene has been performed
