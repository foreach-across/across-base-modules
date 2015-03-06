/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function makeExpandable() {
    var lastTarget;
    var fadeDuration = 200;
    var subscrTbl = $('table[data-tbl-entity-type]');
    var entityType = subscrTbl.data('tbl-entity-type');
    var rows = subscrTbl.find('tbody tr[data-entity-id],tbody tr[data-summary-url]');
    rows.click(function (evt) {
        if ($(evt.target).closest('a').length > 0) {
            // bubble edit button
            return true;
        }
        var target = $(this);
        if (lastTarget && lastTarget.context !== target.context) {
            lastTarget.next().fadeOut({duration: fadeDuration});
        }
        if (target.next().attr('data-summary') !== undefined) {
            target.next().fadeToggle({duration: fadeDuration, complete: function () {
                lastTarget = target;
            }});
        } else {
            var newTr = $('<tr data-summary style="display:none"></tr>');
            var newTd = $('<td colspan=' + $(this).find('td').length + '"></td>');
            var entityId = $(this).data('entity-id');
            var summaryUrl = $(this).data('summary-url');
            newTd.appendTo(newTr);
            target.after(newTr);
            if ( summaryUrl ) {
                newTd.load( summaryUrl, null, function() {
                    newTr.fadeIn({duration: fadeDuration});
                    lastTarget = target;
                });
            } else if( entityId ) {
                newTd.load( entityType + '/' + $(this).attr('data-entity-id') + '?view=summary&_partial=content', null, function() {
                    newTr.fadeIn({duration: fadeDuration});
                    lastTarget = target;
                });
            }
        }
    });
}

$(document).ready( function() {
    makeExpandable();
});