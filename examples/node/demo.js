const request = require('request');
const md5 = require('md5');
const fs = require('fs');

function sign(appKey, appSecret, nonstr, timestamp) {
  return md5(`${appKey}:${appSecret}:${nonstr}:${timestamp}`);
}

const now = +new Date;
const nonstr = Math.random().toString(36).substr(2);

const appKey = '<your appKey>';
const appSecret = '<your appSecret>';


const buff = fs.readFileSync('../test.jpg');
const base64data = buff.toString('base64');


request({
  url: 'http://api.manhattan.hexindeu.com/task/detect',
  method: 'post',
  form: {
    type: 'express',
    data: base64data
  },
  headers: {
    'app-timestamp': now,
    'app-key': appKey,
    'app-nonstr': nonstr,
    'app-signature': sign(appKey, appSecret, nonstr, now)
  }
}, function (err, res, body) {
  console.log(err, body);
});