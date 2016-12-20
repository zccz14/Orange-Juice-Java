const co = require('co');
const mongoose = require('mongoose');
const ObjectId = mongoose.Types.ObjectId;
const express = require('express');
const AccessControl = require('./access_control');
const User = require('../models/user');
const Group = require('../models/group');
const OnError = require('./on_error');


module.exports = express.Router()
    // create a group
    .post('/', AccessControl.signIn)
    .post('/', function(req, res, next) {
        co(function* () {
            var user = req.session.user;
            var name = (req.body.name || '').trim();
            var newGroup = new Group({
                name,
                members: [
                    {
                        userId: user._id,
                        name: user.username,
                        role: 'owner'
                    }
                ]
            });
            newGroup = yield newGroup.save();
            console.log(`new group '${newGroup.name}' created by ${user.username}`);
            user = yield User.findByIdAndUpdate(user._id, {
                $push: {
                    roles: {
                        name: user.username,
                        role: 'owner',
                        group: newGroup._id
                    }
                }
            }).exec();
            req.session.user = user;
            res.json({
                code: 0,
                group: newGroup
            });
        }).catch(OnError(res));
    })


    //delete an existing group
    .delete('/:_id', function(req, res, next) {
        res.json();
    })

    //update an existing group
    //change group name
    .put('/:_id', AccessControl.signIn)
    .put('/:_id', function(req, res, next) {
        co(function* () {
            let user = req.session.user;
            let groupId = req.params._id;
            let newName = (req.body.name || '').trim();
            group = yield Group.update(
                {
                    "_id": new ObjectId(groupId),
                    "members.userId": new ObjectId(user._id),
                    "members.role": 'owner'
                },
                {
                    $set: {
                        "name": newName
                    }
                }
            ).exec();
            if (group.nModified === 1) {
                res.json({
                    code: 0
                });
            } else {
                res.json({
                    code: 11
                });
            }
        }).catch(OnError(res));
    })

    //find a exist group
    .get('/', function(req, res, next) {
        res.json();
    })
    
    // invite some members
    .post('/:_id/invite', AccessControl.signIn)
    .post('/:_id/invite', function(req, res, next) {
        co(function* () {
            let user = req.session.user;
            let groupId = req.params._id;
            let theGroup = yield Group.findById(groupId).exec();
            if (theGroup === null) {
                res.json({ code: 11 });
                return;
            }
            if (!theGroup.members.some(v => v.userId.toString() === user._id && v.role === 'owner')) {
                res.json({ code: 7 });
                return;
            }
            console.log(req.body);
            let members = req.body.members; // Ids
            let existMemberIds = new Set();
            theGroup.members.forEach(v => existMemberIds.add(v.userId));
            members = members.filter(v => !existMemberIds.has(v));
            let WResults = yield members.map(v => User.findByIdAndUpdate(v, {
                $push: {
                    InvitedTo: groupId
                }
            }).exec());
            let result = WResults.map((v, i) => {
                return {
                    userId: members[i],
                    message: v.nMatched === 1 ? 'sent' : 'not found'
                };
            });
            res.json({
                code: 0,
                result
            });
        }).catch(OnError(res));
    })

    // accept some invitations
    .post('/:_id/accept', AccessControl.signIn)
    .post('/:_id/accept', function(req, res, next) {
        co(function* () {
            let user = req.session.user;
            let groupId = req.params._id;
            // [performance]
            // user.InvitedTo is a small array
            if (!user.InvitedTo.some(v => v === groupId)) {
                res.json({ code: 7 });
                return;
            }
            // delete all duplicated invitations
            user.InvitedTo = user.InvitedTo.filter(v => v !== groupId);
            yield [
                Group.findByIdAndUpdate(groupId, {
                    $push: {
                        members: {
                            userId: new ObjectId(user._id),
                            name: user.username,
                            role: 'member'
                        }
                    }
                }).exec(),
                User.findByIdAndUpdate(user._id, {
                    $set: {
                        InvitedTo: user.InvitedTo,
                        roles: {
                            group: groupId,
                            role: 'member',
                            name: user.username
                        }
                    }
                }).exec()
            ];
            res.json({ code: 0 });
        }).catch(OnError(res))
    });