package provider

import org.springframework.security.test.context.support.WithMockUser

// PACT-based tests are using Spring Security's @WithMockUser to simulate a valid user.

@WithMockUser
class PactContractTestBase : ContractTestBase()
