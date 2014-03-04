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
  window.pEdit = function (id, name, description) {
    $("#projectModal").modal("show");
    $("#projectName").val(name);
    $("#projectDescription").val(description);
    $("#projectId").val(id);
    $("#projectEdit").show();
  }

  window.rEdit = function (id, name, description, estimatedEffort) {
    $("#requireModal").modal("show");
    $("#requireName").val(name);
    $("#requireDescription").val(description);
    $("#requireId").val(id);
    $("#requireEstimatedEffort").val(estimatedEffort);
    $("#requireParent").val("-1"); // TODO not needed yet, but keep in mind ;)
    $("#requireEdit").show();
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
      $(".has-error").removeClass("has-error");
      for (var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
    });
  });
  $("#requireEdit").click(function () {
    var id = $("#requireId").val();
    ajaxCall(jsRoutes.controllers.RequirementController.editRequirement(id), $("#requireForm").serialize(), function (data) {
      $(".has-error").removeClass("has-error");
      for (var prop in data) {
        $("#" + prop).closest(".form-group").addClass("has-error");
      }
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

});