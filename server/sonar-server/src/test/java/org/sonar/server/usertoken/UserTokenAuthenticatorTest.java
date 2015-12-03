/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.usertoken;

import com.google.common.base.Optional;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.test.DbTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.db.user.UserTokenTesting.newUserToken;

@Category(DbTests.class)
public class UserTokenAuthenticatorTest {
  static final String GRACE_HOPPER = "grace.hopper";
  static final String ADA_LOVELACE = "ada.lovelace";

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public DbTester db = DbTester.create(System2.INSTANCE);
  DbClient dbClient = db.getDbClient();
  DbSession dbSession = db.getSession();
  TokenGenerator tokenGenerator = mock(TokenGenerator.class);

  UserTokenAuthenticator underTest = new UserTokenAuthenticator(tokenGenerator, db.getDbClient());

  @Test
  public void return_login_when_token_hash_found_in_db() {
    String token = "known-token";
    String tokenHash = "123456789";
    when(tokenGenerator.hash(token)).thenReturn(tokenHash);
    dbClient.userTokenDao().insert(dbSession, newUserToken().setLogin(GRACE_HOPPER).setTokenHash(tokenHash));
    dbClient.userTokenDao().insert(dbSession, newUserToken().setLogin(ADA_LOVELACE).setTokenHash("another-token-hash"));
    db.commit();

    Optional<String> login = underTest.authenticate(token);

    assertThat(login.isPresent()).isTrue();
    assertThat(login.get()).isEqualTo(GRACE_HOPPER);
  }

  @Test
  public void return_absent_if_token_hash_is_not_found() {
    Optional<String> login = underTest.authenticate("unknown-token");
    assertThat(login.isPresent()).isFalse();
  }
}