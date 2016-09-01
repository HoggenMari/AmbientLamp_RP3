//var ftpClient = require('ftp-client');
var fs        = require('fs');
var net       = require('net');
var JSFtp     = require("jsftp");

var countdown = new ReactiveCountdown(10, {

    // Value substracted every tick from the current countdown value
    steps: 1,

    // Specify the countdown's interval in milliseconds
    interval: 1000,

    // Callback: Tick, called on every interval
    tick: function() {
        console.log("tick");
        Settings.update({name: "Genius"}, { $set: { geniusPausedRemain: countdown.get() }});
    },

    // Callback: Complete, called when the countdown has reached 0
    completed: function() {
        console.log("finished");
        Settings.update({name: "Genius"}, { $set: { geniusPausedRemain: 10 }});
    },

});

Meteor.startup(function () {
    /*if (Players.find().count() === 0) {
      var names = ["Ada Lovelace", "Grace Hopper", "Marie Curie",
                   "Carl Friedrich Gauss", "Nikola Tesla", "Claude Shannon"];
      _.each(names, function (name) {
        Players.insert({
          name: name,
          score: 0
        });
      });
    }*/



    var ftp = new JSFtp({
        host: "server47.webgo24.de",
        port: 21,
        user: "web157f2",
        pass: "mediaarch"
    });


    var os     = require('os');
    var ifaces = os.networkInterfaces();

    var localIP = "172.20.19.217";

    'use strict';

    Object.keys(ifaces).forEach(function(ifname) {
        var alias = 0;

        ifaces[ifname].forEach(function(iface){

            console.log(iface);

            if ('IPv4' !== iface.family || iface.internal !== false) {
                // skip over internal (i.e. 127.0.0.1) and non-ipv4 addresses
                return;
            }

            if (alias >= 1) {
                // this single interface has multiple ipv4 addresses
                console.log(ifname + ':' + alias, iface.address);
                console.log("Auto Upload will not work");
            } else {
                // this interface has only one ipv4 adress
                localIP = iface.address;
                console.log(localIP);

                fs.writeFile("ipaddress", iface.address, function(err) {
                    if (err) {
                        return console.log(err);
                    }

                    ftp.put('ipaddress', '/Marius', function(hadError) {
                        if (!hadError) {
                            console.log("FTP upload successful!");
                        } else {
                            console.log("Error: " + hadError);
                        }

                    });
                });
            }
            ++alias;
        });

        if (alias == 0) {
            console.log("No network interface detected!");
        }
    });





    /*config = {
        host: 'ftp.proppe.me',
        port: 21,
        user: 'riskygadgets@proppe.me',
        password: 'urbaninformatics2016'
    },
        options = {
            logging: 'basic'
        },
        client = new ftpClient(config, options);

    client.connect(function () {

        client.upload(['test/**'], '/public_html/test', {
            baseDir: 'test',
            overwrite: 'older'
        }, function (result) {
            console.log(result);
        });

        client.download('/public_html/test2', 'test2/', {
            overwrite: 'all'
        }, function (result) {
            console.log(result);
        });

    });*/


    if (Settings.find().count() === 0) {
      var names = ["Brightness"];
      _.each(names, function (name) {
        Settings.insert({
          name: name,
          score: 100
        });
      });
      var names = ["Saturation"];
      _.each(names, function (name) {
          Settings.insert({
              name: name,
              score: 100
          });
      });
      var names = ["Settings"];
      _.each(names, function(name) {
        Settings.insert({
          name: name,
          index: 0,
          isActive: false
        });
      });
      var names = ["Genius"];
      _.each(names, function(name) {
        Settings.insert({
          name: name,
          geniusActive: false,
          geniusPaused: false,
          geniusPauseTime: 5000,
          geniusPausedRemain: 10
        });
      });
    }

    if (Checkbox.find().count() === 0) {
      var names = ["Lamp"];
      _.each(names, function (name) {
        Checkbox.insert({
          name: name,
          checkedValue: true
        });
      });
    }





    if(Visuals.find().count() == 0) {


        var myjson = {};
        myjson = JSON.parse(Assets.getText("settings.json"));
        console.log(myjson.Visuals[0]);

        _.each(myjson.Visuals, function(visual) {
            Visuals.insert(visual)
        });


        /*var names = ["Visual 1"];
        var index = 0;
        _.each(names, function (name) {
            Visuals.insert({
                name: name,
                index: index,
                colors: [ { color: "#FFFFFF", index: 0},
                          { color: "#FFFFFF", index: 1},
                          { color: "#FFFFFF", index: 2},
                          { color: "#FFFFFF", index: 3}],
                checked: false,
                active: false
            });
            index++;
        });
        var names = ["Visual 2"];
        _.each(names, function (name) {
            Visuals.insert({
                name: name,
                index: index,
                colors: [ { color: "#FFFFAF", index: 0},
                    { color: "#FFFFFF", index: 1}],
                checked: false,
                active: false
            });
            index++;
        });
        var names = ["Visual 3", "Visual 4"];
        _.each(names, function (name) {
            Visuals.insert({
                name: name,
                index: index,
                colors: [ { color: "#FFFFAF", index: 0},
                          { color: "#FFFFFF", index: 1},
                          { color: "#FFFFFF", index: 2}],
                checked: false,
                active: false
            });
            index++;
        });*/
    }
  });

