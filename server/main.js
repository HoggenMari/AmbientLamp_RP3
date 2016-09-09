//var ftpClient = require('ftp-client');
var fs        = require('fs');
var net       = require('net');
var os = Npm.require('os');
var JSFtp     = require("jsftp");
var LoggerFile = require("logger");
// Initialize Logger
this.log = new Logger();

// Initialize LoggerFile:
var LogFile = new LoggerFile(log, {
    fileNameFormat: function(time) {
        /* Create log-files hourly */
        return (time.getDate()) + "-" + (time.getMonth() + 1) + "-" + (time.getFullYear()) + "_" + (time.getHours()) + ".log";
    },
    format: function(time, level, message, data, userId) {
        /* Omit Date and hours from messages */
        var month = (time.getMonth()+1);
        var time_converted=('0'  + time.getHours()).slice(-2)+':'+('0'  + time.getMinutes()).slice(-2)+':'+('0' + time.getSeconds()).slice(-2);
        return "[" + level + "]," + message + "," + (time.getDate()) + "/" + (month) + "/" + (time.getYear()+1900) + " " + time_converted + "," + userId + "," + data + "\r\n";
    },
    path: '/logs' /* Use absolute storage path */
});

// Enable LoggerFile with default settings
LogFile.enable();

//log.info("System", "data", "192.168.0.100");

/*var countdown = new ReactiveCountdown(30, {

    // Value substracted every tick from the current countdown value
    steps: 1,

    // Specify the countdown's interval in milliseconds
    interval: 1000,

    // Callback: Tick, called on every interval
    tick: function() {
        //console.log("tick");
        Settings.update({name: "Genius"}, { $set: { geniusPausedRemain: countdown.get() }});
    },

    // Callback: Complete, called when the countdown has reached 0
    completed: function() {
        //console.log("finished");
        //Settings.update({name: "Genius"}, { $set: { geniusPausedRemain: 10 }});
    },

});*/

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
          geniusPauseTime: 30,
          geniusPausedRemain: 30
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

/*countdown.start(function() {

    // do something when this is completed
    countdown.stop();
    Settings.update({name: "Genius"}, {$set: {"geniusPaused": false}});
    Visuals.update({}, { $set: { active: false } }, { multi: true });
    console.log("finished countdown");
});*/

Meteor.onConnection(function(conn){
    //console.log(getIp());
    if(conn.clientAddress != getIp()) {
        //console.log("new Client connects" + conn.clientAddress);
        log.info("User", "Client connected", conn.clientAddress);

        conn.onClose(function() {
            //console.log("new client disconnects"+conn.clientAddress);
            log.info("User", "Client disconnected", conn.clientAddress);
        });
    }
});

var getIp = function() {
    // Get interfaces
    var netInterfaces = os.networkInterfaces();
    // Result
    var result;
    for (var id in netInterfaces) {
        var netFace = netInterfaces[id];

        for (var i = 0; i < netFace.length; i++) {
            var ip = netFace[i];
            if (ip.internal === false && ip.family === 'IPv4') {
                result = ip.address;
            }
        }
    }
    return result;
};



Meteor.methods({
    'visual.setChecked': function(visualId, setChecked) {
        const visual = Visuals.findOne(visualId);
        Visuals.update(visualId, { $set: { checked: setChecked } });
    },
    'visual.setActive': function(visualId, setActive) {
        const visual = Visuals.findOne(visualId);
        if(Settings.findOne({name: "Genius"}).geniusActive==true){
            Settings.update({name: "Genius"}, { $set: { geniusPaused: true }});
        }
        Visuals.update({}, { $set: { active: false } }, { multi: true })
        Visuals.update({}, { $set: { geniusActive: false } }, { multi: true });
        Visuals.update(visualId, { $set: { active: setActive } });
    },
    'visual.setPaused': function(visualId, setActive) {
        console.log("setpaused");
        if(Settings.findOne({name: "Genius"}).geniusActive==true){
          Settings.update({name: "Genius"}, { $set: { geniusPaused: true }});
          Visuals.update(visualId, { $set: { pausedActive: true } });
        }
    },
    'visual.finishPaused': function(visualId, setActive) {
        console.log("finishPaused");
        if(Settings.findOne({name: "Genius"}).geniusActive==true){
          Visuals.update(visualId, { $set: { pausedActive: false } });
          Settings.update({name: "Genius"}, { $set: { geniusPaused: false }});
        }
    },
    'visual.setSetting': function(visualId, setActive) {
        const visual = Visuals.findOne(visualId);
        //Visuals.update({}, { $set: { active: false } }, { multi: true })
        //Visuals.update({}, { $set: { settingActive: false } }, { multi: true });
        Visuals.update(visualId, { $set: { settingActive: setActive } });
    },
    'visual.finished': function() {
        Visuals.update({}, { $set: { settingActive: false } }, { multi: true });
        //Settings.update({name: "Genius"}, {$set: {"geniusPaused": false}});
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
            //countdown.stop();
            //countdown.add(-countdown.get());
            //countdown.remove();
            Settings.update({name: "Genius"}, { $set: { geniusPaused: false }});
            var visualActive = Visuals.findOne({ geniusActive: true });
            console.log("visualActive");
            console.log(visualActive._id);
            Visuals.update({}, { $set: { geniusActive: false } }, { multi: true });
            Visuals.update(visualActive._id, { $set: { active: true } })
        }else{
            Visuals.update({}, { $set: { active: false } }, { multi: true });
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
            /*if (ret.collection == "settings") {
                if (ret.msg == "changed") {
                    console.log("geniusPaused");
                    console.log(ret.fields.geniusPaused);
                    console.log(ret.id);
                    //Visuals.update(ret.id, {})
                    Settings.update({_id: ret.id}, {$set: {"geniusPaused": ret.fields.geniusPaused}});
                }
            }*/
        }
        //Visuals.update(ret);
    },
    'startCountdown': function(option) {
        /*if(countdown.get()==0) {
            countdown.start();
        }else{
            countdown.add(Settings.findOne({name: "Genius"}).geniusPauseTime-countdown.get());
        }*/
    },
    'getCountdownMethod': function(argument) {
        //return countdown.get();
    },
    'logID': function(ip, id, msg) {
        var _time = (new Date).toTimeString();
        // log.info(msg, id, ip);
        //const visual = Visuals.findOne(id);
        var message = id.name + " " +msg;
        log.info("User", message, ip);
        console.log(_time, ip, id, msg);
    },
    'logIDColor': function(ip, id, msg) {
        var _time = (new Date).toTimeString();
        // log.info(msg, id, ip);
        //const visual = Visuals.findOne(id);
        var message = id + " " +msg;
        log.info("User", message, ip);
        console.log(_time, ip, id, msg);
    },
    'log': function(ip, msg) {
        // log.info(msg, id, ip);
        log.info("User", msg, ip);
        //console.log(_time, ip, id, msg);
    }
});
