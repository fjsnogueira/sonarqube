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
package org.sonar.server.platform.web.requestid;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.core.util.UuidGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestUidGeneratorImplTest {
  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void generate_renews_inner_UuidGenerator_instance_every_number_of_calls_to_generate_specified_in_RequestIdConfiguration() {
    UuidGenerator.WithFixedBase generator1 = increment -> new byte[] {124, 22, 66, 96, 55, 88, 2, 9};
    UuidGenerator.WithFixedBase generator2 = increment -> new byte[] {0, 5, 88, 81, 8, 6, 44, 19};
    UuidGenerator.WithFixedBase generator3 = increment -> new byte[] {126, 9, 35, 76, 2, 1, 2};
    RequestUidGeneratorBase uidGeneratorBase = mock(RequestUidGeneratorBase.class);
    IllegalStateException expected = new IllegalStateException("Unexpected third call to createNew");
    when(uidGeneratorBase.createNew())
      .thenReturn(generator1)
      .thenReturn(generator2)
      .thenReturn(generator3)
      .thenThrow(expected);

    RequestUidGeneratorImpl underTest = new RequestUidGeneratorImpl(uidGeneratorBase, new RequestIdConfiguration(2));

    assertThat(underTest.generate()).isEqualTo("fBZCYDdYAgk="); // using generator1
    assertThat(underTest.generate()).isEqualTo("fBZCYDdYAgk=");
    assertThat(underTest.generate()).isEqualTo("AAVYUQgGLBM="); // call 2
    assertThat(underTest.generate()).isEqualTo("AAVYUQgGLBM=");
    assertThat(underTest.generate()).isEqualTo("CCC");
    assertThat(underTest.generate()).isEqualTo("DDD");

    expectedException.expect(IllegalStateException.class);
    expectedException.expectMessage(expected.getMessage());

    underTest.generate();
  }
}
