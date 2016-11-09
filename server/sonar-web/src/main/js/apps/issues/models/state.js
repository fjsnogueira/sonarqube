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
import _ from 'underscore';
import State from '../../../components/navigator/models/state';

export default State.extend({
  defaults: {
    page: 1,
    maxResultsReached: false,
    query: {},
    facets: ['types', 'resolutions'],
    isContext: false,
    allFacets: [
      'issues',
      'types',
      'resolutions',
      'severities',
      'statuses',
      'createdAt',
      'rules',
      'tags',
      'projectUuids',
      'moduleUuids',
      'directories',
      'fileUuids',
      'assignees',
      'authors',
      'languages'
    ],
    facetsFromServer: [
      'types',
      'severities',
      'statuses',
      'resolutions',
      'projectUuids',
      'directories',
      'rules',
      'moduleUuids',
      'tags',
      'assignees',
      'authors',
      'fileUuids',
      'languages',
      'createdAt'
    ],
    transform: {
      'resolved': 'resolutions',
      'assigned': 'assignees',
      'createdBefore': 'createdAt',
      'createdAfter': 'createdAt',
      'sinceLeakPeriod': 'createdAt',
      'createdInLast': 'createdAt'
    }
  },

  getFacetMode () {
    const query = this.get('query');
    return query.facetMode || 'count';
  },

  toJSON () {
    return _.extend({ facetMode: this.getFacetMode() }, this.attributes);
  }
});

