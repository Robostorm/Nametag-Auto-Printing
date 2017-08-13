/*
█████       ██  █████  ██   ██
██   ██      ██ ██   ██  ██ ██
███████      ██ ███████   ███
██   ██ ██   ██ ██   ██  ██ ██
██   ██  █████  ██   ██ ██   ██
*/

Backbone.ajax = function() {
  var args = Array.prototype.slice.call(arguments, 0);

  if(args[0].type === "PUT"){
    args[0].type = "POST"
    args[0].url = args[0].url.match("/[^/]*") + "/update"
  }

  if(args[0].type === "DELETE"){
    args[0].type = "POST"
    var id = Number(args[0].url.match("[0-9]*$")[0])
    //console.log(args[0].url.match("[0-9]*$"));
    var nametag = {
      "id": ""
    }
    nametag.id = id
    var body = JSON.stringify(nametag)
    args[0].data = body
    args[0].url = args[0].url.match("/[^/]*") + "/delete"
  }

  //console.log(args);

  return Backbone.$.ajax.apply(Backbone.$, args);
};

/*
███    ██  █████  ███    ███ ███████ ████████  █████   ██████  ███████
████   ██ ██   ██ ████  ████ ██         ██    ██   ██ ██       ██
██ ██  ██ ███████ ██ ████ ██ █████      ██    ███████ ██   ███ ███████
██  ██ ██ ██   ██ ██  ██  ██ ██         ██    ██   ██ ██    ██      ██
██   ████ ██   ██ ██      ██ ███████    ██    ██   ██  ██████  ███████
*/

var Nametag = Backbone.Model.extend({
  idAttribute: "id"
});

var Nametags = Backbone.Collection.extend({
  model: Nametag,
  url: "/nametags"
})

var nametags = new Nametags()

nametags.fetch()

window.setInterval(function(){
  nametags.fetch()
}, 1000)

nametags.on("change", function(nametag){
  console.log("Nametag Change!")
  console.log(nametag)
  nametag.save()
})

var NametagColumns = Backgrid.Columns.extend({
  url: "/nametags/columns"
})

var nametagColumns = new NametagColumns()

nametagColumns.fetch().done(function(){
  nametagColumns.create({
    name: 'done',
    label: '',
    editable: false,
    cell: Backgrid.Cell.extend({
      id: 0,
      events: {
        'click button': 'done'
      },
      done: function(e){
        $.ajax({
          data: JSON.stringify({
            "id": Number(e.target.id.match("[0-9]*$")[0])
          }),
          type: "POST",
          url: "nametags/done"
        })
      },
      render: function () {
        this.$el.html('<button class=\'done\' id=\'nametagDelete'+this.model.get("id")+'\'>Done</button>').addClass("cell-btn");
        return this;
      }
    })
  })
  nametagColumns.create({
    name: 'delete',
    label: '',
    editable: false,
    cell: Backgrid.Cell.extend({
      id: 0,
      events: {
        'click button': 'deleteRow'
      },
      deleteRow: function(e){
        nametags.get(e.target.id.match("[0-9]*$")).destroy()
      },
      render: function () {
        this.$el.html('<button class=\'delete\' id=\'nametagDelete'+this.model.get("id")+'\'>Delete</button>').addClass("cell-btn");
        return this;
      }
    })
  })

  console.log(nametagColumns);

  for(var i = 0; i < nametagColumns.length; i++){
    console.log(nametagColumns.models[i].attributes);
  }

  // Initialize a new Grid instance
  var grid = new Backgrid.Grid({
    columns: nametagColumns,
    collection: nametags
  })

  // Render the grid and attach the root to your HTML document
  $("#nametagTable").append(grid.render().el);
})

/*
█████  ██████  ██████
██   ██ ██   ██ ██   ██
███████ ██   ██ ██   ██
██   ██ ██   ██ ██   ██
██   ██ ██████  ██████
*/

$("#nametagAdd").click(function(){
  $.ajax({
    data: JSON.stringify({
      "name": $("#nametagInput").val()
    }),
    type: "POST",
    url: "nametags/update"
  })
  $("#nametagInput").val("")
})

/*
██████  ██████  ██ ███    ██ ████████ ███████ ██████  ███████
██   ██ ██   ██ ██ ████   ██    ██    ██      ██   ██ ██
██████  ██████  ██ ██ ██  ██    ██    █████   ██████  ███████
██      ██   ██ ██ ██  ██ ██    ██    ██      ██   ██      ██
██      ██   ██ ██ ██   ████    ██    ███████ ██   ██ ███████
*/



