$(document).ready(function() {
    $('#reload').click(function() {
        var hash = window.location.hash, arg;
        if (hash.indexOf("#") != -1) {
            $('#tab-content').children().removeClass('active');
            if (hash.indexOf("?") != -1) {
                arg = hash.substring(hash.indexOf("#"), hash.indexOf("?"));
            } else {
                arg = hash.substring(hash.indexOf("#"));
            }
            //console.log(arg + ' : ' + $('.navbar-nav li a').attr('href'));
            hash = hash.replace(arg, '#' + $('.main').find('div.active').attr('id'));
        } else {
            hash = hash + '#' + $('.main').find('div.active').attr('id');
        }
        console.log(window.location.origin + window.location.pathname + hash);
        window.location.replace(window.location.origin + window.location.pathname + hash);
        location.reload();
    });
    $('#start').click(function(){
        $.ajax({
            url: 'ps/start',
            success: function(data) {
                console.log(data);
                var response = jQuery.parseJSON(data);
                $('#status').val(response.status);
                $('#action').val(response.action);
            }
        })
    });
    $('#stop').click(function(){
        $.ajax({
            url: 'ps/stop',
            success: function(data) {
                console.log(data);
                var response = jQuery.parseJSON(data);
                $('#status').val(response.status);
                $('#action').val(response.action);
            }
        })
    });
    $('#refresh').click(function(){
        $.ajax({
            url: 'ps/status',
            success: function(data) {
                console.log(data);
                var response = jQuery.parseJSON(data);
                $('#status').val(response.status);
                $('#action').val(response.action);
            }
        })
    });
});

window.onload = function() {
    var hash = window.location.hash, arg;
    if (hash.indexOf("#") != -1) {
        $('#tab-content').children().removeClass('active');
        if (hash.indexOf("?") != -1) {
            arg = hash.substring(hash.indexOf("#"), hash.indexOf("?"));
        } else {
            arg = hash.substring(hash.indexOf("#"));
        }
        $('.navbar-nav a[href="' + arg + '"]').tab('show');
    }
};

function donePressed(row) {
    var ip = $('input[name="printers[' + row + '].ip"]').val();
    console.log(ip);
    $.ajax({
        url:path + '/ntap/response',
        type: "POST",
        data: {
            printer:ip
        },
        statusCode: {
            400: function () {
                console.log("Invalid DONE request made - 400");
                $.notify({
                    title: 'DONE request Failed: ',
                    message: 'Invalid DONE request made - 400',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            404: function () {
                console.log("DONE request could not be made - 404");
                $.notify({
                    title: 'DONE request Failed: ',
                    message: 'DONE request could not be made - 404',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            500: function() {
                console.log("Internal server error occurred while processing DONE request - 500");
                $.notify({
                    title: 'DONE request Failed: ',
                    message: 'Internal server error occurred while processing DONE request - 500',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            }
        },
        success: function (data) {
            console.log(data);
            $.notify({
                title: 'DONE Request Successful',
                message: data,
                icon: 'glyphicon glyphicon-ok'
            },{
                type: 'success'
            });
        }
    });
}

function reloadQueue() {
    $.ajax({
        url:path + '/ntap/queue/reload',
        type: "POST",
        statusCode: {
            400: function () {
                console.log("Invalid request made - 400");
                $.notify({
                    title: 'request Failed: ',
                    message: 'Invalid request made - 400',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            404: function () {
                console.log("request could not be made - 404");
                $.notify({
                    title: 'request Failed: ',
                    message: 'request could not be made - 404',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            500: function() {
                console.log("Internal server error occurred while processing request - 500");
                $.notify({
                    title: 'request Failed: ',
                    message: 'Internal server error occurred while processing request - 500',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            }
        },
        success: function (data) {
            console.log(data);
            $.notify({
                title: 'Request Successful',
                message: data,
                icon: 'glyphicon glyphicon-ok'
            },{
                type: 'success'
            });
        }
    });
}

function purgeQueue() {
    $.ajax({
        url:path + '/ntap/queue/reload',
        type: "POST",
        statusCode: {
            400: function () {
                console.log("Invalid request made - 400");
                $.notify({
                    title: 'request Failed: ',
                    message: 'Invalid request made - 400',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            404: function () {
                console.log("request could not be made - 404");
                $.notify({
                    title: 'request Failed: ',
                    message: 'request could not be made - 404',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            },
            500: function() {
                console.log("Internal server error occurred while processing request - 500");
                $.notify({
                    title: 'request Failed: ',
                    message: 'Internal server error occurred while processing request - 500',
                    icon: 'glyphicon glyphicon-remove'
                },{
                    type: 'danger'
                });
            }
        },
        success: function (data) {
            console.log(data);
            $.notify({
                title: 'Request Successful',
                message: data,
                icon: 'glyphicon glyphicon-ok'
            },{
                type: 'success'
            });
        }
    });
}