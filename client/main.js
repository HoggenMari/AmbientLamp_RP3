  var timeout;
  var clicker = 'mousedown';
  clicker = ('ontouchstart' in document.documentElement) ? 'touchstart' : 'mousedown';
 
  Template.settingsList.rendered = function(){
    console.log("test");
      //$('body').append("<script type='text/javascript' src='jquery.tinycolorpicker.js'>");
      
      
      $('body').append("<script type='text/javascript'> $(document).ready(function(){ var $box = $('#colorPicker');$box.tinycolorpicker();});</script>");
  };
    
  Template.leaderboard.helpers({
    players: function () {
      return Players.find({}, { sort: { score: -1, name: 1 } });
    },
    selectedName: function () {
      var player = Players.findOne(Session.get("selectedPlayer"));
      return player && player.name;
    }
  });

  Template.settingsList.helpers({
    settings: function () {
      return Settings.find({});
    },
    selectedSetting: function () {
      var setting = Settings.findOne(Session.get("selectedSetting"));
      return setting && setting.name;
    }
  });	

  Template.leaderboard.events({
    'click .inc': function () {
      Players.update(Session.get("selectedPlayer"), {$inc: {score: 5}});
    }
  });

  Template.settingsList.events({
    'click .inc': function () {
      Players.update(Session.get("selectedSetting"), {$inc: {score: 5}});
    }
  });

  Template.player.helpers({
    selected: function () {
      return Session.equals("selectedPlayer", this._id) ? "selected" : '';
    }
  });

  Template.setting.helpers({
    selected: function () {
      return Session.equals("selectedSetting", this._id) ? "selected" : '';
    }
  });

  Template.player.events({
    'click': function () {
      Session.set("selectedPlayer", this._id);
    }
  });

  function increase(setting) {
    var count = Settings.findOne(setting, {fields: {score: 1} });
    if(count.score<100){
    	Settings.update(setting, {$inc: {score: 1}});
    }
  }

  function decrease(setting) {
    var count = Settings.findOne(setting, {fields: {score: 1} });
    if(count.score>0){
    	Settings.update(setting, {$inc: {score: -1}});
    }
  }

  Template.setting.events({
    'touchstart .dec': function () {
      var setting = this._id;
      timeout = setInterval(function(){
      	decrease(setting);
      }, 25);
      return false;
    },
    'mousedown .dec': function () {
      var setting = this._id;
      timeout = setInterval(function(){
      	decrease(setting);
      }, 25);      
      return false;
    },
    'mouseup .dec': function () {
      clearInterval(timeout);
      return false;
    },
    'touchend .dec': function () {
      clearInterval(timeout);
      return false;
    },
    'touchstart .inc': function () {
      var setting = this._id;
      timeout = setInterval(function(){
      	increase(setting);
      }, 25);
      return false;
    },
    'mousedown .inc': function () {
      var setting = this._id;
      timeout = setInterval(function(){
      	increase(setting);
      }, 25);      
      return false;
    },
    'mouseup .inc': function () {
      clearInterval(timeout);
      return false;
    },
    'touchend .inc': function () {
      clearInterval(timeout);
      return false;
    },
    'click .inc': function() {
      var setting = this._id;
      increase(setting);
    },
    'click .dec': function() {
      var setting = this._id;
      decrease(setting);
    }
  });
