function setDeleteListener(){
    $('.delete').click(function () {
        if($(this).hasClass('new')) {
            $(this).parent().parent().remove();
            num--;
        } else {
            if ($(this).parent().find('input[type=hidden]').val() == 'false') {
                console.log('Delete');
                $(this).parent().find('input[type=hidden]').val(true);
                $(this).removeClass('btn-danger');
                $(this).addClass('btn-warning ');
                $(this).attr('value', 'Undo');
            } else {
                console.log('Undo');
                $(this).parent().find('input[type=hidden]').val(false);
                $(this).removeClass('btn-warning ');
                $(this).addClass('btn-danger');
                $(this).attr('value', 'Delete');
            }
        }
    });
}

function setAddListener(){
    $('#addPrinter').click(function () {
        $('.table tr:nth-child(' + num + 'n)').after(
            $('<tr>').addClass('form-inline').append(
                $('<input>').attr('type', 'hidden')
                    .attr('name', 'printers[' + num + '].id')
                    .attr('value', '-1'),
                $('<td>').append(
                    $('<input>').addClass('form-control')
                        .attr('type', 'text')
                        .attr('name', 'printers[' + num + '].name')
                ),
                $('<td>').append(
                    $('<input>').addClass('form-control')
                        .attr('type', 'text')
                        .attr('name', 'printers[' + num + '].ip')
                ),
                $('<td>').append(
                    $('<input>').addClass('form-control')
                        .attr('type', 'text')
                        .attr('name', 'printers[' + num + '].port')
                ),
                $('<td>').append(
                    $('<input>').addClass('form-control')
                        .attr('type', 'text')
                        .attr('name', 'printers[' + num + '].apiKey')
                ),
                $('<td>').append(
                    $('<input>').addClass('form-control')
                        .attr('type', 'text')
                        .attr('name', 'printers[' + num + '].configFile')
                ),
                $('<td>').append(
                    $('<input>')
                        .attr('type', 'checkbox')
                        .attr('name', 'printers[' + num + '].printing')
                        .attr('value', 'false')
                ),
                $('<td>').append(
                    $('<input>')
                        .attr('type', 'checkbox')
                        .attr('name', 'printers[' + num++ + '].active')
                        .attr('value', 'false')
                ),
                $('<td>').append(
                    $('<input>').addClass('btn btn-danger delete new')
                        .attr('type', 'button')
                        .attr('value', 'Delete')
                )
            )
        );
        setDeleteListener();
    });
}


$(document).ready(function () {
    setDeleteListener();
    setAddListener();
});