var Printer = Backbone.Model.extend({
  idAttribute: "id"
});

var Printers = Backbone.Collection.extend({
  model: Printer,
  url: "/printers"
})

var printers = new Printers()

printers.fetch()
window.setInterval(function(){
  printers.fetch()
}, 1000)

printers.on("change", function(printer){
  console.log("Printer Change!")
  console.log(printer)
  printer.save()
})

var PrinterColumns = Backgrid.Columns.extend({
  url: "/printers/columns"
})

var printerColumns = new PrinterColumns()

printerColumns.fetch().done(function(){

  printerColumns.create({
    name: 'abort',
    label: '',
    editable: false,
    cell: Backgrid.Cell.extend({
      id: 0,
      events: {
        'click button': 'abort'
      },
      abort: function(e){
        $.ajax({
          data: JSON.stringify({
            "id": Number(e.target.id.match("[0-9]*$")[0])
          }),
          type: "POST",
          url: "printers/abort"
        })
      },
      render: function () {
        this.$el.html('<button class=\'abort\' id=\'printerDelete'+this.model.get("id")+'\'>Abort</button>').addClass("cell-btn");
        return this;
      }
    })
  })

  printerColumns.create({
    name: 'done',
    label: '',
    editable: false,
    cell: Backgrid.Cell.extend({
      id: 0,
      events: {
        'click button': 'done'
      },
      done: function(e){
        $.ajax({
          data: JSON.stringify({
            "id": Number(e.target.id.match("[0-9]*$")[0])
          }),
          type: "POST",
          url: "printers/done"
        })
      },
      render: function () {
        this.$el.html('<button class=\'done\' id=\'printerDelete'+this.model.get("id")+'\'>Done</button>').addClass("cell-btn");
        return this;
      }
    })
  })

  printerColumns.create({
    name: 'delete',
    label: '',
    editable: false,
    cell: Backgrid.Cell.extend({
      id: 0,
      events: {
        'click button': 'deleteRow'
      },
      deleteRow: function(e){
        printers.get(e.target.id.match("[0-9]*$")).destroy()
      },
      render: function () {
        this.$el.html('<button class=\'delete\' id=\'printerDelete'+this.model.get("id")+'\'>Delete</button>').addClass("cell-btn");
        return this;
      }
    })
  })

  // Initialize a new Grid instance
  var grid = new Backgrid.Grid({
    columns: printerColumns,
    collection: printers
  })

  // Render the grid and attach the root to your HTML document
  $("#printerTable").append(grid.render().el);
})

/*
█████  ██████  ██████
██   ██ ██   ██ ██   ██
███████ ██   ██ ██   ██
██   ██ ██   ██ ██   ██
██   ██ ██████  ██████
*/

$("#printerAdd").click(function(){
  $.ajax({
    data: JSON.stringify({
      "name": $("#printerInput").val()
    }),
    type: "POST",
    url: "printers/update"
  })
})

/*
███    ███  █████  ███    ██  █████   ██████  ███████ ██████
████  ████ ██   ██ ████   ██ ██   ██ ██       ██      ██   ██
██ ████ ██ ███████ ██ ██  ██ ███████ ██   ███ █████   ██████
██  ██  ██ ██   ██ ██  ██ ██ ██   ██ ██    ██ ██      ██   ██
██      ██ ██   ██ ██   ████ ██   ██  ██████  ███████ ██   ██
*/

setInterval(function(){
  $.ajax({
    type: "GET",
    url: "manager/state",
    complete: function(request, state){
      if(state === "success"){
        console.log(request)
        if(request.responseJSON.managing){
          $("#managerState").text("Manager Running").css("background-color", "limegreen")
        }else{
          $("#managerState").text("Manager Stopped").css("background-color", "red")
        }
        if(request.responseJSON.currentCommand === ""){
          $("#currentCommand").text("Idle")
        }else{
          $("#currentCommand").text(request.responseJSON.currentCommand.split(' ')[0])
        }
      }
    }
  })
}, 1000)

$("#managerStart").click(function(){
  $.ajax({
    data: JSON.stringify({
      "managing": true
    }),
    type: "POST",
    url: "manager/state"
  })
})

$("#managerStop").click(function(){
  $.ajax({
    data: JSON.stringify({
      "managing": false
    }),
    type: "POST",
    url: "manager/state"
  })
})
