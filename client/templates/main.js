  var timeout;
  var clicker = 'mousedown';
  clicker = ('ontouchstart' in document.documentElement) ? 'touchstart' : 'mousedown';

  Router.configure({
    layoutTemplate: 'mainPage'
  });

  Router.route('/', {
    name: 'leaderboard',
    template: 'leaderboard'
  });

  Router.route('/list/:_id', {
    name: 'listPage',
    template: 'listPage',
    data: function(){
      var currentList = this.params._id;
      return Visuals.findOne({ _id: currentList });
    }
  });

  Template.listPage.rendered = function() {
      console.log(Visuals.find({}));

  }

  Template.header.rendered = function (){

    elem.onchange = function() {
      console.log("clicked button");


      Checkbox.findAndModify(
          {name: 'Lamp'}, // query
          [['_id','asc']],  // sort order
          {$set: {checkedValue: elem.checked}}, // replacement, replaces only the field "hi"
          {}, // options
          function(err, object) {
            if (err){
              console.warn(err.message);  // returns error if no matching object found
            }else{
              console.dir(object);
            }
          });
    };

  }

  Template.settingsList.rendered = function(){
    console.log("test");
      
      $('body').append("<script type='text/javascript' src='tinycolorpicker.js'></script>");
      
      
      $('body').append("<script type='text/javascript'>window.onload = function(){var $picker = document.getElementById('colorPicker'),picker  = tinycolorpicker($picker);var $picker = document.getElementById('colorPicker2'),picker  = tinycolorpicker($picker);}</script>");

    var elem = document.querySelector('.js-switch');
    var init = new Switchery(elem);

  };

  Template.settingsList.helpers({
    settings: function () {
      return Settings.find({});
    },
    selectedSetting: function () {
      var setting = Settings.findOne(Session.get("selectedSetting"));
      return setting && setting.name;
    }
  });

  Template.settingsList.events({
    'click .inc': function () {
      Settings.update(Session.get("selectedSetting"), {$inc: {score: 5}});
    }
  });

  Template.setting.helpers({
    selected: function () {
      return Session.equals("selectedSetting", this._id) ? "selected" : '';
    }
  });

  Template.leaderboard.helpers({
    visuals: function () {
      return Visuals.find({});
    },
    selectedSetting: function () {
      var visual = Visuals.findOne(Session.get("selectedSetting"));
      return visual && visual.name;
    }
  });

  Template.visual.helpers({
    selected: function () {
      return Session.equals("selectedSetting", this._id) ? "selected" : '';
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
