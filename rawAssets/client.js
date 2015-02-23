/* global document */
"use strict";

require('node-jsx').install({ extension: '.jsx' });
var React  = require('react');
var jsx = require('jsx-loader');
var App = require('./components/App.jsx');
module.exports = function(req, res, next) {
	return React.renderToString(App({}));
}