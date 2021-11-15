/*
 * The MIT License
 * Copyright (c) 2019- Nordic Institute for Interoperability Solutions (NIIS)
 * Copyright (c) 2018 Estonian Information System Authority (RIA),
 * Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import { RouteConfig } from 'vue-router';
import TabsBase from '@/components/layout/TabsBase.vue';

import AppLogin from '@/views/AppLogin.vue';
import AppBase from '@/views/AppBase.vue';

import AppError from '@/views/AppError.vue';

import { Permissions, RouteName } from '@/global';

import AlertsContainer from '@/components/ui/AlertsContainer.vue';
import Settings from '@/views/Settings/Settings.vue';
import SettingsTabs from '@/views/Settings/SettingsTabs.vue';
import MemberList from '@/views/Members/MemberList.vue';

import Members from '@/views/Members/Members.vue';
import Member from '@/views/Members/Member/Member.vue';

import MemberDetails from '@/views/Members/Member/Details/MemberDetails.vue';
import PageNavigation from '@/components/layout/PageNavigation.vue';
import MemberManagementRequests from '@/views/Members/Member/ManagementRequests/MemberManagementRequests.vue';
import MemberSubsystems from '@/views/Members/Member/Subsystems/MemberSubsystems.vue';
import BackupAndRestore from '@/views/Settings/BackupAndRestore/BackupAndRestore.vue';
import ApiKeys from '@/views/Settings/ApiKeys/ApiKeys.vue';
import CreateApiKeyStepper from '@/views/Settings/ApiKeys/CreateApiKeyStepper.vue';

import SystemSettings from '@/views/Settings/SystemSettings/SystemSettings.vue';
import SecurityServers from '@/views/SecurityServers/SecurityServers.vue';
import TrustServices from '@/views/TrustServices/TrustServices.vue';

import SecurityServersList from '@/views/SecurityServers/SecurityServersList.vue';
import SecurityServer from '@/views/SecurityServers/SecurityServer/SecurityServer.vue';
import SecurityServerDetails from '@/views/SecurityServers/SecurityServer/SecurityServerDetails.vue';
import SecurityServerClients from '@/views/SecurityServers/SecurityServer/SecurityServerClients.vue';
import SecurityServerAuthenticationCertificates from '@/views/SecurityServers/SecurityServer/SecurityServerAuthenticationCertificates.vue';
import SecurityServerManagementRequests from '@/views/SecurityServers/SecurityServer/SecurityServerManagementRequests.vue';

import GlobalResources from '@/views/GlobalResources/GlobalResources.vue';
import GlobalResourcesList from '@/views/GlobalResources/GlobalResourcesList.vue';
import GlobalGroup from '@/views/GlobalResources/GlobalGroup/GlobalGroup.vue';

import InitialConfiguration from '@/views/InitialConfiguration/InitialConfiguration.vue';

import GlobalConfiguration from '@/views/GlobalConfiguration/GlobalConfiguration.vue';
import GlobalConfigurationTabs from '@/views/GlobalConfiguration/GlobalConfigurationTabs.vue';
import ExternalConfiguration from '@/views/GlobalConfiguration/ExternalConfiguration/ExternalConfiguration.vue';
import InternalConfiguration from '@/views/GlobalConfiguration/InternalConfiguration/InternalConfiguration.vue';
import TrustedAnchors from '@/views/GlobalConfiguration/TrustedAnchors/TrustedAnchors.vue';
import ManagementRequests from '@/views/ManagementRequests/ManagementRequests.vue';
import TabsBaseEmpty from '@/components/layout/TabsBaseEmpty.vue';

const routes: RouteConfig[] = [
  {
    path: '/',
    component: AppBase,
    name: RouteName.BaseRoute,
    redirect: { name: RouteName.Members },
    children: [
      {
        path: '/settings',
        meta: {
          permissions: [
            Permissions.MOCK_PERMISSION1,
            Permissions.MOCK_PERMISSION2,
          ],
        },
        components: {
          default: Settings,
          top: TabsBase,
          subTabs: SettingsTabs,
          alerts: AlertsContainer,
        },
        props: {
          subTabs: true,
        },
        children: [
          {
            path: 'globalresources',
            component: GlobalResources,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
            children: [
              {
                name: RouteName.GlobalResources,
                path: '',
                component: GlobalResourcesList,
                props: true,
              },
              {
                name: RouteName.GlobalGroup,
                path: 'globalgroup/:groupId',
                component: GlobalGroup,
                props: true,
              },
            ],
          },
          {
            name: RouteName.SystemSettings,
            path: 'systemsettings',
            component: SystemSettings,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
          {
            name: RouteName.BackupAndRestore,
            path: 'backup',
            component: BackupAndRestore,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
          {
            name: RouteName.ApiKeys,
            path: 'apikeys',
            component: ApiKeys,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
        ],
      },

      {
        name: RouteName.CreateApiKey,
        path: '/keys/apikey/create',
        components: {
          default: CreateApiKeyStepper,
          alerts: AlertsContainer,
        },
        props: {
          default: true,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
      },

      {
        path: '/members',
        components: {
          default: Members,
          top: TabsBase,
          alerts: AlertsContainer,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
        children: [
          {
            name: RouteName.Members,
            path: '',
            component: MemberList,
          },
          {
            path: ':memberid',
            components: {
              default: Member,
              pageNavigation: PageNavigation,
            },
            props: { default: true },
            redirect: '/members/:memberid/details',
            children: [
              {
                name: RouteName.MemberDetails,
                path: 'details',
                component: MemberDetails,
                props: { default: true },
              },
              {
                name: RouteName.MemberManagementRequests,
                path: 'managementrequests',
                component: MemberManagementRequests,
                props: { default: true },
              },
              {
                name: RouteName.MemberSubsystems,
                path: 'subsystems',
                component: MemberSubsystems,
                props: { default: true },
              },
            ],
          },
        ],
      },

      {
        path: '/security-servers',
        components: {
          default: SecurityServers,
          top: TabsBase,
          alerts: AlertsContainer,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
        children: [
          {
            name: RouteName.SecurityServers,
            path: '',
            component: SecurityServersList,
          },
          {
            path: ':id',
            components: {
              default: SecurityServer,
              pageNavigation: PageNavigation,
            },
            props: { default: true },
            redirect: '/security-servers/:id/details',
            children: [
              {
                name: RouteName.SecurityServerDetails,
                path: 'details',
                component: SecurityServerDetails,
                props: { default: true },
              },
              {
                name: RouteName.SecurityServerManagementRequests,
                path: 'managementrequests',
                component: SecurityServerManagementRequests,
                props: { default: true },
              },
              {
                name: RouteName.SecurityServerAuthenticationCertificates,
                path: 'authenticationcertificates',
                component: SecurityServerAuthenticationCertificates,
                props: { default: true },
              },
              {
                name: RouteName.SecurityServerClients,
                path: 'clients',
                component: SecurityServerClients,
                props: { default: true },
              },
            ],
          },
        ],
      },

      {
        name: RouteName.TrustServices,
        path: '/trust-services',
        components: {
          default: TrustServices,
          top: TabsBase,
          alerts: AlertsContainer,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
      },

      {
        name: RouteName.Initialisation,
        path: '/init',
        components: {
          default: InitialConfiguration,
          top: TabsBaseEmpty,
          alerts: AlertsContainer,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
      },

      {
        name: RouteName.ManagementRequests,
        path: '/management-requests',
        components: {
          default: ManagementRequests,
          top: TabsBase,
          alerts: AlertsContainer,
        },
        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
      },

      {
        path: '/global-configuration',
        components: {
          default: GlobalConfiguration,
          top: TabsBase,
          subTabs: GlobalConfigurationTabs,
          alerts: AlertsContainer,
        },
        props: {
          subTabs: true,
        },
        children: [
          {
            name: RouteName.InternalConfiguration,
            path: '',
            component: InternalConfiguration,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
          {
            name: RouteName.ExternalConfiguration,
            path: 'external-configuration',
            component: ExternalConfiguration,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
          {
            name: RouteName.TrustedAnchors,
            path: 'trusted-anchors',
            component: TrustedAnchors,
            props: true,
            meta: { permissions: [Permissions.MOCK_PERMISSION1] },
          },
        ],

        meta: { permissions: [Permissions.MOCK_PERMISSION1] },
      },
    ],
  },
  {
    path: '/login',
    name: RouteName.Login,
    component: AppLogin,
  },
  {
    path: '*',
    component: AppError,
  },
];

export default routes;