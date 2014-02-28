$(function () {
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
    ajaxCall(jsRoutes.controllers.RequirementController.addProject(name, description));
  });
  hideAll();
  window.pEdit = function (id) {
    $("#projectModal").modal("show");
    $("#projectEdit").show();
  }
  window.pDelete = function (id) {

  }

  function hideAll() {
    $("#newProjectAdd").hide();
    $("#projectEdit").hide();
  }
});