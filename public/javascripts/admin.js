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
$(function() {
  // --------------------------------------------- tables
  if ($(".tablesorter.desc").length > 0) {
    $(".tablesorter.desc").dataTable({
      "iDisplayLength": 25,
      "aLengthMenu": [[25, 50, 100, -1], [25, 50, 100, "All"]],
      "aaSorting": [[ 0, "desc" ]]
    });
  }
  if ($(".tablesorter.asc").length > 0) {
    $(".tablesorter.asc").dataTable({
      "iDisplayLength": 25,
      "aLengthMenu": [[25, 50, 100, -1], [25, 50, 100, "All"]]
    });
  }
  $(document).on("click", ".showException", function() {
    var tr = $(this).closest("tr");
    if($(tr).next().hasClass('exceptionRow') == true) {
      $(tr).next().remove();
    } else {
      $(tr).after("<tr class='exceptionRow'><td colspan='5'>" + $(this).data("content") + "</td></tr>");
    }
  });
  $(document).on("click", ".tablesorter thead, .tablesorter a", function() {
    if($(this).hasClass("showException") == false) {
      $(this).closest("table").find(".exceptionRow").remove();
    }
  });
  $(document).on("change", ".tablesorter :input", function() {
    $(this).closest("table").find(".exceptionRow").remove();
  });

});