import { Mongo } from 'meteor/mongo';

export const Players = new Mongo.Collection('players');

export const Settings = new Mongo.Collection('settings');
