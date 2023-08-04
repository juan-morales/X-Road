@SecurityServer
@Client
Feature: 0540 - SS: Client OpenApi REST services

  Background:
    Given SecurityServer login page is open
    And User xrd logs in to SecurityServer with password secret
    And Clients tab is selected

  Scenario: Client service with invalid openApi spec is not added
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Rest service dialog is opened and OpenApi spec is set to "https://www.niis.org/nosuchopenapi.yaml" and service code "s4c1"
    Then Dialog data is saved and error message "Parsing OpenApi3 description failed" is shown

  Scenario: Client service with invalid openApi spec is not added
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Rest service dialog is opened and OpenApi spec is set to "https://www.niis.org/nosuchopenapi.yaml" and service code "s4c1"
    Then Dialog data is saved and error message "Parsing OpenApi3 description failed" is shown

  Scenario: Client service with openApi yaml spec is added
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Rest service dialog is opened and OpenApi spec is set to "http://mock-server:1080/test-services/testopenapi1.yaml" and service code "s4c1"
    Then Dialog data is saved and success message "OpenApi3 service added" is shown
    And  Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi1.yaml)" is present in the list

  Scenario: Client service with openApi json spec is added
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Rest service dialog is opened and OpenApi spec is set to "http://mock-server:1080/test-services/testopenapi2.json" and service code "s4c2"
    Then Dialog data is saved and success message "OpenApi3 service added" is shown
    And  Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is present in the list

  Scenario: Client service is edited
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    And Service URL is set to "https://petstore.swagger.io/v3", timeout to 30 and tls certificate verification is unchecked
    Then Service is saved and success message "Service saved" is shown
    When Service with code "s4c2" is opened
    Then Service URL is "https://petstore.swagger.io/v3", timeout is 30 and tls certificate verification is unchecked

  Scenario: Client service has access rights added to it
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    And Service add subjects dialog is opened
    And Service subject lookup is executed with member name "TestGov" and subsystem code "test"
    And Subject with id "CS:GOV:0245437-2:TestSaved" and "CS:GOV:0245437-2:test-consumer" is selected from the table. There are total 3 entries
    Then Service Access Rights table member with id "CS:GOV:0245437-2:TestSaved" is present
    And Service Access Rights table member with id "CS:GOV:0245437-2:test-consumer" is present


  Scenario: Client service has one access rights removed
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    And Service Access Rights table member with id "CS:GOV:0245437-2:TestSaved" is present
    When Service Access Rights subject with id "CS:GOV:0245437-2:TestSaved" is removed
    Then Service Access Rights table member with id "CS:GOV:0245437-2:TestSaved" is missing
    And Service Access Rights table member with id "CS:GOV:0245437-2:test-consumer" is present

  Scenario: Client service has all access rights removed
    When Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    And Service add subjects dialog is opened
    And Service subject lookup is executed with member name "TestGov" and subsystem code "test"
    And Subject with id "CS:GOV:0245437-2:TestSaved" and "CS:GOV:0245437-2:TestService" is selected from the table. There are total 2 entries
    Then Service Access Rights table member with id "CS:GOV:0245437-2:TestSaved" is present
    And Service Access Rights table member with id "CS:GOV:0245437-2:test-consumer" is present
    When Service has all Access Rights removed
    Then Service Access Rights table member with id "CS:GOV:0245437-2:TestSaved" is missing
    And Service Access Rights table member with id "CS:GOV:0245437-2:test-consumer" is missing
    And Service Access Rights table member with id "CS:GOV:0245437-2:TestService" is missing

  Scenario: Client service has new endpoint added to it
    Given Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    And Service endpoints view is opened
    When Service endpoint with HTTP request method "PATCH" and path "/new/path/" is added
    Then Service endpoint with HTTP request method "PATCH" and path "/new/path/" is present in the list
    When Service endpoint with duplicated HTTP request method "PATCH" and path "/new/path/" is not added
    Then Service endpoint with HTTP request method "PATCH" and path "/new/path/" is present in the list

  Scenario: Only manually added endpoints can be edited
    Given Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    When Service endpoints view is opened
    Then Service endpoint with HTTP request method "PUT" and path "/pet" is not editable
    When Service endpoint with HTTP request method "PATCH" and path "/new/path/" has its path changed to "/new/path/edited"
    Then Service endpoint with HTTP request method "PATCH" and path "/new/path/edited" is present in the list
    And Service endpoint with HTTP request method "PATCH" and path "/new/path/" is missing in the list

  Scenario: Manually added endpoints can be deleted
    Given Client "TestService" is opened
    And Services sub-tab is selected
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is expanded
    And Service with code "s4c2" is opened
    When Service endpoints view is opened
    When Service endpoint with HTTP request method "PATCH" and path "/new/path/edited" is deleted
    And Service endpoint with HTTP request method "PATCH" and path "/new/path/edited" is missing in the list

  Scenario: Newly added services are enabled and one of them disabled
    Given Client "TestService" is opened
    And Services sub-tab is selected
    When Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi1.yaml)" is enabled
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is enabled
    Then Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi2.json)" is disabled with notice "just disabled."

  Scenario: Newly added service is edited
    Given Client "TestService" is opened
    And Services sub-tab is selected
    When Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi1.yaml)" is updated with url "http://mock-server:1080/test-services/testopenapi11.yaml" and service code "s4c100"
    Then Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi1.yaml)" is missing in the list
    And Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi11.yaml)" is present in the list

  Scenario: Newly added service is deleted
    Given Client "TestService" is opened
    And Services sub-tab is selected
    When Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi11.yaml)" is deleted
    Then Service "OPENAPI3 (http://mock-server:1080/test-services/testopenapi11.yaml)" is missing in the list
