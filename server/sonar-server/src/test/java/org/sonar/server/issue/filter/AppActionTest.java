/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.issue.filter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;

@RunWith(MockitoJUnitRunner.class)
public class AppActionTest {
  @Rule
  public UserSessionRule userSessionRule = UserSessionRule.standalone();

  AppAction underTest;
  WsTester ws;

  @Before
  public void setUp() {
    underTest = new AppAction(userSessionRule);
    ws = new WsTester(new IssueFilterWs(underTest));
  }

  @Test
  public void anonymous_app() throws Exception {
    userSessionRule.anonymous();
    ws.newGetRequest("api/issue_filters", "app").execute().assertJson(getClass(), "anonymous_page.json");
  }

  @Test
  public void logged_in_app() throws Exception {
    userSessionRule.login("eric").setUserId(123);
    ws.newGetRequest("api/issue_filters", "app").execute()
      .assertJson(getClass(), "logged_in_page.json");
  }

}
