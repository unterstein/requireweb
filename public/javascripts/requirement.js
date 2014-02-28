$(function() {
  $("#newProject").click(function() {
    $("#newModal").modal("show");
    return false;
  });
  /** clear behavior */
  $("#newModal").on("show.bs.modal", function () {
    $(this).find(":input").val("");
  });
});