countdown.start(function() {

    // do something when this is completed
    countdown.stop();
    console.log("finished countdown");
});

Meteor.methods({
    'visual.setChecked': function(visualId, setChecked) {
        const visual = Visuals.findOne(visualId);
        Visuals.update(visualId, { $set: { checked: setChecked } });
    },
    'visual.setActive': function(visualId, setActive) {
        const visual = Visuals.findOne(visualId);
        Visuals.update({}, { $set: { active: false } }, { multi: true })
        Visuals.update(visualId, { $set: { active: setActive } });
        if(Settings.findOne({name: "Genius"}).geniusActive==true){
            Settings.update({name: "Genius"}, { $set: { geniusPaused: true }});
        }
    },
    'notification.setChecked': function(visualId, setChecked) {
        console.log("notification setChecked");
        const visual = Visuals.findOne(visualId);
        Visuals.update(visualId, { $set: { notification: setChecked } });
    },
    'updateColor': function(id, index, color) {
        Visuals.update({_id: id, "colors.index": index}, { $set: { "colors.$.color": color}});
    },
    'reset': function(id) {
        console.log("RESET");
        var myjson = {};
        myjson = JSON.parse(Assets.getText("settings.json"));
        //console.log(myjson.Visuals[0]);
        console.log(Visuals.findOne(id).name);
        var visual = null;
        for(var i=0; i<myjson.Visuals.length; i++){
            if(myjson.Visuals[i].name==Visuals.findOne(id).name){
                console.log("test");
                visual = myjson.Visuals[i];
                break;
            }
        }
        console.log(visual.colors);
        const visual_db = Visuals.findOne(id);
        visual_db.colors = visual.colors;
        Visuals.update(id, visual_db);

        //if(Visuals.findOne(id).name=="Visual 1"){
        //    console.log("Visual 1");
        //}
        //console.log(myjson.Visuals);
    },
    'genius': function(setting) {
        Settings.update({name: "Genius"}, { $set: { geniusActive: setting }});
        if(setting==false){
            Visuals.update({}, { $set: { geniusActive: false } }, { multi: true })
        }
    },
    'update': function(options) {
        var ret = JSON.parse(options);
        console.log("call from java");
        //console.log(ret.msg);
        if(Settings.findOne({name: "Genius"}).geniusActive==true) {
            if (ret.collection == "visuals") {
                if (ret.msg == "changed") {
                    console.log(ret.fields.geniusActive);
                    //Visuals.update(ret.id, {})
                    Visuals.update({_id: ret.id}, {$set: {"geniusActive": ret.fields.geniusActive}});
                }
            }
            if (ret.collection == "settings") {
                if (ret.msg == "changed") {
                    console.log("geniusPaused");
                    console.log(ret.fields.geniusPaused);
                    console.log(ret.id);
                    //Visuals.update(ret.id, {})
                    Settings.update({_id: ret.id}, {$set: {"geniusPaused": ret.fields.geniusPaused}});
                }
            }
        }
        //Visuals.update(ret);
    },
    'startCountdown': function(option) {
        if(countdown.get()==0) {
            countdown.start();
        }
    },
    'getCountdownMethod': function(argument) {
        return countdown.get();
    }
});
