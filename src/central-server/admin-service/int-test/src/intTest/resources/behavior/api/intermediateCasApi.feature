@Modifying
@IntermediateCas
Feature: Intermediate CAS API

  Background:
    Given Authentication header is set to SYSTEM_ADMINISTRATOR
    And Certification service is created

  Scenario: Adding intermediate CA is created
    Given intermediate CA added to certification service
    When intermediate CAs are retrieved
    Then intermediate CA is as follows
      | #$hash                    | A9D0114E3D7FADFF960EBBF2E21DD064D530F4D6 |
      | $issuerDistinguishedName  | CN=Issuer                                |
      | $subjectDistinguishedName | CN=Subject                               |
      | $subjectCommonName        | Subject                                  |
      | $notBefore                | 2023-02-27T10:01:42Z                     |
      | $notAfter                 | 2024-02-27T10:01:42Z                     |

  Scenario: Adding intermediate CA is deleted
    Given intermediate CA added to certification service
    When intermediate CA is deleted
    Then deleted intermediate CA is not present

  Scenario: Adding OCSP responder to intermediate CA
    Given intermediate CA added to certification service
    Then OCSP responder is added to intermediate CA

  Scenario: View the OCSP responders of an intermediate CA
    Given intermediate CA added to certification service
    And OCSP responder is added to intermediate CA
    And OCSP responder is added to intermediate CA
    And OCSP responder is added to intermediate CA
    Then intermediate CA has 3 OCSP responders

  Scenario: Deleting OCSP responder from intermediate CA
    Given Certification service is created
    And intermediate CA added to certification service
    And OCSP responder is added to intermediate CA
    Then OCSP responder is deleted from intermediate CA

  Scenario: Modify the OCSP responder of intermediate CA
    Given intermediate CA added to certification service
    And OCSP responder is added to intermediate CA
    When OCSP responder url is updated
    Then intermediate CA has 1 OCSP responders
    And intermediate CA has the updated OCSP responder

  Scenario: Modify the OCSP responder of intermediate CA #2
    Given intermediate CA added to certification service
    And OCSP responder is added to intermediate CA
    When OCSP responder url and certificate is updated
    Then intermediate CA has the updated OCSP responder
    And the OCSP responder certificate was updated
