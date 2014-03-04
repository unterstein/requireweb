(function () {
  window.ajaxCall = function (route, data, successFn, errorFn) {
    route.ajax({
      success: function (data) {
        if (data != null && data[0] == '/') {
          window.location.href = data;
        } else if (data != null && data == 'reload') {
          window.location.reload()
        } else {
          if (successFn) successFn(data);
          doAfterAjaxHandling();
        }
      },
      error: function (data) {
        if (data != null && data[0] == '/') {
          window.location.href = data;
        } else if (data != null && data == 'reload') {
          window.location.reload()
        } else {
          if (errorFn) errorFn(data);
          doAfterAjaxHandling();
        }
      },
      data: data
    });
  }

  window.updateElement = function (element, data) {
    $(element).html(data);
  }

  window.doAfterAjaxHandling = function () {
    doFileUpload();
    $("textarea").autoGrow();
  }

  $(function () {
    /* submit behavior */
    $(document).on("keypress", ".modal form :input", function (e) {
      if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
        $(".modal button.submit").click();
        e.stopImmediatePropagation();
        e.preventDefault();
        return false;
      } else {
        return true;
      }
    });
    /* deletes */
    $(document).on("click", ".delete", function(e) {
      var that = $(this);
      $("#deleteModal").modal("show");
      $("#deleteConfirm").unbind("click").bind("click", function() {
        $.post(that.data("url"))
          .success(function() {
            window.location.href = that.data("success")
          });
      });
    });

    /* focus behavior */
    $(document).on("shown.bs.modal", ".modal", function () {
      var inputs = $(this).find(".modal-body :input[type!='hidden']");
      if (inputs.length > 0) {
        $(inputs[0]).focus();
      } else {
        var buttons = $(this).find(".btn");
        if (buttons.length > 0) {
          $(buttons[0]).focus();
        }
      }
    });
    doAfterAjaxHandling();
    $(document).on("focus", ".fullText", function () {
      var that = $(this);
      setTimeout(function () {
        that.selectAll();
      }, 100);
    });
    $("select.select2, .select2 select").select2({
      minimumResultsForSearch: 7
    });
    $(".datetimepicker").each(function() {
      $(this).datetimepicker({
        pickSeconds: false,
        pickTime: $(this).find(":input").data("time"),
        startDate: new Date(),
        format: $(this).find(":input").data("format")
      });
    });
  });

  function doFileUpload() {
    var uploadProxy = $('.uploadField button[name="fileUploadProxy"], .uploadField .uploadLabel');
    $(uploadProxy).unbind('click').click(function (e) {
      $(this).siblings('input[type="file"]').click();
      e.preventDefault();
    }).bind("keypress",function (e) {
        if (e.keyCode == 32) { // whitespace
          $(this).siblings('input[type="file"]').click();
        }
        e.preventDefault();
      }).siblings('input[type="file"]').change(function (e) {
        $(this).siblings('.uploadLabel').val(/([^\\\/]+)$/.exec(this.value)[1]); // Extract the filename
        $(this).siblings('.uploadLabel').change();
      });
  }
})();

(function () {
  $.fn.setCursorPosition = function (position) {
    if (this.length == 0) return this;
    return $(this).setSelection(position, position);
  };

  $.fn.setSelection = function (selectionStart, selectionEnd) {
    if (this.length == 0) return this;
    input = this[0];

    if (input.createTextRange) {
      var range = input.createTextRange();
      range.collapse(true);
      range.moveEnd('character', selectionEnd);
      range.moveStart('character', selectionStart);
      range.select();
    } else if (input.setSelectionRange) {
      input.focus();
      input.setSelectionRange(selectionStart, selectionEnd);
    }

    return this;
  };

  $.fn.focusEnd = function () {
    this.setCursorPosition(this.val().length);
  };

  $.fn.selectAll = function () {
    if (this.val() != undefined) {
      this.setSelection(0, this.val().length);
    }
  };

  $.fn.endsWith = function (suffix) {
    if (this.val() != undefined) {
      return this.val().toLowerCase().match(suffix.toLowerCase() + "$") == suffix.toLowerCase();
    } else {
      return false;
    }
  }
})();