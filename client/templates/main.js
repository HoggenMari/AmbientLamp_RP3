  var timeout;
  var clicker = 'mousedown';
  clicker = ('ontouchstart' in document.documentElement) ? 'touchstart' : 'mousedown';
  var counter = 0;
  var currentID = 0;
  var cols, colsOld = 0;
  var visualBol = false;
  var resultElement = document.getElementById('result'),
      sliders = document.getElementsByClassName('sliders');
  var countdown = new ReactiveCountdown(10);
  var settingsUp = false;

  Meteor.call('getCountdownMethod', "foo", function(error, result){
      Session.set('myMethodResult', result);
  });

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
      //currentID = this.params._id;
      //console.log(this.params._id);
      currentID = this.params._id;
      return Visuals.findOne({ _id: currentList });
    }
  });

  Template.listPage.rendered = function() {

      console.log("COLOR COLOR");

      //counter = 0;
      console.log(Visuals.find({}));
      console.log(currentID);
      cols = Visuals.findOne({ _id: currentID }, {fields: {colors: 1}});
      console.log(cols);

      var $picker = document.getElementsByClassName('colorPicker');

      console.log($picker);

      visualBol = true;

      /*for ( var i = 0; i < sliders.length; i++ ) {

          noUiSlider.create(sliders[i], {
              start: 127,
              connect: "lower",
              orientation: "vertical",
              range: {
                  'min': 0,
                  'max': 255
              }
          });

          // Bind the color changing function
          // to the slide event.
          sliders[i].noUiSlider.on('slide', setColor);
      }*/
      //var $picker = document.getElementById('colorPicker');
      //picker = tinycolorpicker($picker);

      //for (i = 0; i < cols.length; i++) {
      //    $('#colors').append("<div id='colorPicker" + i + "' class='colorPicker'><a class='color'><div class='colorInner'></div></a><div class='track'></div><ul class='dropdown'><li></li></ul> <input type='hidden' class='colorInput'/></div>");
      //}
  }


  function setColor(){


      // Get the slider values,
      // stick them together.
      var color = 'rgb(' +
          sliders[0].noUiSlider.get() + ',' +
          sliders[1].noUiSlider.get() + ',' +
          sliders[2].noUiSlider.get() + ')';

      // Fill the color box.
      resultElement.style.background = color;
      resultElement.style.color = color;
  }

  Template.header.rendered = function (){


      //var elem = document.querySelector('.genius');
      //var init = new Switchery(elem, { color: '#6f47a8', jackColor: '#ffffff' });


    //$('body').append("<script type='text/javascript' src='tinycolorpicker.js'></script>");

    //var $picker = document.getElementById('colorPicker');
    //picker  = tinycolorpicker($picker);

    //$('body').append("<script type='text/javascript'>window.onload = function(){var $picker = document.getElementById('colorPicker'),picker  = tinycolorpicker($picker);var $picker = document.getElementById('colorPicker2'),picker  = tinycolorpicker($picker);}</script>");



    /*elem.onchange = function() {
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
    };*/

  }

  Template.settingsList.rendered = function(){
    console.log("test");
      /*$(".settings").swipe( {
          //Generic swipe handler for all directions
          swipe:function(event, direction, distance, duration, fingerCount, fingerData) {
            //console.log("swipe test"+direction);
              if(direction == "up"){
                  console.log("swipe test"+direction);
                  moveSettings();
              }
          },
          //Default is 75px, set to 0 for demo so any distance triggers swipe
          threshold:0
      });*/

  };

  Template.settingsList.helpers({
    settings: function () {
      return Settings.find({});
    },
    selectedSetting: function () {
      var setting = Settings.findOne(Session.get("selectedSetting"));
      return setting && setting.name;
    },
    display: function() {
      console.log(this.name);
      if(this.name == "Brightness" || this.name == "Saturation") {
          return true;
      }else{
          return false;
      }
    }
  });

  function moveSettings() {
      var myElement = document.querySelector(".settings");
      settingsUp = !settingsUp;
      if(settingsUp) {
          $(".settings").animate({
              'marginBottom': '+=60px'
          }, 250);
      }else{
          //myElement.style.marginBottom = "-60px";
          $(".settings").animate({
              'marginBottom': '-=60px'
          }, 250);
      }
  }

  Template.settingsList.events({
    'click .inc': function () {
      Settings.update(Session.get("selectedSetting"), {$inc: {score: 5}});
    },
    'mousedown': function() {
          //console.log("click snap");
          moveSettings();
      }


      /*$("#test").swipe( {
      //Generic swipe handler for all directions
      swipe:function(event, direction, distance, duration, fingerCount, fingerData) {
          $(this).text("You swiped " + direction );
      },
      //Default is 75px, set to 0 for demo so any distance triggers swipe
      threshold:0
      });*/

      /*,
      'touchend': function() {
          console.log("click snap");
          var myElement = document.querySelector(".settings");
          if(settingsUp) {
              myElement.style.marginBottom = "0px";
          }else{
              myElement.style.marginBottom = "-60px";
          }
          settingsUp = !settingsUp;
      }*/
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
    },
    incompleteCount: function() {
      return Visuals.find({ checked: { $ne: false } }).count();
    },
    checkedVisuals: function() {
      if(Visuals.find({ checked: { $ne: false } }).count()>0){
          return true;
      }else{
          return false;
      }
    },
    geniusActive: function () {
      //return "checked";
      console.log("called");
      console.log(Settings.findOne({name: "Genius"}));
      //if(Settings.findOne({name: "Genius"})!=undefined) {
          if (Settings.findOne({name: "Genius"}).geniusActive) {
              console.log("genius true");
              return "unchecked";
          } else {
              return "unchecked";
          }
      //};

      //return "unchecked";
    },
    isGeniusPaused: function() {
      return Settings.findOne({name: "Genius"}).geniusPaused;
    },
    isGeniusActive: function() {
      return Settings.findOne({name: "Genius"}).geniusActive;
    },
    getCountdown: function() {
        var cnt = Settings.findOne({name: "Genius"}).geniusPausedRemain;
        if(cnt>0) {
            return Settings.findOne({name: "Genius"}).geniusPausedRemain;
        }else {
            return "";
        }
    },
      myHelper: function(){
          return Session.get('myMethodResult'); // "bar"
      }
  });


  Template.leaderboard.rendered = function() {
    console.log("Render leaderboard");

      var template = this;
      template.autorun(function () {
          var setting = Settings.findOne({name: "Genius"});
          if(setting != undefined) {
              //console.log(document.getElementsByClassName('switchery').parentNode);
              var list = document.getElementsByClassName("switchery");
              for(var i = list.length - 1; 0 <= i; i--)
                  if(list[i] && list[i].parentElement)
                      list[i].parentElement.removeChild(list[i]);
              var elem = document.querySelector('.genius');
              if (setting.geniusActive) {

                  elem.setAttribute("checked", "checked");
                  elem.removeAttribute("unchecked");
              } else {
                  elem.setAttribute("unchecked", "unchecked");
                  elem.removeAttribute("checked");
              }
              var init = new Switchery(elem, { color: '#6f47a8', jackColor: '#ffffff' });

          }

      });
      /*if (Settings.findOne({name: "Genius"}).geniusActive) {
          console.log("genius true");
          elem.setAttribute("checked","");
          elem.removeAttribute("unchecked");
      } else {
          elem.setAttribute("unchecked","");
          elem.removeAttribute("checked");
      }*/

  };

  Template.leaderboard.events({
      'click .switchery.switchery-default': function() {
          console.log("test");
      },
      'click .switchery.switchery-default': function() {
          // Set the checked property to the opposite of its current value
          console.log("genius events");
          console.log(this._id);
          console.log(this.notification);

          var setting = Settings.findOne({name: "Genius"}).geniusActive;
          Meteor.call('genius', !setting);
      }
  });

  Template.visual.helpers({
    selected: function () {
      return Session.equals("selectedSetting", this._id) ? "selected" : '';
    },
    isGeniusActive: function() {
      return Settings.findOne({name: "Genius"}).geniusActive;
    }
  });

  Template.visual.events({
    'click .toggle-checked': function() {
      // Set the checked property to the opposite of its current value
      console.log("visual events");
      console.log(this._id);
      console.log(this.checked);
      Meteor.call('visual.setChecked', this._id, !this.checked);
    },
    'click .vActive': function() {
      console.log("tester");
      countdown.start();
      Meteor.call('startCountdown');
      Meteor.call('visual.setActive', this._id, true);

    }
  });

  countdown.start(function() {

      // do something when this is completed
    countdown.stop();
    console.log("finished countdown");
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

  Template.visual.rendered = function () {
    counter = 0;
    console.log("call visual");
    visualBol = true;
      //var elem = document.querySelector('.js-switch.notification');
      //var init = new Switchery(elem, { color: '#969696', jackColor: '#ffffff' });
    //console.log(currentID);
  }

  Template.color.rendered = function () {


      console.log("CALLLLED");
      //console.log(this.connection);
      //console.log(colsOld);
      cols = Visuals.findOne(currentID, {fields: {"colors": 1}});
      //console.log(cols.colors[2].color);


          for (i = 0; i < cols.length; i++) {
              if (colsOld[i] != cols[i]) {
                  break;
              }
          }


          //console.log("cols length");
          var count = cols.length;


          //$('#colors').remove('div #colorPicker');
          //$('#colors').append("<div id='colorPicker" + counter + "' class='colorPicker'><a class='color'><div class='colorInner'></div></a><div class='track'></div><ul class='dropdown'><li></li></ul> <input type='hidden' class='colorInput'/></div>");


          //var $picker = document.getElementsByClassName('colorPicker');
          //console.log($picker[counter])
          if (counter < cols.colors.length) {
              console.log("false");
              var col = cols.colors[counter].color;

          } else {
              console.log("true");
              var col = cols[i];
          }
          picker = tinycolorpicker(this.firstNode);
          console.log("COL " + i + " " + counter + " " + cols.colors[counter].color);
          //var col1 = col.color;
          console.log($(".colorNotation").children().get(counter));
      $($($(".colorNotation").children().get(counter)).children().first()).css({"backgroundColor":col});
          picker.setColor(col);
      counter++;

          //console.log(col);
          // visualBol = false;

          colsOld = Visuals.findOne(currentID, {fields: {colors: 1}}).colors;



      /*if(counter<cols.length) {
          counter++;
      }else{
          counter=1;
      }*/
  }

  function keyValue(key, value){
      this.Key = key;
      this.Value = value;
  };

  function updateColors(index){
      var color_array = [];

      //var i = 0;
      //while( (this.firstnode = this.firstnode.previousSibling) != null )
      //    i++;
      console.log("Update Colors");
      console.log(this.firstnode);
      console.log(index);

      for (i = 0; i < $('.colorInput').length; i++) {
          console.log($('input').get(i).getAttribute('value'));
          color_array.push($('input').get(i).getAttribute('value'));
      }
      //Visuals.update(currentID, { $unset: { colors: ""}});
      //Visuals.update(currentID, { $set: { "colors.$.colors": 0} });
      //var sel = colors.+index+.color;
      //console.log(sel);

      var color = $('input').get(index).getAttribute('value');
      Meteor.call('updateColor', currentID, index, color);
      //Visuals.update({_id: currentID, "colors.index": index}, { $set: { "color.$.color": "#000000"}});

      /*var colsOldBuf = cols;
      colsOldBuf = $.unique(colsOldBuf);
      console.log("listpage"+colsOldBuf.length+" "+colsOldBuf.length)
      if(colsOldBuf.length < colsOld.length) {
          //location.reload();
      }*/
  }

  function prevAll(element) {
      var result = [];

      while (element = element.previousElementSibling)
          result.push(element);
      return result;
  }

  Template.listPage.events({
      'click .track': function(event) {
          //var i = 0;
          //while( (event.target.parentElement.parentElement = event.target.parentElement.parentElement.previousSibling) != null )
          //    i++;
          //var i = 0;
          //while((event.target.parentElement.parentElement == event.target.parentElement.parentElement.previousElementSibling) != null)
          //      i++;
          console.log(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
          updateColors(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
      },
      'touchend .track': function() {
          console.log(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
          updateColors(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
      },
      'touchmove .track': function() {
          console.log(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
          updateColors(prevAll(event.target.parentElement.parentElement.parentElement.parentElement).length);
      },
      'click .reset_tab': function() {
          console.log("reset");
          reset();
      },
      'click .info': function() {
          console.log("info");
          var myElement = document.querySelector(".infoOverlay");
          myElement.style.display = "block";
          var myElement2 = document.querySelector(".overlayBG");
          myElement2.style.visibility = "visible";
      },
      'click .close': function() {
          console.log("info");
          var myElement = document.querySelector(".infoOverlay");
          myElement.style.display = "none";
          var myElement2 = document.querySelector(".overlayBG");
          myElement2.style.visibility = "hidden";
      }
  });

  Template.notification.events({
      'click .switchery.switchery-default': function() {
      // Set the checked property to the opposite of its current value
      console.log("notification events");
      console.log(this._id);
      console.log(this.notification);

      Meteor.call('notification.setChecked', this._id, !this.notification);
      }
  })

  Template.notification.rendered = function() {
     var elem = document.querySelector('.js-switch.notification');
     var init = new Switchery(elem, { color: '#969696', jackColor: '#ffffff' });
  }

  Template.listPage.helpers({
      checkIfNotificationExists: function () {
          var visual = Visuals.findOne(currentID);
          if(typeof visual.notification === "undefined"){
              return false;
          }else {
              return true;
          }
      }/*,
      notificationState: function () {
          var visual = Visuals.findOne(currentID);
          console.log("notificationstate");
          console.log(visual.notification);
          return visual.notification;
      }*/
  });

  function sleep(milliseconds) {
      var start = new Date().getTime();
      for (var i = 0; i < 1e7; i++) {
          if ((new Date().getTime() - start) > milliseconds){
              break;
          }
      }
  }

  function reset(){
      Meteor.call('reset', currentID, function(){
          sleep(500);
          location.reload();
      });
  }