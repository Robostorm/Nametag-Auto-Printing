function getPrinters() {
    $.ajax({
        url: "ntap/printers/view",
        success: function (data) {
            response = JSON.parse(data);
            $.each(response, function (i, item) {
                var tr = $('<form>').addClass("form-inline").append(
                    $('<tr>').append(
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("Name"),
                            $('<input>').addClass("form-control")
                                .attr('size', (item.name.length > 4 ? item.name.length - 3 : 1))
                                .attr('value', item.name)
                                .attr('readonly', true)
                        ),
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("IP"),
                            $('<input>').addClass("form-control")
                                .attr('size', (item.ip.length > 4 ? item.ip.length - 3 : 1))
                                .attr('value', item.ip)
                                .attr('readonly', true)
                        ),
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("Port"),
                            $('<input>').addClass("form-control")
                                .attr('size', (item.port.length > 4 ? item.port.length - 3 : 1))
                                .attr('value', item.port)
                                .attr('readonly', true)
                        ),
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("Api-Key"),
                            $('<input>').addClass("form-control")
                                .attr('size', (item.apiKey.length > 4 ? item.apiKey.length - 3 : 1))
                                .attr('value', item.apiKey)
                                .attr('readonly', true)
                        ),
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("Active"),
                            $('<input>').attr('type', 'checkbox')
                                .prop('checked', item.active)
                                .attr('onclick', "return false")
                        ),
                        $('<td>').append(
                            $('<label>').addClass("control-label").text("Printing"),
                            $('<input>').attr('type', 'checkbox')
                                .prop('checked', item.printing)
                                .attr('onclick', "return false")
                        )
                    )
                ).appendTo("#printers");
            });
        }
    });
}

function resizeInput() {
    //console.log(this);
    $(this).css('width', $(this).val().length * 6);
}

$(document).ready(function() {
    //getPrinters();
    //console.log($('.resize'));
    //$('.resize').each(resizeInput);
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