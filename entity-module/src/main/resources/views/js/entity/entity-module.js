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
var TablePager = function ( element )
{
    var table = $( element );
    var id = $( element ).attr( 'data-tbl' );
    var page = table.attr( 'data-tbl-page' );
    var size = table.attr( 'data-tbl-size' );
    var sort = [];

    var tblSort = table.attr( 'data-tbl-sort' );

    var props = tblSort ? tblSort.split( ',' ) : [];

    var sortables = $( "[data-tbl='" + id + "'][data-tbl-sort-property]" );

    sortables.removeClass( 'dropup', 'dropdown' );

    for ( var i = 0; i < props.length; i++ ) {
        var pairs = props[i].split( ':' );
        sort.push( { property: pairs[0], direction: pairs[1].trim() } );

        $( "[data-tbl='" + id + "'][data-tbl-sort-property='" + pairs[0] + "']" ).addClass(
                pairs[1].trim() == 'ASC' ? 'dropdown' : 'dropup'
        )
    }

    var pager = this;

    $( "[data-tbl='" + id + "'][data-tbl-page]" ).click( function ()
                                                         {
                                                             pager.moveToPage( $( this ).attr( 'data-tbl-page' ) );
                                                             return false;
                                                         } );

    sortables.click( function ()
                     {
                         pager.sortOnProperty( $( this ).attr( 'data-tbl-sort-property' ) );
                         return false;
                     } );

    this.moveToPage = function ( pageNumber )
    {
        var sortValue = '';

        for ( var i = 0; i < sort.length; i++ ) {
            if ( sortValue.length > 0 ) {
                sortValue += '&';
            }

            sortValue += sort[i].property + ',' + sort[i].direction;
        }

        var url = paramReplace( 'sort', window.location.href, sortValue );
        url = paramReplace( 'page', url, pageNumber );
        url = paramReplace( 'size', url, size );

        window.location.href = url;
        //alert('moving ' + table + ' to ' + pageNumber );
    };

    this.sortOnProperty = function ( propertyName )
    {
        var current;
        for ( var i = 0; i < sort.length; i++ ) {
            if ( sort[i].property == propertyName ) {
                current = sort[i];
            }
        }

        if ( current ) {
            current.direction = current.direction == 'ASC' ? 'DESC' : 'ASC';
        }
        else {
            sort = [];
            current = { property: propertyName, direction: 'ASC' };
            sort.push( current );
        }

        this.moveToPage( page );
    };
};

function paramReplace( name, string, value )
{
    // Find the param with regex
    // Grab the first character in the returned string (should be ? or &)
    // Replace our href string with our new value, passing on the name and delimeter

    var re = new RegExp( "[\\?&]" + name + "=([^&#]*)" );
    var matches = re.exec( string );
    var newString;

    if ( matches === null ) {
        // if there are no params, append the parameter
        if ( string.indexOf( '?' ) >= 0 ) {
            newString = string + '&' + name + '=' + value;
        }
        else {
            newString = string + '?' + name + '=' + value;
        }
    }
    else {
        var delimeter = matches[0].charAt( 0 );
        newString = string.replace( re, delimeter + name + "=" + value );
    }
    return newString;
}

$( document ).ready( function ()
                     {
                         $( '[data-tbl-type="paged"]' ).each( function ()
                                                              {
                                                                  new TablePager( $( this ) );
                                                              } );
                     } );