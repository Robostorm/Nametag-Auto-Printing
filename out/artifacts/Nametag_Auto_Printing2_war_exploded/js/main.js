function preview() {
    var name = $('#name').val();
    $.ajax({url: "ntap/preview.json",
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
                console.log("Internal server error occurred while processing request for preview image");
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

function submit() {
    var name = $('#name').val();
    $.ajax({url: "ntap/queue/add.json",
        type: "POST",
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
                console.log("Internal server error occurred while processing request for preview image");
            }
        },
        success: function (data) {
            console.log(data);
        }
    });
}

$(document).ready(function() {
    $('#preview').click(function() {
        preview();
    });

    $('#submit').click(function() {
        submit();
    });
});