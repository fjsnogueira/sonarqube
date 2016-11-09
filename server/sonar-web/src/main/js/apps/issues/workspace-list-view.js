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
import $ from 'jquery';
import WorkspaceListView from '../../components/navigator/workspace-list-view';
import IssueView from './workspace-list-item-view';
import EmptyView from './workspace-list-empty-view';
import Template from './templates/issues-workspace-list.hbs';
import ComponentTemplate from './templates/issues-workspace-list-component.hbs';

const COMPONENT_HEIGHT = 29;
const BOTTOM_OFFSET = 60;

export default WorkspaceListView.extend({
  template: Template,
  componentTemplate: ComponentTemplate,
  childView: IssueView,
  childViewContainer: '.js-list',
  emptyView: EmptyView,

  initialize (options) {
    WorkspaceListView.prototype.initialize.apply(this, arguments);
    this.listenTo(options.app.state, 'change:total', this.render);
  },

  bindShortcuts () {
    const that = this;
    const doAction = function (action) {
      const selectedIssue = that.collection.at(that.options.app.state.get('selectedIndex'));
      if (selectedIssue == null) {
        return;
      }
      const selectedIssueView = that.children.findByModel(selectedIssue);
      selectedIssueView.$('.js-issue-' + action).click();
    };
    WorkspaceListView.prototype.bindShortcuts.apply(this, arguments);
    key('right', 'list', function () {
      const selectedIssue = that.collection.at(that.options.app.state.get('selectedIndex'));
      that.options.app.controller.showComponentViewer(selectedIssue);
      return false;
    });
    key('space', 'list', function () {
      const selectedIssue = that.collection.at(that.options.app.state.get('selectedIndex'));
      selectedIssue.set({ selected: !selectedIssue.get('selected') });
      return false;
    });
    key('f', 'list', function () {
      return doAction('transition');
    });
    key('a', 'list', function () {
      return doAction('assign');
    });
    key('m', 'list', function () {
      return doAction('assign-to-me');
    });
    key('p', 'list', function () {
      return doAction('plan');
    });
    key('i', 'list', function () {
      return doAction('set-severity');
    });
    key('c', 'list', function () {
      return doAction('comment');
    });
    key('t', 'list', function () {
      return doAction('edit-tags');
    });
  },

  unbindShortcuts () {
    WorkspaceListView.prototype.unbindShortcuts.apply(this, arguments);
    key.unbind('right', 'list');
    key.unbind('space', 'list');
    key.unbind('f', 'list');
    key.unbind('a', 'list');
    key.unbind('m', 'list');
    key.unbind('p', 'list');
    key.unbind('i', 'list');
    key.unbind('c', 'list');
    key.unbind('t', 'list');
  },

  scrollTo () {
    const selectedIssue = this.collection.at(this.options.app.state.get('selectedIndex'));
    if (selectedIssue == null) {
      return;
    }
    const selectedIssueView = this.children.findByModel(selectedIssue);
    const parentTopOffset = this.$el.offset().top;
    let viewTop = selectedIssueView.$el.offset().top - parentTopOffset;
    if (selectedIssueView.$el.prev().is('.issues-workspace-list-component')) {
      viewTop -= COMPONENT_HEIGHT;
    }
    const viewBottom = selectedIssueView.$el.offset().top + selectedIssueView.$el.outerHeight() + BOTTOM_OFFSET;
    const windowTop = $(window).scrollTop();
    const windowBottom = windowTop + $(window).height();
    if (viewTop < windowTop) {
      $(window).scrollTop(viewTop);
    }
    if (viewBottom > windowBottom) {
      $(window).scrollTop($(window).scrollTop() - windowBottom + viewBottom);
    }
  },

  attachHtml (compositeView, childView, index) {
    const $container = this.getChildViewContainer(compositeView);
    const model = this.collection.at(index);
    if (model != null) {
      const prev = index > 0 && this.collection.at(index - 1);
      let putComponent = !prev;
      if (prev) {
        const fullComponent = [model.get('project'), model.get('component')].join(' ');
        const fullPrevComponent = [prev.get('project'), prev.get('component')].join(' ');
        if (fullComponent !== fullPrevComponent) {
          putComponent = true;
        }
      }
      if (putComponent) {
        $container.append(this.componentTemplate(model.toJSON()));
      }
    }
    $container.append(childView.el);
  },

  destroyChildren () {
    WorkspaceListView.prototype.destroyChildren.apply(this, arguments);
    this.$('.issues-workspace-list-component').remove();
  },

  serializeData () {
    return {
      ...WorkspaceListView.prototype.serializeData.apply(this, arguments),
      state: this.options.app.state.toJSON()
    };
  }
});

