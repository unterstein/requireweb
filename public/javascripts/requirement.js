/**
 * Copyright (C) 2014 Johannes Unterstein, unterstein@me.com, unterstein.info
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
$(function () {
  /** project stuff */
  $("#newProject").click(function () {
    ajaxCall(jsRoutes.controllers.ProjectController.projectEditPanel(-1), null, function(data) {
      var modal = $("#projectModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
    return false;
  });
  /** add/edit buttons in projet modal */
  $(document).on("click", "#newProjectAdd", function () {
    ajaxCall(jsRoutes.controllers.ProjectController.addProject(), $("#projectForm").serialize(), function (data) {
      $("#projectModal .modal-content").html(data);
      $("#projectModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  $(document).on("click", "#projectEdit", function () {
    var id = $("#projectId").val();
    ajaxCall(jsRoutes.controllers.ProjectController.editProject(id), $("#projectForm").serialize(), function (data) {
      $("#projectModal .modal-content").html(data);
      $("#projectModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  window.pEdit = function (id) {
    ajaxCall(jsRoutes.controllers.RequirementController.projectEditPanel(id), null, function(data) {
      var modal = $("#projectModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
  }
  $(".projectInfo").click(function () {
    var id = $(this).data("id");
    ajaxCall(jsRoutes.controllers.ProjectController.projectInfoPanel(id), null, function(data) {
      var modal = $("#projectInfoModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
  });

  window.rEdit = function (project, id) {
    ajaxCall(jsRoutes.controllers.RequirementController.requirementEditPanel(project, id), null, function(data) {
      var modal = $("#requireModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
  }

  /** requirement stuff */
  $(document).on("click", ".newRequirement", function () {
    var id = $(this).data("id");
    var parent = $(this).data("parent");
    ajaxCall(jsRoutes.controllers.RequirementController.requirementEditPanel(id, -1), null, function(data) {
      var modal = $("#requireModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
      $("#requireProjectId").val(id);
      $("#requireParent").val(parent);
    });
    return false;
  });
  /** add/edit buttons in require modal */
  $(document).on("click", "#newRequireAdd", function () {
    var id = $("#requireProjectId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.addRequirement(id), $("#requireForm").serialize(), function (data) {
      $("#requireModal .modal-content").html(data);
      $("#requireModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  $(document).on("click", "#requireEdit", function () {
    var id = $("#requireId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.editRequirement(id), $("#requireForm").serialize(), function (data) {
      $("#requireModal .modal-content").html(data);
      $("#requireModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  $(".requireInfo").click(function () {
    var id = $(this).data("id");
    ajaxCall(jsRoutes.controllers.RequirementController.requirementInfoPanel(id), null, function(data) {
      var modal = $("#requireInfoModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
  });

  /** expanded toggle */
  $(".showReq").click(function() {
    ajaxCall(jsRoutes.controllers.RequirementController.toggleExpandedState($(this).data("id"), true));
    $(this).closest(".panel").removeClass("off").addClass("on");
  });
  $(".hideReq").click(function() {
    ajaxCall(jsRoutes.controllers.RequirementController.toggleExpandedState($(this).data("id"), false));
    $(this).closest(".panel").removeClass("on").addClass("off");
  });

  /** tabs */
  $("#tabOthers, #tabRequirement").click(function() {
    window.history.pushState(null, document.title, $(this).data("url"));
  });

  /** efforts */
  $(document).on("click", "#newEffort", function () {
    var id = $(this).data("id");
    ajaxCall(jsRoutes.controllers.EffortController.effortEditPanel(id, -1), null, function(data) {
      var modal = $("#effortModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
      $("#effortProjectId").val(id);
    });
    return false;
  });
  $(document).on("click", "#newEffortAdd", function () {
    var id = $("#effortProjectId").val();
    ajaxCall(jsRoutes.controllers.EffortController.addEffort(id), $("#effortForm").serialize(), function (data) {
      $("#effortModal .modal-content").html(data);
      $("#effortModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  $(document).on("click", "#effortEdit", function () {
    var id = $("#effortId").val();
    ajaxCall(jsRoutes.controllers.EffortController.editEffort(id), $("#effortForm").serialize(), function (data) {
      $("#effortModal .modal-content").html(data);
      $("#effortModal").find(".modal-body :input[type!='hidden']")[0].focus();
    });
  });
  window.eEdit = function (project, id) {
    ajaxCall(jsRoutes.controllers.EffortController.effortEditPanel(project, id), null, function(data) {
      var modal = $("#effortModal");
      modal.modal("show");
      modal.find(".modal-content").html(data);
    });
  }
});