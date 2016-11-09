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

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.server.user.UserSession;
import org.sonar.server.ws.WsAction;

// TODO remove this WS when ui won't rely on it to know if the user can bulk
public class AppAction implements WsAction {

  private final UserSession userSession;

  public AppAction(UserSession userSession) {
    this.userSession = userSession;
  }

  @Override
  public void define(WebService.NewController controller) {
    WebService.NewAction action = controller.createAction("app");
    action
      .setDescription("Get data required for rendering the page 'Issues'.")
      .setSince("4.2")
      .setInternal(true)
      .setHandler(this)
      .setResponseExample(getClass().getResource("example-app.json"));
    action
      .createParam("id")
      .setDeprecatedSince("6.2");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    JsonWriter json = response.newJsonWriter();
    json.beginObject();
    json.prop("canBulkChange", userSession.isLoggedIn());
    json.endObject();
    json.close();
  }

}
