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