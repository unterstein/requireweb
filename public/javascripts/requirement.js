$(function () {
  /** project stuff */
  $("#newProject").click(function () {
    $("#projectModal").modal("show");
    $("#newProjectAdd").show();
    return false;
  });
  /** clear behavior */
  $("#projectModal").on("show.bs.modal", function () {
    hideAll();
    $(this).find(":input").val("");
  });
  /** add/edit buttons in modals */
  $("#newProjectAdd").click(function () {
    var name = $("#projectName").val();
    var description = $("#projectDescription").val();
    ajaxCall(jsRoutes.controllers.RequirementController.addProject(), $("#projectForm").serialize(), function(data) {
      $(".has-error").removeClass("has-error");
      for(var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
  $("#projectEdit").click(function () {
    var name = $("#projectName").val();
    var description = $("#projectDescription").val();
    var id = $("#projectId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.editProject(id), $("#projectForm").serialize(), function(data) {
      $(".has-error").removeClass("has-error");
      for(var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
  /** other stuff */
  hideAll();
  window.pEdit = function (id, name, description) {
    $("#projectModal").modal("show");
    $("#projectName").val(name);
    $("#projectDescription").val(description);
    $("#projectId").val(id);
    $("#projectEdit").show();
  }

  function hideAll() {
    $(".has-error").removeClass("has-error");
    $("#newProjectAdd").hide();
    $("#projectEdit").hide();
    $("#requireEdit").hide();
    $("#newRequireAdd").hide();
  }


  /** requirement stuff */
  $(".newRequirement").click(function() {
    $("#requireModal").modal("show");
    $("#newRequireAdd").show();
    $("#requireProjectId").val($(this).data("id"));
    $("#requireParent").val($(this).data("parent"));
    return false;
  });
  /** add/edit buttons in modals */
  $("#newRequireAdd").click(function () {
    var id = $("#requireProjectId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.addRequirement(id), $("#requireForm").serialize(), function(data) {
      $(".has-error").removeClass("has-error");
      for(var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
});