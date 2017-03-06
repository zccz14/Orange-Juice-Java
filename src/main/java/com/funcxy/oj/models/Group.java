package com.funcxy.oj.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by aak12 on 2017/3/4.
 */
@Document(collection = "groups")
public class Group {

    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    @Indexed
    @NotNull
    private ObjectId ownerId;
    @Indexed
    @NotBlank(message = "组名为空")
    private String name;

    private List<ObjectId> memberId;
    private List<ObjectId> joiningMemberId;
    private List<ObjectId> ownedProblemList;
    private GroupType type;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectId ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ObjectId> getMemberId() {
        return memberId;
    }

    public void setMemberId(List<ObjectId> memberId) {
        this.memberId = memberId;
    }

    public List<ObjectId> getJoiningMemberId() {
        return joiningMemberId;
    }

    public void setJoiningMemberId(List<ObjectId> joiningMemberId) {
        this.joiningMemberId = joiningMemberId;
    }

    public List<ObjectId> getOwnedProblemList() {
        return ownedProblemList;
    }

    public void setOwnedProblemList(List<ObjectId> ownedProblemList) {
        this.ownedProblemList = ownedProblemList;
    }

    public GroupType getType() {
        return type;
    }

    public void setType(GroupType type) {
        this.type = type;
    }
    public void addMember(ObjectId memberId){
        this.joiningMemberId.remove(joiningMemberId.indexOf(memberId));
        this.memberId.add(memberId);
    }

    public void askJoin(ObjectId joinId){
        this.joiningMemberId.add(joinId);
    }
}
