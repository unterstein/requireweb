$(function() {
  $("#newProject").click(function() {
    $("#newModal").modal("show");
    return false;
  });
  /** clear behavior */
  $("#newModal").on("show.bs.modal", function () {
    $(this).find(":input").val("");
  });
  $("#newProjectAdd").click(function() {
    var name = $("#newProjectName").val();
    var description = $("#newProjectDescription").val();
    ajaxCall(jsRoutes.controllers.RequirementController.addProject(name, description));
  });
});