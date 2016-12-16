const co = require('co');
const express = require('express');
const mongoose = require('mongoose');

const Problem = require('../models/problem');

module.exports = express.Router()
    // create a problem
    .post('/', function (req, res, next) {
        co(function* () {
            let newProblem = new Problem({
                title: (req.body.title || '').trim(),
                description: (req.body.description || '').trim(),
            });
            newProblem = yield newProblem.save();
            res.json({ code: 0 });
        }).catch(OnError(res));
    })
    // get problem list
    .get('/', function (req, res, next) {
        co(function* () {
            let problems = yield Problem.find({}).exec();
            res.json({
                code: 0,
                problems
            });
        }).catch(OnError(res));
    })
    // update a problem
    .put('/:_id', function (req, res, next) {
        co(function* () {
            let problem = yield Problem.update(
                {
                    _id: new ObjectId(req.params._id)
                },
                {
                    $set: req.body
                }
            ).exec();
            res.json({ code: 0 });
        }).catch(OnError(res));
    })
    // delete a problem
    .delete('/:_id', function (req, res, next) {
        co(function* () {
            let problem = yield Problem.findByIdAndRemove(req.params._id).exec();
            res.json({ code: 0 });
        }).catch(OnError(res));
    })