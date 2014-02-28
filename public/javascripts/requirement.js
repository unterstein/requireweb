$(function() {
  $("#newProject").click(function() {
    $("#newModal").modal("show");
    return false;
  });
  $("#newModal").on("show.bs.modal", function () {
    $(this).find(":input").val("");
  });
});