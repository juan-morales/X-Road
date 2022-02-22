-- data populated for the integration tests
-- SQL needs to be defined in terms of autogenerated HSQL table structure, not the actual PostgreSQL tables.
INSERT INTO UI_USERS(ID, USERNAME, LOCALE, CREATED_AT, UPDATED_AT) values (1, 'testuser', null, now(), now());
-- noinspection SqlResolve
INSERT INTO APIKEY (ID, ENCODED_KEY) values (1, 'ad26a8235b3e847dc0b9ac34733d5acb39e2b6af634796e7eebe171165cdf2d1');
INSERT INTO APIKEY_ROLES (APIKEY_ID, ROLE) values (1, 'XROAD_SYSTEM_ADMINISTRATOR');
INSERT INTO APIKEY_ROLES (APIKEY_ID, ROLE) values (1, 'XROAD_SECURITY_OFFICER');
INSERT INTO APIKEY_ROLES (APIKEY_ID, ROLE) values (1, 'XROAD_SERVICE_ADMINISTRATOR');
INSERT INTO APIKEY_ROLES (APIKEY_ID, ROLE) values (1, 'XROAD_REGISTRATION_OFFICER');

INSERT INTO MEMBER_CLASSES (ID, CODE, DESCRIPTION, CREATED_AT, UPDATED_AT) values (1, 'GOV', 'Government', now(), now());

INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (1, 'MEMBER', 'TEST', 'GOV', 'M1', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (2, 'MEMBER', 'TEST', 'GOV', 'M2', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (3, 'MEMBER', 'TEST', 'GOV', 'M3', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (4, 'MEMBER', 'TEST', 'GOV', 'M4', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (5, 'MEMBER', 'TEST', 'GOV', 'M5', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (6, 'MEMBER', 'TEST', 'GOV', 'M6', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (7, 'MEMBER', 'TEST', 'GOV', 'M7', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (8, 'MEMBER', 'TEST', 'GOV', 'M8', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (9, 'MEMBER', 'TEST', 'GOV', 'M9', null, 'ClientId');
INSERT INTO IDENTIFIERS (ID, OBJECT_TYPE, XROAD_INSTANCE, MEMBER_CLASS, MEMBER_CODE, SUBSYSTEM_CODE, TYPE) values (10, 'SUBSYSTEM', 'TEST', 'GOV', 'M1', 'SS1', 'ClientId');


INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (1, 'M1', null, 'Member1', null, 1, 1, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (2, 'M2', null, 'Member2', null, 1, 2, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (3, 'M3', null, 'Member3', null, 1, 3, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (4, 'M4', null, 'Member4', null, 1, 4, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (5, 'M5', null, 'Member5', null, 1, 5, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (6, 'M6', null, 'Member6', null, 1, 6, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (7, 'M7', null, 'Member7', null, 1, 7, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (8, 'M8', null, 'Member8', null, 1, 8, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (9, 'M9', null, 'Member9', null, 1, 9, 'XRoadMember', now(), now());
INSERT INTO SECURITY_SERVER_CLIENTS (ID, MEMBER_CODE, SUBSYSTEM_CODE, NAME, XROAD_MEMBER_ID, MEMBER_CLASS_ID, SERVER_CLIENT_ID, TYPE, CREATED_AT, UPDATED_AT) values (10, null, 'SS1', 'Member1-SS1', 1, 1, 10, 'Subsystem', now(), now());
