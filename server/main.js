import { Settings } from '../imports/api/lists/settings.js';

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
    if (Settings.find().count() === 0) {
      var names = ["Brightness", "Saturation"];
      _.each(names, function (name) {
        Settings.insert({
          name: name,
          score: 0
        });
      });	
    }

  });