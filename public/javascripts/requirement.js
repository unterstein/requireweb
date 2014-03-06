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
    $("#projectModal").modal("show");
    $("#newProjectAdd").show();
    return false;
  });
  /** clear behavior */
  $("#projectModal, #requireModal").on("show.bs.modal", function () {
    hideAll();
    $(this).find(":input").val("");
  });
  /** add/edit buttons in projet modal */
  $("#newProjectAdd").click(function () {
    ajaxCall(jsRoutes.controllers.RequirementController.addProject(), $("#projectForm").serialize(), function (data) {
      $(".has-error").removeClass("has-error");
      for (var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
  $("#projectEdit").click(function () {
    var id = $("#projectId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.editProject(id), $("#projectForm").serialize(), function (data) {
      $(".has-error").removeClass("has-error");
      for (var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
  /** other stuff */
  hideAll();
  window.pEdit = function (id, name, shortName, description) {
    $("#projectModal").modal("show");
    $("#projectName").val(name);
    $("#shortName").val(shortName);
    $("#projectDescription").val(description);
    $("#projectId").val(id);
    $("#projectEdit").show();
  }

  window.rEdit = function (id) {
    ajaxCall(jsRoutes.controllers.RequirementController.requirementEditPanel(id), null, function(data) {
      var modal = $("#requireModal");
      modal.modal("show");
      modal.find(".modal-body").html(data);
      $("#requireEdit").show();
    });
  }

  function hideAll() {
    $(".has-error").removeClass("has-error");
    $("#newProjectAdd").hide();
    $("#projectEdit").hide();
    $("#requireEdit").hide();
    $("#newRequireAdd").hide();
  }

  /** requirement stuff */
  $(".newRequirement").click(function () {
    $("#requireModal").modal("show");
    $("#newRequireAdd").show();
    $("#requireProjectId").val($(this).data("id"));
    $("#requireParent").val($(this).data("parent"));
    return false;
  });
  /** add/edit buttons in require modal */
  $("#newRequireAdd").click(function () {
    var id = $("#requireProjectId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.addRequirement(id), $("#requireForm").serialize(), function (data) {
      $("#requireModal .modal-body").html(data);
    });
  });
  $("#requireEdit").click(function () {
    var id = $("#requireId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.editRequirement(id), $("#requireForm").serialize(), function (data) {
      $("#requireModal .modal-body").html(data);
    });
  });
  $(".requireInfo").click(function () {
    var modal = $("#requireInfoModal")
    modal.modal("show");
    var title = modal.find(".modal-title");
    title.html(title.data("title") + $(this).data("name"));
    modal.find(".own").html(parseFloat($(this).data("effortown")));
    modal.find(".children").html(parseFloat($(this).data("effortchildren")));
    modal.find(".sum").html(parseFloat($(this).data("effortchildren")) + parseFloat($(this).data("effortown")));
    modal.find(".ownreal").html(parseFloat($(this).data("effortownreal")));
    modal.find(".childrenreal").html(parseFloat($(this).data("effortchildrenreal")));
    modal.find(".sumreal").html(parseFloat($(this).data("effortchildrenreal")) + parseFloat($(this).data("effortownreal")));
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

});