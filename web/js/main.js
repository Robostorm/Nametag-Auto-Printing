var $start;
var $end;

function loadRooms() {
    $.getJSON("js/rooms.json", function(data) {
        var rooms = data;
    });
}

function getData(f) {
    $.ajax({url: "navigator.html",
        data: {
            "start": $('#start').val(),
            "end": $('#end').val()
        },
        success: function (data, textStatus, jqXHR) {
            console.log(data);
            f(data);
        }
    });
}

function scroll(id) {
    $('html, body').stop().animate({
        scrollTop: $($('a[href=' + id +']').attr('href')).offset().top
    }, 1500, 'easeInOutExpo');
}

function preview() {
    var name = $('#name').val();
    $.ajax({url: "nap/preview.json",
        data: {
            "name": name
        },
        statusCode: {
            400: function () {
                console.log("Invalid request made for preview image");
            },
            404: function () {
                console.log("Request for preview image could not be made");
            },
            500: function() {
                console.log("Internal server error occued while maling request for preview image");
            }
        },
        success: function (data) {
            console.log(data);
            var response = jQuery.parseJSON(data);
            if(response.code === 0) {
                $('#preview-image').attr('src', response.image);
            } else {
                console.log(response.error);
            }
        }
    });
}

$(document).ready(function() {

    $('#toTop').click(function() {
        scroll("#page-top");
    });

    $('#preview').click(function() {
        preview();
    });
});